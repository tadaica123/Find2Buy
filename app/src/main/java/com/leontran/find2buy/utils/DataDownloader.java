/**
 * File: DataDownloader.java
 * 
 */
package com.leontran.find2buy.utils;

import android.app.Activity;
import android.os.AsyncTask;

import com.leontran.find2buy.networking.MySSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DataDownloader {
	private static final String TAG = "DataDownloader";
	// private static final int POST_COMPLETED_DELAY = 100;
	public static final boolean DEBUG = OGILVYLog.LOCAL_TEST_ON;
	public static final String HTTPMETHOD_GET = "GET";
	public static final String HTTPMETHOD_POST = "POST";
	private static int REQUEST_TIMEOUT = 50000;

	private boolean mExitTasksEarly = false;
	private String jsonListChild = "";
	private byte[][] listAvatar;
	private String userId = "";
	private String accessToken = "";
	protected boolean mPauseWork = false;
	private static String sHTTPMethod = HTTPMETHOD_GET;
	private final Object mPauseWorkLock = new Object();

	// Dual thread executor for main AsyncTask
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "DataDownloader#" + mCount.getAndIncrement());
		}
	};
	public static final Executor DUAL_THREAD_EXECUTOR = Executors
			.newFixedThreadPool(2, sThreadFactory);

	private AsynDownloadTask task = null;
	private OnDownloadCompletedListener listener = null;
	private String mPostFiles;
	private boolean postJosn = false;
	private boolean mHasPostFiles = false;

	private Activity act;
	/**
	 * @Description: setup default method for DataDownloader utility.
	 * @param method
	 *            0 is GET, others is POST
	 */
	public static void setHTTPDefaultMethod(String method) {
		sHTTPMethod = method;
	}

	public DataDownloader(Activity mAct , OnDownloadCompletedListener pListener) {
		listener = pListener;
		mHasPostFiles = false;
		act = mAct;
	}

	public DataDownloader(OnDownloadCompletedListener pListener) {
		listener = pListener;
		mHasPostFiles = false;
	}

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	/**
	 * Setting this to true will signal the working tasks to exit processing at
	 * the next chance. This helps finish up pending work when the activity is
	 * no longer in the foreground and completing the tasks is no longer useful.
	 * 
	 * @param exitTasksEarly
	 */
	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	/**
	 * AsynDownloadTask for download JSON data.<br>
	 * //XXX: if you do need to have a serialized tasks, then you should create
	 * them as in "..params"
	 */
	public class AsynDownloadTask extends AsyncTask<Object, Integer, String> {
		private static final String TAG = "AsynDownloadTask";
		// private static final int POST_COMPLETED_DELAY = 100;

		private Object dataIn;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(Object... params) {
			dataIn = params[0];
			String dataString = String.valueOf(dataIn);

			OGILVYLog.l(0, TAG + "=> doInBackground - starting work with: "
					+ dataString);
			String result = null;

			synchronized (mPauseWorkLock) {
				while (mPauseWork && !isCancelled()) {
					try {
						mPauseWorkLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}
			// process method (as implemented by a subclass)
			if (result == null && !isCancelled() && !mExitTasksEarly) {
				if (postJosn) {
					result = myPostJson(params[0], mPostFiles);
					postJosn = false;
				} else if (!mHasPostFiles) {
					result = processingDownload(params[0]);
				} else {
					if (jsonListChild.trim().length() == 0) {
						result = myPostFile(params[0], mPostFiles);
					} else {
						result = myPostListChilds(params[0]);
					}

					// result = processingPostFile(params[0], mPostFiles);
				}
			}
			// OGILVYLog.l(0, TAG
			// + "=> doInBackground - finished work, result length= "
			// + resultlength);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCancelled() || mExitTasksEarly) {
				result = null;
			}
			OGILVYLog.lf(null, 0, TAG + "=>onPostExecute");
			if (listener != null) {
				listener.onCompleted(dataIn, result);
			}
			super.onPostExecute(result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			synchronized (mPauseWorkLock) {
				mPauseWorkLock.notifyAll();
			}
		}

	}

	public interface OnDownloadCompletedListener {
		void onCompleted(Object key, String result);

		String doInBackgroundDebug(Object... params);
	}

	public String processingDownload(Object key) {
		if (key == null) {
			return null;
		}
		String result = null;
		RequestData reqData = (RequestData) key;
		// check from cache and expired time
		String strURL = reqData.getURLString();
		// I will check


			OGILVYLog.lf(null, 1, TAG + "=> processingDownload: " + strURL);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				if (doRequestToStream(reqData, baos)) {
					if (baos.size() > 0) {
						result = baos.toString("utf-8");
					}
				}
			} catch (UnsupportedEncodingException e) {
				OGILVYLog.lf(null, 4, TAG + "=> UnsupportedEncodingException: "
						+ e.getMessage());
			} catch (Exception e) {
				OGILVYLog.lf(null, 4, TAG + "=> Exception: " + e.getMessage());
			}

		return result;
	}

	public static byte[] readFile(File file) throws IOException {
		// Open file
		RandomAccessFile f = new RandomAccessFile(file, "r");
		try {
			// Get and check length
			long longlength = f.length();
			int length = (int) longlength;
			if (length != longlength)
				throw new IOException("File size >= 2 GB");
			// Read file and return data
			byte[] data = new byte[length];
			f.readFully(data);
			return data;
		} finally {
			f.close();
		}
	}

	public String myPostJson(Object key, String postfiles) {
		RequestData reqData = (RequestData) key;
		DefaultHttpClient httpClient = getNewHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"Android");
		HttpPost httpPost = new HttpPost(reqData.getHost());

		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			multipartEntity.addPart("json_data", new StringBody(postfiles,
					Charset.forName("UTF-8")));

			// ByteArrayBody body = new ByteArrayBody(bao.toByteArray(),
			// "image/jpeg", "picture");
			// entity.addPart("picture", body);
			// Execute POST request.
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// Handle Response
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();

			String resultString = convertStreamToString(inputStream);
			JSONObject resultJson = new JSONObject(resultString);
			return resultJson.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "";
	}

	public String myPostFile(Object key, String postfiles) {
		RequestData reqData = (RequestData) key;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(reqData.getHost());

		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			// Add authentication variables.
			// multipartEntity.addPart("kid_name", new StringBody(
			// mChild.getName(), Charset.forName("UTF-8")));
			// multipartEntity.addPart("height", new
			// StringBody(mChild.getHeight()
			// + ""));
			// multipartEntity.addPart("weight", new
			// StringBody(mChild.getWeight()
			// + ""));
			// multipartEntity.addPart("gender", new
			// StringBody(mChild.isMale()));
			// multipartEntity.addPart("kid_avatar", new StringBody(avatar));

			// ByteArrayBody body = new ByteArrayBody(bao.toByteArray(),
			// "image/jpeg", "picture");
			// entity.addPart("picture", body);
			// Execute POST request.
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// Handle Response
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();

			String resultString = convertStreamToString(inputStream);
			JSONObject resultJson = new JSONObject(resultString);
			return resultJson.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String myPostListChilds(Object key) {
		RequestData reqData = (RequestData) key;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(reqData.getHost());

		MultipartEntity multipartEntity = new MultipartEntity();
		try {
			// Add authentication variables.
			multipartEntity.addPart("member_id", new StringBody(userId));
			multipartEntity.addPart("accesstoken", new StringBody(accessToken));
			multipartEntity.addPart("type", new StringBody("syncChildren"));
			multipartEntity.addPart("children", new StringBody(jsonListChild,
					Charset.forName("UTF-8")));
			for (int i = 0; i < listAvatar.length; i++) {
				if (listAvatar[i] == null) {
					listAvatar[i] = new byte[0];
				}
				ByteArrayBody bab = new ByteArrayBody(listAvatar[i], "image"
						+ i + ".jpg");
				multipartEntity.addPart("avatar" + (i + 1), bab);
			}

			// ByteArrayBody body = new ByteArrayBody(bao.toByteArray(),
			// "image/jpeg", "picture");
			// entity.addPart("picture", body);
			// Execute POST request.
			httpPost.setEntity(multipartEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// Handle Response
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream inputStream = httpEntity.getContent();

			String resultString = convertStreamToString(inputStream);
			JSONObject resultJson = new JSONObject(resultString);
			return resultJson.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	private static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				4096);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private boolean doRequestToStream(RequestData key, OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(key.host);
			urlConnection = (HttpURLConnection) url.openConnection();
			if (HTTPMETHOD_POST.equalsIgnoreCase(sHTTPMethod)) {
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod(HTTPMETHOD_POST);
				urlConnection.setReadTimeout(REQUEST_TIMEOUT);
				OutputStream os = urlConnection.getOutputStream();
				os.write(key.params.getBytes("utf-8"));
				os.flush();
				os.close();
			}

			in = new BufferedInputStream(urlConnection.getInputStream());
			out = new BufferedOutputStream(outputStream);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			out.flush();
			return true;
		} catch (final IOException e) {
			OGILVYLog.l(4, TAG + "=> Error in doRequestToStream - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				OGILVYLog.l(4, TAG + "=> Error in closing connection - " + e);
			}
		}
		return false;
	}

	/**
	 * Download a JSON String from a URL and write the content to an output
	 * stream.
	 * 
	 * @param urlString
	 *            The URL to fetch
	 * @param outputStream
	 *            The outputStream to write to
	 * @return true if successful, false otherwise
	 */
	public boolean downloadUrlToStream(String urlString,
			OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream());
			out = new BufferedOutputStream(outputStream);

			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			out.flush();
			return true;
		} catch (final IOException e) {
			OGILVYLog.l(4, TAG + "=> Error in download - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return false;
	}

	public void addDownload(RequestData contentData) {

		task = new AsynDownloadTask();
		mHasPostFiles = false;
		if (UIUtils.hasHoneycomb()) {
			task.executeOnExecutor(DUAL_THREAD_EXECUTOR, contentData);
		} else {
			task.execute(contentData);
		}
	}

	public void ExitTask() {
		postJosn = false;
		if (task != null) {
			task.cancel(true);
			task = null;
		}
	}

	public void addDownload(boolean forceUpdate, RequestData contentData) {
		if (contentData != null) {
			contentData.forceUpdate = forceUpdate;
		}
		addDownload(contentData);
	}

	public void addDownload(RequestData... contentDatas) {
		if (contentDatas == null || contentDatas.length < 1) {
			return;
		}
		postJosn = false;
		task = new AsynDownloadTask();
		if (UIUtils.hasHoneycomb()) {
			task.executeOnExecutor(DUAL_THREAD_EXECUTOR,
					(Object[]) contentDatas);
		} else {
			task.execute((Object[]) contentDatas);
		}
	}

	public void addPOSTJson(RequestData contentData, String json) {

			mPostFiles = json;
			postJosn = true;
			task = new AsynDownloadTask();
			if (UIUtils.hasHoneycomb()) {
				task.executeOnExecutor(DUAL_THREAD_EXECUTOR, contentData);
			} else {
				task.execute(contentData);
			}


	}

	public void addPOSTJson(Activity actNow, final  RequestData contentData, final String json) {
		act = actNow;
		if (act != null){
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mPostFiles = json;
					postJosn = true;
					task = new AsynDownloadTask();
					if (UIUtils.hasHoneycomb()) {
						task.executeOnExecutor(DUAL_THREAD_EXECUTOR, contentData);
					} else {
						task.execute(contentData);
					}
				}
			});
		}
	}

	public void addSyncChildren(RequestData contentData, String json,
			byte[][] dataAvatar, String userId, String accessToken) {
		jsonListChild = json;
		this.userId = userId;
		postJosn = false;
		this.accessToken = accessToken;
		listAvatar = dataAvatar;
		mHasPostFiles = true;
		task = new AsynDownloadTask();
		if (UIUtils.hasHoneycomb()) {
			task.executeOnExecutor(DUAL_THREAD_EXECUTOR, contentData);
		} else {
			task.execute(contentData);
		}
	}

	public DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}
