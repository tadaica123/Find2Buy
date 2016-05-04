/**
 * File: RequestData.java
 * Creator: Duy
 * 
 */
package com.leontran.find2buy.utils;



public class RequestData extends Object {

	protected String host;
	public String params;
	public int type;
	public String method;
	public boolean forceUpdate;

	/**
	 * 04-12-2012
	 */
	protected RequestData() {
		this("", 0);
	}

	public RequestData(String inParams, int pType) {
		host = "";
		params = inParams;
		type = pType;
		forceUpdate = false;
	}

	public RequestData(String inHost, String api, String inParams, int pType) {
		host = inHost + api;
		params = inParams;
		type = pType;
		forceUpdate = false;
	}
	
	public RequestData(String inHost, String inParams, int pType) {
		host = inHost ;
		params = inParams;
		type = pType;
		forceUpdate = false;
	}

	public RequestData(String inHost, String api, String inParams, int pType,
			String pmethod) {
		host = inHost + api;
		type = pType;
		forceUpdate = false;
		if (inParams == null) {
			params = "";
		} else {
			params = inParams;
		}
		this.method = pmethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getURLString();
	}

	public String getHost() {
		if (host != null) {
			return host;
		}
		return "";
	}

	public String getURLString() {
		// try {
		// return host + "?" + URLEncoder.encode(params, "utf-8");
		// } catch (UnsupportedEncodingException e) {
		// GKIMLog.lf(null, 4,
		// "RequestData=>getUrlString UnsupportedEncodingException: "
		// + e.getMessage());
		// }
		return host + "?" + params;
	}
}
