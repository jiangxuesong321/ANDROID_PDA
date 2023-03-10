package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.utils.DateUtils;

import java.util.Date;

public class OfflineController {
    protected static final String TAG = OfflineController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();

    public void saveOfflineData(String id, String orderBody, String orderNumber,
                                LogisticsProvider logisticsProvider, String logisticNumber, String plant,
                                StorageLocation sendStorageLocation, StorageLocation receiveStorageLocation){
        String logisticsProviderId = "";
        String sendLocation = "";
        String receiveLocation = "";
        if(logisticsProvider != null){
            logisticsProviderId = logisticsProvider.getZtId();
        }
        if(receiveStorageLocation != null){
            receiveLocation = receiveStorageLocation.getStorageLocation();
        }
        if(sendStorageLocation != null){
            sendLocation = sendStorageLocation.getStorageLocation();
        }
        Offline offline = new Offline(id, orderBody, orderNumber, "", "",
                DateUtils.dateToString(new Date(), DateUtils.FormatFullDate),
                logisticsProviderId, logisticNumber, plant, sendLocation, receiveLocation, "", "");
        app.getDBService().getDatabaseServiceOffline().createData(offline);
    }


    public Offline getOfflineData(String id){
        Offline offline = app.getDBService().getDatabaseServiceOffline().getDataByKey(id);
        LogUtils.i(TAG, "Offline Data--->" + JSON.toJSONString(offline));
        return offline;
    }

    public void clearOfflineData(String id){
        LogUtils.i(TAG, "clearOfflineData Data--->" + JSON.toJSONString(id));
        app.getDBService().getDatabaseServiceOffline().deleteDataByKey(id);
    }
}
