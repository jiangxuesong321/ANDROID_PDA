package com.android.pda.utils;


import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;

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

public class HttpRequestUtil {
    public static final int HTTP_DELETE_METHOD = 0;
    public static final int HTTP_POST_METHOD = 1; // requestHttpMethod
    public static final int HTTP_GET_METHOD = 2; // requestHttpMethod
    public static final int HTTP_PATCH_METHOD = 3; // requestHttpMethod
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    protected static final String TAG = HttpRequestUtil.class.getSimpleName();

    private final static AndroidApplication app = AndroidApplication.getInstance();
    static public OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json");

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
        if (code == 401) {
            LogUtils.e("HttpRequest", "HttpRequest AuthorizationException ");
            throw new AuthorizationException();
        }

//        String token = response.header("x-csrf-token");
//        String cookie = "";
//        String ifMatchValue = response.header("etag");
//        for (String header : response.headers("set-cookie")) {
//            cookie = cookie + header + ";";
//        }
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
//        if (!StringUtils.isEmpty(token)) {
//            httpResponse.setToken(token);
//        }
//        if (!StringUtils.isEmpty(cookie)) {
//            httpResponse.setCookie(cookie);
//        }
//        if (!StringUtils.isEmpty(ifMatchValue)) {
//            httpResponse.setIfMatchValue(ifMatchValue);
//        }

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
        builder.addHeader("Cookie", "SAP_SESSIONID_T9F_100=a4u7z-qbE8ueg2dcX8SHJ7Nb5MW-5BHtg6kAFj4WEzw%3d; sap-usercontext=sap-client=100;");
        if (flag == HTTP_POST_METHOD) {
            try {
                String csrfToken = getCsrfToken();
                builder.addHeader("x-csrf-token", csrfToken);
                System.out.println("post 获取的token是:" + csrfToken);
            } catch (Exception e) {
                LogUtils.e(TAG, "get csrfToken error" + e.getMessage());
                e.printStackTrace();
            }
        }
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

    public String getCsrfToken() throws Exception {
        String csrfToken = "";
        OkHttpClient clientCookie = new OkHttpClient.Builder().connectTimeout(1800L, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
                .readTimeout(1800L, TimeUnit.MILLISECONDS).build();
        String urlString = app.getOdataService().getHost() + app.getString(R.string.sap_url_login);
        Map<String, String> header = new HashMap<>();
        header.put("x-csrf-token", "fetch");
        header.put("Accept", "application/json");
        header.put("Connection", "keep-alive");
        header.put("Cookie", "SAP_SESSIONID_T9F_100=a4u7z-qbE8ueg2dcX8SHJ7Nb5MW-5BHtg6kAFj4WEzw%3d; sap-usercontext=sap-client=100;");
        String username = app.getOdataService().getUserName();
        String pwd = app.getOdataService().getPassword();
        if (username != null && pwd != null) {
            String credential = Credentials.basic(username, pwd);
            header.put("Authorization", credential);
        }
        Request.Builder builder = new Request.Builder();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + "---->" + entry.getValue());
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        builder.url(urlString);
        Request request = builder.get().build();
        Call call = clientCookie.newCall(request);
        Response response = call.execute();
        int code = response.code();
        String cookie = "";
        for (String header1 : response.headers("set-cookie")) {
            cookie = cookie + header1 + ";";
        }
        System.out.println("cookie" + cookie);
        if (response != null && response.headers() != null) {
            csrfToken = response.headers().get("x-csrf-token");
        }


        return csrfToken;
    }

    public Map<String, String> getIfMatch(String url) throws Exception {
        Map<String, String> map = new HashMap<>();
        OkHttpClient clientCookie = new OkHttpClient.Builder().connectTimeout(1800L, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
                .readTimeout(1800L, TimeUnit.MILLISECONDS).build();
        String urlString = url;
        Map<String, String> header = new HashMap<>();
        header.put("x-csrf-token", "fetch");
        header.put("Accept", "application/json");
        header.put("Connection", "keep-alive");
        header.put("Cookie", "SAP_SESSIONID_T9F_100=a4u7z-qbE8ueg2dcX8SHJ7Nb5MW-5BHtg6kAFj4WEzw%3d; sap-usercontext=sap-client=100;");
        String username = app.getOdataService().getUserName();
        String pwd = app.getOdataService().getPassword();
        if (username != null && pwd != null) {
            String credential = Credentials.basic(username, pwd);
            header.put("Authorization", credential);
        }
        Request.Builder builder = new Request.Builder();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + "---->" + entry.getValue());
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        builder.url(urlString);
        Request request = builder.get().build();
        Call call = clientCookie.newCall(request);
        Response response = call.execute();
        int code = response.code();
        if (code == 200) {
            String cookie = "";
            for (String header1 : response.headers("set-cookie")) {
                cookie = cookie + header1 + ";";
            }
            System.out.println("cookie" + cookie);
            if (response != null && response.headers() != null) {
                map.put("x-csrf-token", response.headers().get("x-csrf-token"));
                map.put("if-match", response.headers().get("etag"));
            }

        }
        return map;
    }
}
