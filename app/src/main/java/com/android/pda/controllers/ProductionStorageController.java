package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.POStorageQuery;
import com.android.pda.models.ProductionStorageQuery;
import com.android.pda.utils.Algorithm;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

public class ProductionStorageController {
    protected static final String TAG = ProductionStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    public static String getMaterialDocumentYear(ProductionStorageQuery query) {
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        String materialDocument = query.getMaterialDocument();
        String api = "?$format=json&$filter=MaterialDocument eq " + "'" + materialDocument + "'";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_doc_specific) + api;
        LogUtils.e(TAG, "MaterialDocument Request URL --------->" + url);
        try {
            String msgtxt = "";
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
            // 解析 http 的返回结果
            String result = "";
            if (httpResponse != null && httpResponse.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));

                return jsonObject.getString("MaterialDocumentYear");
            } else {
                result = "E";
            }
            // 如果 http 无响应的情况
            if (StringUtils.isEmpty(result)) {
                result = "";
            }
            if (StringUtils.equalsIgnoreCase(result, "S")) {
                return "";
            } else if (StringUtils.equalsIgnoreCase(result, "E")) {
                if (msgtxt != null) {
                    return msgtxt;
                } else {
                    return app.getString(R.string.login_failed);
                }
            } else {
                return app.getString(R.string.text_service_failed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Get MaterialDocumentYear Error ------> " + e.getMessage());
            return app.getString(R.string.text_service_failed);
        }
    }

    /**
     * 验证查询参数：物料凭证号
     *
     * @param query
     * @return
     */
    public String verifyQuery(ProductionStorageQuery query) {
        if (query != null) {
            if (StringUtils.isEmpty(query.getMaterialDocument())) {
                return app.getString(R.string.text_input_material_doc_num);
            }
            // TODO: verify the digit
        }
        return null;
    }

}
