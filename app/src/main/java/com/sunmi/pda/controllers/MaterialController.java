package com.sunmi.pda.controllers;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;

import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MaterialController {
    protected static final String TAG = MaterialController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();

    public HttpResponse syncData() throws Exception {
        int index = 0;
        int top = 5000;
        int skip = 0;
        boolean isFinished = false;
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material) + app.getString(R.string.url_language_param)+ app.getString(R.string.sap_url_client);
        String lastUpdateDate = AppUtil.getLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_MATERIAL);
        if(StringUtils.isNotEmpty(lastUpdateDate)){
            lastUpdateDate = lastUpdateDate.replace(" ", "T");
            url = url + "&$filter=(LastChangeDate ge datetime'" + lastUpdateDate + "')";
        }
        HttpResponse httpResponse = null;

        while (!isFinished) {
            String urlTop = "";
            if (index == 0) {
                skip = 0;
            } else {
                skip = top * index;
            }
            index = index + 1;
            urlTop += "&$top=" + top + "&$skip=" + skip;
            ;
            httpResponse = sync(url + urlTop);
            isFinished = httpResponse.isFinished();
        }
        return httpResponse;
    }

    private HttpResponse sync(String url)throws Exception{
        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response Code--->" + httpResponse.getCode());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        if(jsonArray != null && jsonArray.size() > 0){
            String now = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
            AppUtil.saveLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_MATERIAL, now);
        }else{
            //httpResponse.setError(app.getString(R.string.text_no_master_data));
            httpResponse.setFinished(true);
        }
        List<Material> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String materialNr = objectI.getString("Material");
                String materialName = objectI.getString("MaterialName");
                String unit = objectI.getString("unit_txt");
                String batchFlag = objectI.getBooleanValue("BatchFlag") == false? "0": "1";
                String serialFlag = objectI.getString("SerialFlag");
                String barcode = objectI.getString("BarCode");
                long creationDate = DateUtils.jsonDateTimeToTimeStamp(objectI.getString("CreationDate"), objectI.getString("CreationTime"));
                long lastChangeDate = DateUtils.jsonDateToTimeStamp(objectI.getString("LastChangeDate"));
                Material material = new Material(materialNr, materialName, unit, batchFlag, serialFlag, barcode, creationDate, lastChangeDate);
                all.add(material);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "syncMaterialData Error ------> " + e.getMessage());
            }
        }
        app.getDBService().getDatabaseServiceMaterial().createData(all);
        return httpResponse;
    }

    public List<Material> getMaterial(String material){
        return app.getDBService().getDatabaseServiceMaterial().getAllData(material);
    }

    public int getMaterialCount(){
        return app.getDBService().getDatabaseServiceMaterial().getMaterialCount();
    }

    public Material getMaterialByKey(String materialNr){
        return app.getDBService().getDatabaseServiceMaterial().getDataByKey(materialNr);
    }

    public Material getMaterialByBarCode(String barCode) {
        return app.getDBService().getDatabaseServiceMaterial().getDataByBarCode(barCode);
    }
}
