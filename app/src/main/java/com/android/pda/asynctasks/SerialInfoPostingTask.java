package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.SerialInfoController;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.SerialInfo;
import com.android.pda.models.SerialNumberResults;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SerialInfoPostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final SerialInfoController controller = app.getSerialInfoController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private List<SerialNumberResults> request;
    public SerialInfoPostingTask(Context context, OnTaskEventListener callback, List<SerialNumberResults> request) {
        mCallBack = callback;
        mContext = context;
        this.request = request;
    }
    @Override
    protected Object doInBackground(Void... params) {

        try {
            List<SerialInfo> serialInfos = controller.syncData(request);
            if(serialInfos != null){
                return serialInfos;
            }
        }catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (Exception e) {
            e.printStackTrace();
            error = mContext.getString(R.string.text_service_failed);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (mCallBack != null) {
            if (StringUtils.isEmpty(error)) {
                mCallBack.onSuccess(null);
                mCallBack.bindModel(o);
            } else {
                mCallBack.onFailure(error);
            }
        }
    }
}