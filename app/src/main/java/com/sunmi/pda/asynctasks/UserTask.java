package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.HttpResponse;

import org.apache.commons.lang3.StringUtils;

public class UserTask extends AsyncTask<Void, Void, String> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    public UserTask(Context context, OnTaskEventListener callback) {
        mCallBack = callback;
        mContext = context;
    }
    @Override
    protected String doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            HttpResponse httpResponse = userController.syncData();
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
