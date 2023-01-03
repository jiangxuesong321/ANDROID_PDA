package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.PickingController;

import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.Picking;

import com.sunmi.pda.models.BusinessOrderQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PickingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final PickingController controller = app.getPickingController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private BusinessOrderQuery query;
    private int functionId;
    public PickingTask(Context context, OnTaskEventListener callback, BusinessOrderQuery query, int functionId) {
        mCallBack = callback;
        mContext = context;
        this.functionId = functionId;
        this.query = query;
    }
    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<Picking> pickings = controller.syncData(query, functionId);
            if(pickings != null){
                return pickings;
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
