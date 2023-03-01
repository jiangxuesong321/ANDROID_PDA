package com.android.pda.utils;


import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestUtilNew {
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

    public HttpResponse callHttp(String url, int flag, String json, Map<String, String> headers, String userId)
            throws AuthorizationException, GeneralException {

        HttpResponse httpResponse;

        Request request = getRequest(flag, url, headers, userId, json);

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
        if (code == 401) {
            LogUtils.e("HttpRequest", "HttpRequest AuthorizationException ");
            throw new AuthorizationException();
        }

        String token = response.header("x-csrf-token");
        String cookie = "";
        String ifMatchValue = response.header("etag");
        for (String header : response.headers("set-cookie")) {
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
        if (!StringUtils.isEmpty(token)) {
            httpResponse.setToken(token);
        }
        if (!StringUtils.isEmpty(cookie)) {
            httpResponse.setCookie(cookie);
        }
        if (!StringUtils.isEmpty(ifMatchValue)) {
            httpResponse.setIfMatchValue(ifMatchValue);
        }

        return httpResponse;
    }

    private Request getRequest(int flag, String url, Map<String, String> headers, String userId, String json) {

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Request request = null;
        builder.addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        builder.addHeader("Accept", "application/json");
        Login loginInfo = app.getDBService().getDatabaseServiceLogin().getDataByKey(userId);
        builder.addHeader("APIKey", loginInfo.getZuid());

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                System.out.println(entry.getKey() + "---->" + entry.getValue());
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (flag == HTTP_GET_METHOD) {
            request = builder.get().build();
        } else if (flag == HTTP_POST_METHOD) {
            RequestBody body = RequestBody.create(JSON, json);
            request = builder.post(body).build();
        } else {
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


    public HttpResponse login(String userId, String pwd, String urlString) throws GeneralException, AuthorizationException {
        HttpResponse httpResponse;
        Request.Builder builder = new Request.Builder();
        builder.url(urlString);
        String credential = null;
        Map<String, String> headers = new HashMap<>();
        if (userId != null && pwd != null) {
            credential = Credentials.basic(userId, pwd);
        }
        if (credential != null) {
            builder.addHeader("Authorization", credential);
        }
        builder.addHeader("Accept", "application/json");
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                System.out.println(entry.getKey() + "---->" + entry.getValue());
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.get().build();
        Call call = client.newCall(request);
        Response response = null;
        //发送请求
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
        if (code == 401) {
            LogUtils.e("HttpRequest", "HttpRequest AuthorizationException ");
            throw new AuthorizationException();
        }
        String result = null;
        try {
            result = response.body().string();
            httpResponse = new HttpResponse(code, result);
        } catch (IOException e) {
            throw new GeneralException();
        }
        return httpResponse;
    }
}
