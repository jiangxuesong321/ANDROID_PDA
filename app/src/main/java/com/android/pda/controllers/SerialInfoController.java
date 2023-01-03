package com.android.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.log.LogUtils;
import com.android.pda.models.GeneralMaterialDocumentItemResults;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.SerialInfo;
import com.android.pda.models.SerialNumber;
import com.android.pda.models.SerialNumberResults;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SerialInfoController {
    protected static final String TAG = SerialInfoController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    public final static int ERROR_REPEAT_SCAN = 0;
    public final static int ERROR_MAX_COUNT = 1;


    public List<SerialInfo> syncData(List<SerialNumberResults> serialNumberResults ) throws Exception {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_serialInfo) + app.getString(R.string.sap_url_client);
        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();

        String arrStr = JSON.toJSONString(serialNumberResults);
        String json = "{ \"data\": " + arrStr + " }";
        LogUtils.d("Serial Query ", "POST--->" + json);

        Map<String, String> headers = new HashMap<>();
        headers.put(app.getString(R.string.header_language_key), app.getString(R.string.header_language_value));
        LogUtils.d("Serial Query ", "Header--->" + headers);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, headers);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        List<SerialInfo> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String plant = objectI.getString("plant");
                String material = objectI.getString("material");

                String batch = objectI.getString("batch");
                String serialnumber = objectI.getString("serialnumber");
                String storagelocation = objectI.getString("storagelocation");
                String materialdes = objectI.getString("materialdes");
                String plantdes = objectI.getString("plantdes");
                String storagelocdes = objectI.getString("storagelocdes");
                String quantityinentryunit = objectI.getString("quantityinentryunit");
                String serialflag = objectI.getString("serialflag");
                SerialInfo serialInfo = new SerialInfo(plant, material, materialdes, batch, serialnumber,
                        storagelocation, plantdes, storagelocdes, quantityinentryunit, serialflag);
                all.add(serialInfo);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "Query Serial Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public List<GeneralMaterialDocumentItemResults> getItems(List<SerialInfo> serialInfos){

        List<GeneralMaterialDocumentItemResults> items = new ArrayList<>();
        //TEST DATA
        /*SerialInfo serialInfo = new SerialInfo("1100", "C04000026", "80mm Kitchen Cloud Printer",  "2021083001", "AB0200003", "1003");
        serialInfos.add(serialInfo);

        SerialInfo serialInfo2 = new SerialInfo("1100", "C04000026","80mm Kitchen Cloud Printer",  "2021083002", "AB0200003", "1003");
        serialInfos.add(serialInfo2);

        SerialInfo serialInfo3 = new SerialInfo("1100", "C04000027","80mm Kitchen Cloud Printer",  "2021083002", "AB0200003", "1003");
        serialInfos.add(serialInfo3);*/

        Map<String, List<SerialInfo>> map =  new HashMap<>();
        serialInfos.stream().collect(Collectors.groupingBy(SerialInfo::getGroupFiled,Collectors.toList()))
                .forEach(map::put);

        map.forEach((x, y)->{
            if(y.size() > 0){
                GeneralMaterialDocumentItemResults item = new GeneralMaterialDocumentItemResults();

                List<SerialNumberResults> results = new ArrayList<>();
                item.setGoodsMovementType("311");
                item.setGoodsMovementRefDocType("");

                item.setMaterial(y.get(0).getMaterial());
                item.setPlant(y.get(0).getPlant());
                item.setBatch(y.get(0).getBatch());
                item.setQuantityInEntryUnit(String.valueOf(y.size()));
                item.setStorageLocation(y.get(0).getStorageLocation());
                item.setMaterialDesc(y.get(0).getMaterialDesc());
                for(SerialInfo serialInfo1 : y){
                    SerialNumberResults serialNumberResults = new SerialNumberResults(serialInfo1.getSerialnumber());
                    results.add(serialNumberResults);
                }
                SerialNumber serialNumber = new SerialNumber(results);
                item.setSerialNumber(serialNumber);
                items.add(item);
            }
        });
        return items;
    }
    public int verifyScanData(List<String> snList, List<String> codes){

        HashSet<String> set = new HashSet<>(snList);
        set.retainAll(codes);
        if(set.size() > 0){
            return ERROR_REPEAT_SCAN;
        }
        int scanCount = snList.size() + codes.size();
        if(scanCount > 600){
            return ERROR_MAX_COUNT;
        }
        return -1;
    }

    public List<SerialNumberResults> buildSerialNumberResults(List<String> snList){
        List<SerialNumberResults> serialNumberResults = new ArrayList<>();
        for(String sn : snList){
            SerialNumberResults serial = new SerialNumberResults(sn);
            serialNumberResults.add(serial);
        }
        return serialNumberResults;
    }
    public List<SerialInfo> filterSerialInfo(List<SerialInfo> serialInfos, List<String> snList){
        List<SerialInfo> all = new ArrayList<>();
        for(String sn : snList){
            List<SerialInfo> filtered = serialInfos.stream().filter(s->StringUtils.equalsIgnoreCase(s.getSerialnumber(), sn)).collect(Collectors.toList());
            if(filtered != null && filtered.size() > 0){
                all.addAll(filtered);
            }else{
                SerialInfo serialInfo = new SerialInfo(sn);
                all.add(serialInfo);
            }
        }
        return all;
    }
}
