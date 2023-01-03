package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StorageLocationController {
    protected static final String TAG = StorageLocationController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    public HttpResponse syncData() throws Exception {
        String filter = "&$filter=" + app.getString(R.string.filter_language);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_plant) + app.getString(R.string.sap_url_client);

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        //LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        if(jsonArray != null && jsonArray.size() > 0){
            String now = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
            AppUtil.saveLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_LOCATION, now);
        }else{
            httpResponse.setError(app.getString(R.string.text_no_master_data));
        }
        List<StorageLocation> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String plant = objectI.getString("plant");
                String storageLocation = objectI.getString("storageLocation");
                String plantName = objectI.getString("plantName");
                String storageLocationName = objectI.getString("storageLocationName");
                StorageLocation plantMaster = new StorageLocation(plant, storageLocation, plantName, storageLocationName);
                all.add(plantMaster);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "syncPlantData Error ------> " + e.getMessage());
            }
        }
        app.getDBService().getDatabaseServicePlant().createData(all);
        return httpResponse;
    }

    public List<StorageLocation> getStorageLocation(){
        return app.getDBService().getDatabaseServicePlant().getAllData();
    }

    public List<String> getStorageLocationStr(List<StorageLocation> locations){
        List<String> strArray = new ArrayList<>();
        if(locations != null){
            for(int i = 0; i < locations.size(); i++){
                strArray.add(locations.get(i).getStorageLocation() + " - " + locations.get(i).getStorageLocationName()) ;
            }
        }
        return strArray;
    }

    public int getStorageLocationPosition(String storageLocation, List<StorageLocation> storageLocations){
        int position = 0;
        for(int i = 0; i < storageLocations.size(); i++){
            if(StringUtils.isNotEmpty(storageLocation)){
                if(StringUtils.equalsIgnoreCase(storageLocation, storageLocations.get(i).getStorageLocation())){
                    position = i;
                    return position;
                }
            }
        }
        return position;
    }

    public int getPlantPosition(String plant, List<StorageLocation> plants){
        int position = 0;
        for(int i = 0; i < plants.size(); i++){
            if(StringUtils.isNotEmpty(plant)){
                if(StringUtils.equalsIgnoreCase(plant, plants.get(i).getPlant())){
                    position = i;
                    return position;
                }
            }
        }
        return position;
    }

    public List<StorageLocation> filterStorageLocationByPlant(List<StorageLocation> storageLocations, String plant){
        if(StringUtils.isNotEmpty(plant)){
            List<StorageLocation> locationList = storageLocations.stream().filter(s -> StringUtils.equalsIgnoreCase(s.getPlant(), plant)).collect(Collectors.toList());
            return locationList;
        }else{
            return storageLocations;
        }
    }

    public int getStorageLocationCount(){
        return app.getDBService().databaseServiceStorageLocation.getStorageLocationCount();
    }
}
