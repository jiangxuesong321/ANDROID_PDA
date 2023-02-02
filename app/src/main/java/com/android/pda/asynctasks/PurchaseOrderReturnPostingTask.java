package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.PurchaseOrderController;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.PurchaseOrderReturnPostingRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PurchaseOrderReturnPostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private List<PurchaseOrderReturnPostingRequest> requestList;
    public PurchaseOrderReturnPostingTask(Context context, OnTaskEventListener callback,
                                          List<PurchaseOrderReturnPostingRequest> requestList) {
        mCallBack = callback;
        mContext = context;
        this.requestList = requestList;
    }
    @Override
    protected Object doInBackground(Void... params) {

        try {
            HttpResponse httpResponse = purchaseOrderController.postingReturn(requestList);
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