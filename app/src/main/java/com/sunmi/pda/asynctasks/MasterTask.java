package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MasterTask extends AsyncTask<Void, Void, String> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController userController = app.getUserController();
    private static final LogisticsProviderController logisticsProviderController = app.getLogisticsProviderController();
    private static final MaterialController materialController = app.getMaterialController();
    private static final StorageLocationController storageLocationController = app.getStorageLocationController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    public MasterTask(Context context, OnTaskEventListener callback) {
        mCallBack = callback;
        mContext = context;
    }
    @Override
    protected String doInBackground(Void... params) {

        try {
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("HH");
            String str = df.format(date);
            int a = Integer.parseInt(str);
            //if (a >= 0 && a <= 6) {
                materialController.syncData();
                storageLocationController.syncData();
                logisticsProviderController.syncData();
                userController.syncData();
            /*}else{
                materialController.syncData();
            }*/
            LogUtils.d("MasterTask", "Sync master data finished: " + DateUtils.dateToString(date, DateUtils.FormatFullDate));
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            LogUtils.e("MasterTask", "Sync master data error: " + error);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mCallBack != null) {
            if (StringUtils.isEmpty(error)) {
                mCallBack.onSuccess(null);
            } else {
                mCallBack.onFailure(error);
            }
        }
    }
}
