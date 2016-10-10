/**
 * File: DataDownloader.java
 * 
 */
package com.leontran.themobilevn.utils;

import android.os.AsyncTask;

import com.leontran.themobilevn.networking.MySSLSocketFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DataDownloader {

	public static final String HTTPMETHOD_POST = "POST";
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;
	private boolean mExitTasksEarly = false;

	protected boolean mPauseWork = false;
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
	private int typeRequest = 1;

	public DataDownloader(OnDownloadCompletedListener pListener) {
		listener = pListener;
	}



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
					if (typeRequest == METHOD_GET){
						result = myGetJson(params[0]);
					} else {
						result = myPostJson(params[0], mPostFiles);
					}
					postJosn = false;
				}
			}
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

	public String myGetJson(Object key) {
		RequestData reqData = (RequestData) key;
		DefaultHttpClient httpClient = getNewHttpClient();

		HttpGet httpGet = new HttpGet(reqData.toString());
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);

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

	public void ExitTask() {
		postJosn = false;
		if (task != null) {
			task.cancel(true);
			task = null;
		}
	}

	public void addRequestJson(RequestData contentData, String json, int type) {
			typeRequest = type;
			mPostFiles = json;
			postJosn = true;
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
