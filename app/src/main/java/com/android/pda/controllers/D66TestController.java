package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.MaterialInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

public class D66TestController {
    protected static final String TAG = D66TestController.class.getSimpleName();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public List<MaterialInfo> getMaterialList(String material) throws Exception {

        List<MaterialInfo> materialInfosList = new ArrayList<>();
        OkHttpClient clientToken = new OkHttpClient.Builder().connectTimeout(1800L, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
                .readTimeout(1800L, TimeUnit.MILLISECONDS).build();
        //获取csrftoken
        String urlCookie = "http://112.103.135.105:8081/sap/opu/odata/sap/ZCL_ZTEST_ATOM_SRV/?$format=json";
        Map<String, String> cookieInfo = getToken(clientToken, urlCookie);

        LogUtils.d(TAG, "cookieInfo--->" + cookieInfo);
        //获取业务数据
        //组装body体
        Map<String, Object> bodys = new HashMap<>();
        bodys.put("FCode", "ZFM_WM_RF01_GET");
        bodys.put("ReturnMessage", "");

        JSONArray ja = new JSONArray();
        JSONObject job = new JSONObject();
        JSONObject param = new JSONObject();
        param.put("matnr",material);
        job.put("Input", JSONObject.toJSONString(param));
        job.put("Output", "");
        ja.add(job);
        bodys.put("np_expand", ja);
        //创建client
        String credential = Credentials.basic("dlw_atom", "Admin@123");
        Request.Builder builder = new Request.Builder();
        String url = "http://112.103.135.105:8081/sap/opu/odata/sap/ZCL_ZTEST_ATOM_SRV/requestSet";
        builder.url(url);
        if (credential != null) {
            builder.addHeader("Authorization", credential);
        }
        builder.addHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
        builder.addHeader("Accept", "application/json");
//        builder.addHeader("Cookie", "ApplicationGatewayAffinityCORS=fd8cdebd503d5ba81352b643c6b2b0b0dd270b53be94fc932ea5bc98b36f3367;");
        builder.addHeader("X-CSRF-TOKEN", cookieInfo.get("x-csrf-token") + "");
        builder.addHeader("Cookie", cookieInfo.get("Cookie") + "");
//        if (headers != null) {
//            for (Map.Entry<String, String> entry : headers.entrySet()) {
//                System.out.println(entry.getKey() + "---->" + entry.getValue());
//                builder.addHeader(entry.getKey(), entry.getValue());
//            }
//        }
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(1800L, TimeUnit.MILLISECONDS).retryOnConnectionFailure(true)
                .readTimeout(1800L, TimeUnit.MILLISECONDS).build();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSONString(bodys));
        System.out.println("body体"+JSONObject.toJSONString(bodys));
        Request request = builder.post(body).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        int code = response.code();
        if (code == 201 || code == 200) {
            String result = response.body().string();
            HttpResponse httpResponse = new HttpResponse(code, result);
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject jsond = JSONObject.parseObject(JSONObject.toJSONString(jsonObject.get("d")));
            JSONObject jsonExpand =  JSONObject.parseObject(JSONObject.toJSONString(jsond.get("np_expand")));
            JSONArray jsonResults = JSONArray.parseArray(JSONObject.toJSONString(jsonExpand.get("results")));
            JSONObject jsonResults0 =  JSONObject.parseObject(JSONObject.toJSONString(jsonResults.get(0)));
            String str = JSONObject.toJSONString(jsonResults0.get("Output"));
            System.out.println("str:" + str);
//            JSONArray  jaList = JSONArray.parseArray(jsonResults0.get("Output").toString());
            materialInfosList = JSONArray.parseArray(jsonResults0.get("Output").toString(),MaterialInfo.class);


//            materialInfosList = JSONArray.parseArray(str,MaterialInfo.class);
            System.out.println("result" + materialInfosList);
        }

        return materialInfosList;
    }

    public Map<String, String> getToken(OkHttpClient client, String urlString) throws Exception {

        Map<String, String> header = new HashMap<>();
        header.put("x-csrf-token", "fetch");
        String credential = Credentials.basic("dlw_atom", "Admin@123");
        header.put("Authorization", credential);
        Request.Builder builder = new Request.Builder();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                System.out.println(entry.getKey() + "---->" + entry.getValue());
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        builder.url(urlString);
        Request request = builder.get().build();
        Call call = client.newCall(request);
        Response response = call.execute();
        LogUtils.d(TAG, "response--->" + response);
        String cookie = "";
        for (String headerParam : response.headers("set-cookie")) {
            if (headerParam.contains("SAP_SESSIONID_D66")){
                cookie = headerParam;
            }
//            if (StringUtils.isNotEmpty(cookie)){
//                cookie = cookie.split(";")[0] + ";";
//            }
        }
        int code = response.code();
        String result = response.body().string();
        HttpResponse httpResponse = new HttpResponse(code, result);
        if (!StringUtils.isEmpty(cookie)) {
            httpResponse.setCookie(cookie);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("x-csrf-token", response.headers().get("x-csrf-token"));
        headers.put("Cookie", httpResponse.getCookie());

        return headers;
    }
}
