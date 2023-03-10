package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.NoValueController;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.NoValueRequest;

import org.apache.commons.lang3.StringUtils;

public class NoValuePostingTask extends AsyncTask<Void, Void, Object> {
    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final NoValueController noValueCtrl = app.getNoValueController();

    private OnTaskEventListener<String> mCallback;
    private Context mContext;
    private String error;
    private NoValueRequest request;

    public NoValuePostingTask(Context mContext, OnTaskEventListener<String> mCallback, NoValueRequest request) {
        this.mCallback = mCallback;
        this.mContext = mContext;
        this.request = request;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            HttpResponse response = noValueCtrl.post(request);
            if(response != null){
                if(StringUtils.isNotEmpty(response.getError())){
                    error = response.getError();
                }else{
                    return response.getResponseString();
                }
            }
        }catch (AuthorizationException e) {
            error = mContext.getString(R.string.text_service_authorization_error);
        }catch (GeneralException e) {
            e.printStackTrace();
            error = mContext.getString(R.string.text_service_failed);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (mCallback != null) {
            if (StringUtils.isEmpty(error)) {
                mCallback.onSuccess(null);
                mCallback.bindModel(o);
            } else {
                mCallback.onFailure(error);
            }
        }
    }
}
