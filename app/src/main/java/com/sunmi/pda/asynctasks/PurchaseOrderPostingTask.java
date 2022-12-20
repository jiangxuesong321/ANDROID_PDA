package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.PurchaseOrderController;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.PurchaseOrderPostingRequest;
import com.sunmi.pda.models.PurchaseOrderQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PurchaseOrderPostingTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private PurchaseOrderPostingRequest request;
    public PurchaseOrderPostingTask(Context context, OnTaskEventListener callback, PurchaseOrderPostingRequest request) {
        mCallBack = callback;
        mContext = context;
        this.request = request;
    }
    @Override
    protected Object doInBackground(Void... params) {

        try {
            HttpResponse httpResponse = purchaseOrderController.posting(request);
            if(httpResponse != null){
                if(StringUtils.isNotEmpty(httpResponse.getError())){
                    error = httpResponse.getError();
                }else{
                    return httpResponse.getResponseString();
                }
            }
        }catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (GeneralException e) {
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
