/**
 * File: Utils.java
 * 
 */
package com.leontran.themobilevn.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.ObjectByteArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 */
public class Utils {
	public static byte[] raw = null;
	private static final String TAG = "Utils";
	private Context applicationContext = null;
	private UtilsPreference mPrefUtil = null;

	public Utils(Context context) {
		raw = context.getResources()
				.getString(R.string.test_raw)
				.getBytes();
		applicationContext = context.getApplicationContext();
		mPrefUtil = new UtilsPreference(context);
	}

	public Context getApplicationContext() {
		return applicationContext;
	}

	public Resources getResource() {
		return applicationContext.getResources();
	}

	@SuppressWarnings("unused")
	private void setContext(Context context) {
		applicationContext = context.getApplicationContext();
		if (mPrefUtil != null) {
			mPrefUtil = null;
		}
		mPrefUtil = new UtilsPreference(context);
	}

	public boolean isConnectionAvailable() {
		if (applicationContext == null) {
			return false;
		}
		ConnectivityManager cm = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null
		// otherwise check if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public String getJSONStringFromAssetFile(String testFilename) {
		if (applicationContext == null) {
			return null;
		}
		String result = null;
		StringBuffer sb = new StringBuffer();
		String line;
		try {
			AssetManager am = applicationContext.getAssets();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					am.open(testFilename), "utf-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (IOException e) {
			OGILVYLog.lf(null, 4, TAG + "=> IOException: " + e.getMessage());
		}
		return result;
	}

	public UtilsPreference getUtilPreference() {
		if (mPrefUtil == null && applicationContext != null) {
			mPrefUtil = new UtilsPreference(applicationContext);
		}
		return mPrefUtil;
	}

	public boolean hasKey(String key) {
		if (mPrefUtil != null) {
			return mPrefUtil.hasKey(key);
		}
		return false;
	}


	public int getIntPref(String prefName) {
		if (mPrefUtil != null && mPrefUtil.hasKey(prefName)) {
			return mPrefUtil.getIntPref(prefName);
		}
		return -1;
	}

	public float getFloatPref(String prefName) {
		if (mPrefUtil != null && mPrefUtil.hasKey(prefName)) {
			return mPrefUtil.getFloatPref(prefName);
		}
		return -1;
	}

	public boolean getBooleanPref(String prefName) {
		if (mPrefUtil != null && mPrefUtil.hasKey(prefName)) {
			return mPrefUtil.getBooleanPref(prefName);
		}
		return false;
	}

	public InputStream getInputStream(final String urlString) {
		if (isConnectionAvailable()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						URL url = new URL(urlString);
						url.getContent();
					} catch (Exception e) {
						OGILVYLog.lf(null, 4, TAG
								+ "=> failed to getInputStream: " + urlString
								+ " because: " + e.getMessage());
					}
				}
			}).start();

		}
		return null;
	}

	public void setIntPref(String prefKeyname, int size) {
		if (mPrefUtil != null) {
			mPrefUtil.setIntPref(prefKeyname, size);
		}
	}

	public void setBoolPref(String prefKeyname, boolean state) {
		if (mPrefUtil != null) {
			mPrefUtil.setBooleanPref(prefKeyname, state);
		}
	}

	public void setFloatPref(String prefKeyname, float size) {
		if (mPrefUtil != null) {
			mPrefUtil.setFloatPref(prefKeyname, size);
		}
	}


	public void clear(){
		mPrefUtil.clear();
	}

	public String getDeviceIMEI() {
		if (applicationContext != null) {
			TelephonyManager tm = (TelephonyManager) applicationContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			if (imei != null) {
				return imei;
			}
		}
		return "";
	}

	public static String arrayToString(String[] items, String seperator) {
		if ((items == null) || (items.length == 0)) {
			return "";
		} else {
			StringBuffer buffer = new StringBuffer(items[0]);
			for (int i = 1; i < items.length; i++) {
				buffer.append(seperator);
				buffer.append(items[i]);
			}
			return buffer.toString();
		}
	}


	public static byte[] encryptMsg(String message, SecretKey secret) {
		/* Encrypt the message. */
		try {

			Cipher cipher = null;
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secret);
			byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
			return cipherText;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;

	}

	public static String decryptMsg(byte[] cipherText, SecretKey secret) {

		/*
		 * Decrypt the message, given derived encContentValues and
		 * initialization vector.
		 */
		try {

			Cipher cipher = null;
			cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secret);
			String decryptString = new String(cipher.doFinal(cipherText),
					"UTF-8");
			return decryptString;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public static String parserURL(String param) {
		return param.replace(" ", "%20");
	}

	public void setStringPref(String prefKeyname, String value) {
		if (mPrefUtil != null) {
			ObjectByteArray data = new ObjectByteArray();
			data.setData(Utils.encryptMsg(value, Utils.generateKey()));
			mPrefUtil.setStringPref(prefKeyname, MPreferenceManager
					.getJsonFromObject(data));
		}
	}

	public static SecretKey generateKey() {
		return new SecretKeySpec(raw, "AES");
	}

	public String getStringPref(String prefName) {
		if (mPrefUtil != null && mPrefUtil.hasKey(prefName)) {
			String json = mPrefUtil.getStringPref(prefName);
			ObjectByteArray data = MPreferenceManager.getClassFromJson(json,
					ObjectByteArray.class);
			json = Utils.decryptMsg(data.getData(), Utils.generateKey());
			data = null;
			return json;
		}
		return "";
	}
}
