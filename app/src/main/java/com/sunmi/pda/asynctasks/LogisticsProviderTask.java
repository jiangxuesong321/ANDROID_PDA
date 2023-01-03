package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.application.AndroidApplication;

import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.HttpResponse;

import org.apache.commons.lang3.StringUtils;

public class LogisticsProviderTask extends AsyncTask<Void, Void, String> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    public LogisticsProviderTask(Context context, OnTaskEventListener callback) {
        mCallBack = callback;
        mContext = context;
    }
    @Override
    protected String doInBackground(Void... params) {

        try {
            HttpResponse httpResponse = logisticsProviderController.syncData();
            if(httpResponse != null){
                System.out.println("doInBackground--->" + httpResponse.getError());
                if(StringUtils.isEmpty(httpResponse.getError())){
                    return null;
                }else{
                    return httpResponse.getError();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mCallBack != null) {
            if (StringUtils.isEmpty(result)) {
                mCallBack.onSuccess(null);
            } else {
                mCallBack.onFailure(result);
            }
        }
    }
}
