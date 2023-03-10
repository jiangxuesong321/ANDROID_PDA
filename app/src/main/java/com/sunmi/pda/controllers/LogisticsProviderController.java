package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;

import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogisticsProviderController {
    protected static final String TAG = LogisticsProviderController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();

    public HttpResponse syncData() throws Exception {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_logistics_provider) + app.getString(R.string.sap_url_client);

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        //LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        if(jsonArray != null && jsonArray.size() > 0){
            String now = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
            AppUtil.saveLastChangeDate(app, AppUtil.PROPERTY_LAST_CHANGE_DATE_LOGISTICS, now);
        }else{
            httpResponse.setError(app.getString(R.string.text_no_master_data));
        }
        List<LogisticsProvider> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String ztid = objectI.getString("ztid");
                String ztname = objectI.getString("ztname");

                LogisticsProvider logisticsProvider = new LogisticsProvider(ztid, ztname);
                all.add(logisticsProvider);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "syncLogisticsProvider Error ------> " + e.getMessage());
            }
        }
        app.getDBService().getDatabaseServiceLogisticsProvider().createData(all);
        return httpResponse;
    }

    public List<LogisticsProvider> getLogisticsProvider(){
        return app.getDBService().getDatabaseServiceLogisticsProvider().getAllData();
    }

    public int getLogisticsProviderIndex(String logisticsProviderId, List<LogisticsProvider> logisticsProviders){
        int index = 0;
        for(int i = 0; i < logisticsProviders.size(); i++){
            if(StringUtils.equalsIgnoreCase(logisticsProviderId, logisticsProviders.get(i).getZtId())){
                return i;
            }
        }
        return index;
    }
}
