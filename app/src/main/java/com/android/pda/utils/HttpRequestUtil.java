package com.android.pda.utils;


import com.android.pda.application.AndroidApplication;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

public class HttpRequestUtil {
	public static final int HTTP_DELETE_METHOD = 0;
	public static final int HTTP_POST_METHOD = 1; // requestHttpMethod
	public static final int HTTP_GET_METHOD = 2; // requestHttpMethod
	public static final int HTTP_PATCH_METHOD = 3; // requestHttpMethod
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_VALUE = "application/json";

	private final static AndroidApplication app = AndroidApplication.getInstance();
	static public OkHttpClient client;
	private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	static {
		client = new OkHttpClient.Builder().connectTimeout(180000L, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
				.readTimeout(180000L, TimeUnit.MILLISECONDS).build();

	}

	public enum State {SUCCESS, FAILURE, NETWORK_FAILURE}

	public HttpResponse callHttp(String url, int flag, String json, Map<String, String> headers)
			throws AuthorizationException, GeneralException {

		HttpResponse httpResponse;
		String credential = null;
		String username = app.getOdataService().getUserName();
		String pwd = app.getOdataService().getPassword();
		if (username != null && pwd != null) {
			credential = Credentials.basic(username, pwd);
		}

		Request request = getRequest(flag, url, headers, credential, json);

		Call call = client.newCall(request);


		Response response = null;
		try {
			response = call.execute();
		} catch (Exception e) {
			LogUtils.e("HttpRequest", "HttpRequest Error ------> " + e.getMessage());
			throw new GeneralException();
		}
		int code = response.code();
		if (code == 510 || code == 500) {
			throw new GeneralException();
		}
		if(code == 401){
			LogUtils.e("HttpRequest", "HttpRequest AuthorizationException " );
			throw new AuthorizationException();
		}

		String token = response.header("x-csrf-token");
		String cookie = "";
		String ifMatchValue = response.header("etag");
		for(String header : response.headers("set-cookie")){
			cookie = cookie + header + ";";
		}
		/*if(response.headers("set-cookie").size() > 1){
			cookie = response.headers("set-cookie").get(1);
		}*/
		//System.out.println("ifMatchValue---->" + ifMatchValue);
		//System.out.println("cookie---->" + cookie);
		String result = null;
		try {
			result = response.body().string();
		} catch (IOException e) {
			throw new GeneralException();
		}
		//System.out.println("result---->" + result);
		httpResponse = new HttpResponse(code, result);
		if(!StringUtils.isEmpty(token)){
			httpResponse.setToken(token);
		}
		if(!StringUtils.isEmpty(cookie)){
			httpResponse.setCookie(cookie);
		}
		if(!StringUtils.isEmpty(ifMatchValue)){
			httpResponse.setIfMatchValue(ifMatchValue);
		}

		return httpResponse;
	}

	private Request getRequest(int flag, String url, Map<String, String> headers, String credential, String json) {

		Request.Builder builder = new Request.Builder();
		builder.url(url);
		Request request = null;
		if (credential != null) {
			builder.addHeader("Authorization", credential);
		}
		builder.addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		builder.addHeader("Accept", "application/json");
		builder.addHeader("Cookie", "ApplicationGatewayAffinityCORS=fd8cdebd503d5ba81352b643c6b2b0b0dd270b53be94fc932ea5bc98b36f3367;");

		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				System.out.println(entry.getKey() + "---->" + entry.getValue());
				builder.addHeader(entry.getKey(), entry.getValue());
			}
		}
		if (flag == HTTP_GET_METHOD) {
			request = builder.get().build();
		} else if(flag == HTTP_POST_METHOD){
			RequestBody body = RequestBody.create(JSON, json);
			request = builder.post(body).build();
		}else{
			RequestBody body = RequestBody.create(JSON, json);
			request = builder.patch(body).build();
		}
		return request;
	}



	public static void main(String args[]) {

	}
	private ResultCallback resultCallback;
	
	public void setResultCallback(ResultCallback resultCallback) {
		this.resultCallback = resultCallback;
	}
	
    public interface ResultCallback {
        void onCallBack(State state, String result, int index, int code);
    }

	public Map<String, String> getToken(String urlString) throws GeneralException, AuthorizationException {

		HttpRequestUtil http = new HttpRequestUtil();
		Map<String, String> headers = new HashMap<>();
		headers.put("x-csrf-token", "fetch");
		urlString = urlString + "&$filter=(Material eq '123')";
		//System.out.println("urlString" + urlString);
		//urlString = "https://sapqas.sunmi.com/sap/opu/odata/sap/API_MATERIAL_DOCUMENT_SRV/A_MaterialDocumentHeader?sap-client=100";
		HttpResponse httpResponse = http.callHttp(urlString, HttpRequestUtil.HTTP_GET_METHOD, null, headers);

		headers = new HashMap<>();
		//System.out.println("x-csrf-token---->" + httpResponse.getToken());
		//System.out.println("Cookie---->" + httpResponse.getCookie());
		headers.put("x-csrf-token", httpResponse.getToken());
		headers.put("Cookie", httpResponse.getCookie());
		return headers;
	}
}
