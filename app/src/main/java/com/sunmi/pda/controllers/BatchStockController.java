package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.BatchStock;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;

import java.util.ArrayList;
import java.util.List;

public class BatchStockController {
    protected static final String TAG = BatchStockController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();

    public HttpResponse syncData() throws Exception {
        String filter = "$filter=" + app.getString(R.string.filter_language);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_stock) + app.getString(R.string.sap_url_client) + filter ;

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<BatchStock> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String id = objectI.getString("ID");
                String plant = objectI.getString("Plant");
                String material = objectI.getString("Material");
                String batch = objectI.getString("Batch");
                String storageLocation = objectI.getString("StorageLocation");
                String language = objectI.getString("Language");
                String materialText = objectI.getString("MaterialText");
                String plantName = objectI.getString("PlantName");
                String storageLocationName = objectI.getString("StorageLocationName");
                String unit = objectI.getString("Unit");
                double quantity = objectI.getDouble("Quantity");
                BatchStock stock = new BatchStock(id, plant, material, batch, storageLocation, language, materialText, plantName, storageLocationName, unit, quantity ) ;
                all.add(stock);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "fetch batch stock Error ------> " + e.getMessage());
            }
        }

        return httpResponse;
    }

}
