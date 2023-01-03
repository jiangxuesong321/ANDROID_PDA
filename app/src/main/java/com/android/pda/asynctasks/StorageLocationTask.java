package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.HttpResponse;

import org.apache.commons.lang3.StringUtils;

public class StorageLocationTask extends AsyncTask<Void, Void, String> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final StorageLocationController controller = app.getStorageLocationController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    public StorageLocationTask(Context context, OnTaskEventListener callback) {
        mCallBack = callback;
        mContext = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            HttpResponse httpResponse = controller.syncData();
            if(httpResponse != null){
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
