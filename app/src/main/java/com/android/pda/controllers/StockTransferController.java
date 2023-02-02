package com.android.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.GeneralMaterialDocumentItem;
import com.android.pda.models.GeneralMaterialDocumentItemResults;
import com.android.pda.models.GeneralPostingRequest;

import com.android.pda.models.SerialInfo;
import com.android.pda.models.SerialNumber;
import com.android.pda.models.SerialNumberResults;
import com.android.pda.utils.DateUtils;
import com.android.pda.utils.HttpRequestUtil;
import com.android.pda.utils.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockTransferController {
    protected static final String TAG = StockTransferController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    public GeneralPostingRequest buildRequest(List<SerialInfo> serialInfos, String locationFrom, String locationTo, String plant, String confirmDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        String postingDate = confirmDate + "T00:00:00";
        String logistics = "";
        String goodsMovementCode = "04";
        String materialDocumentHeaderText = "";

        List<GeneralMaterialDocumentItemResults> items = groupSerialInfo(serialInfos, locationFrom, locationTo, plant);
        GeneralMaterialDocumentItem documentItem = new GeneralMaterialDocumentItem(items);
        GeneralPostingRequest request = new GeneralPostingRequest(documentDate, postingDate,
                goodsMovementCode, materialDocumentHeaderText, logistics, "",
                "9", documentItem);
        return request;
    }


    public HttpResponse posting(GeneralPostingRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        Map<String, String> tokenHeaders = http.getToken(url);
        tokenHeaders.put(app.getString(R.string.header_language_key), app.getString(R.string.header_language_value));
        LogUtils.d("StockTransfer", "Headers---->" + JSON.toJSONString(tokenHeaders));

        String json = JSON.toJSONString(request);
        LogUtils.d("StockTransfer", "POST --->" + json);

        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, tokenHeaders);
        LogUtils.d("StockTransfer", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("StockTransfer", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            String errorMessage = Util.parseSapError(httpResponse.getResponseString());
            LogUtils.e("StockTransfer", "errorMessage-->" + errorMessage);
            httpResponse.setError(errorMessage);
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            String MaterialDocument = d.getString("MaterialDocument");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }

    public List<GeneralMaterialDocumentItemResults> groupSerialInfo(List<SerialInfo> serialInfos, String locationFrom, String locationTo, String plant){
        List<GeneralMaterialDocumentItemResults> items = new ArrayList<>();

        Map<String, List<SerialInfo>> groupBy =  new HashMap<>();
        serialInfos.stream().collect(Collectors.groupingBy(SerialInfo::getGroupFiled,Collectors.toList()))
                .forEach(groupBy::put);

        groupBy.forEach((x, y)->{
            if(y.size() > 0){
                GeneralMaterialDocumentItemResults item = new GeneralMaterialDocumentItemResults();

                List<SerialNumberResults> results = new ArrayList<>();
                item.setGoodsMovementType("311");
                item.setGoodsMovementRefDocType("");
                item.setMaterial(y.get(0).getMaterial());
                item.setPlant(plant);
                item.setBatch(y.get(0).getBatch());
                item.setStorageLocation(locationFrom);
                item.setIssuingOrReceivingStorageLoc(locationTo);

                int count = 0;
                for(SerialInfo serialInfo1 : y){
                    SerialNumberResults serialNumberResults = new SerialNumberResults(serialInfo1.getSerialnumber());
                    results.add(serialNumberResults);

                    //count of material+batch group, manual entered and scanned
                    if (Integer.valueOf(y.get(0).getCount()) > 0) {
                        count += Integer.valueOf(y.get(0).getCount());
                    } else {
                        count++;
                    }
                }
                item.setQuantityInEntryUnit(String.valueOf(count));

                SerialNumber serialNumber = new SerialNumber(results);
                item.setSerialNumber(serialNumber);
                items.add(item);
            }
        });
        return items;
    }


}