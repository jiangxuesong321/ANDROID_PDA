package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.GeneralMaterialDocumentItem;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LendBackController {
    protected static final String TAG = LendBackController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();

    public GeneralPostingRequest buildRequest(List<GeneralMaterialDocumentItemResults> items, String locationTo,
                                              LogisticsProvider logisticsProvider, String logisticNumber,
                                              String plant, String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "04";
        String materialDocumentHeaderText = "";

        for(GeneralMaterialDocumentItemResults results : items){
            results.setIssuingOrReceivingStorageLoc(locationTo);
            results.setStorageLocation("1301");
            results.setMaterialDesc(null);
            results.setPlant(plant);
        }

        GeneralMaterialDocumentItem documentItem = new GeneralMaterialDocumentItem(items);
        String logistics = "";
        if(logisticsProvider != null){
            logistics = logisticsProvider.getZtName();
        }
        GeneralPostingRequest request = new GeneralPostingRequest(documentDate, postingDate,
                goodsMovementCode, materialDocumentHeaderText, logistics, Util.removeEnter(logisticNumber),
                "8", documentItem);
        return request;
    }




    public HttpResponse posting(GeneralPostingRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        Map<String, String> tokenHeaders = http.getToken(url);
        LogUtils.d("LendBack", "Headers---->" + JSON.toJSONString(tokenHeaders));

        String json = JSON.toJSONString(request);
        LogUtils.d("LendBack", "json---->" + json);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, tokenHeaders);
        LogUtils.d("LendBack", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("LendBack", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            String errorMessage = Util.parseSapError(httpResponse.getResponseString());
            LogUtils.e("LendBack", "errorMessage-->" + errorMessage);
            httpResponse.setError(errorMessage);
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            String MaterialDocument = d.getString("MaterialDocument");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }

    public boolean checkStorageLocation(String receivingStorage){

        if(StringUtils.equalsIgnoreCase("1301", receivingStorage)){
            return false;
        }
        return true;
    }
}
