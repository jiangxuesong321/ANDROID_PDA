package com.android.pda.models;

public class HttpResponse {
	private int code;
	private String responseString;
	private String error;
	private String token;
	private String cookie;
	private String ifMatchValue;
	private boolean finished;
	public HttpResponse(int code, String responseString) {
		this.code = code;
		this.responseString = responseString;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getIfMatchValue() {
		return ifMatchValue;
	}

	public void setIfMatchValue(String ifMatchValue) {
		this.ifMatchValue = ifMatchValue;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public static void main(String args[]) {
		
	}
	
}
