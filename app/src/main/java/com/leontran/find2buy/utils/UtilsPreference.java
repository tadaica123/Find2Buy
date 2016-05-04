package com.leontran.find2buy.utils;

import android.content.Context;
import android.os.Handler;

import java.util.Hashtable;

public class UtilsPreference {

	private static final String TAG = "UtilsPreference";
	public static final String EMPTY_STRING = "";
	protected static final long CONST_DELAY_MESSAGE = 200;

	// it should be application context
	private Context mApplicationContext = null;
	private Hashtable<String, Handler> mObserver = new Hashtable<String, Handler>();
	private SecurePreferences preferences;
	/**
	 * Listener object for changing in the SharedPreferences. It just for
	 * logging
	 */
	private boolean mHasListened = false;

	/**
	 * Jun 12, 2012
	 */
	public UtilsPreference(Context mContext) {
		preferences = new SecurePreferences(mContext, "xyztal", "kdsjfb238lsksdjkf", true);
		this.mApplicationContext = mContext.getApplicationContext();
	}

	public boolean hasKey(String prefName) {
		if (preferences.containsKey(prefName)) {
			return true;
		}
		return false;
	}


	public String getStringPref(String prefName) {
		if (preferences.containsKey(prefName)) {
			return preferences.getString(prefName);
		}
		return EMPTY_STRING;
	}

	/**
	 * @Description: Give String data into a Application's sharepreference
	 *               object.
	 * @param prefName
	 * @param value
	 */
	public void setStringPref(String prefName, String value) {
		preferences.put(prefName, value);
	}


	public int getIntPref(String prefName) {
		if (preferences.containsKey(prefName)) {
			return preferences.getInt(prefName);
		}
		return -1;
	}

	public void setIntPref(String prefName, int value) {
		preferences.put(prefName, value);
	}

	public float getFloatPref(String prefName) {
		if (preferences.containsKey(prefName)) {
			return preferences.getFloat(prefName);
		}
		return -1;
	}

	public void setFloatPref(String prefName, float value) {
		preferences.put(prefName, value);
	}


	public boolean getBooleanPref(String prefName) {
		if (preferences.containsKey(prefName)) {
			return preferences.getBool(prefName);
		}
		return false;
	}

	public void setBooleanPref(String prefName, boolean value) {
		preferences.put(prefName, value);
	}
	
	public void clear(){
		preferences.clear();
	}

}
