package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.PurchaseOrderGiController;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.PurchaseOrderGiPostingRequest;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PurchaseOrdeGiPostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderGiController purchaseOrderController = app.getPurchaseOrderGiController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private List<PurchaseOrderGiPostingRequest> request;
    private String functionId;
    public PurchaseOrdeGiPostingTask(Context context, OnTaskEventListener callback,
                                     List<PurchaseOrderGiPostingRequest> request, String functionId) {
        mCallBack = callback;
        mContext = context;
        this.request = request;
        this.functionId = functionId;
    }
    @Override
    protected Object doInBackground(Void... params) {

        try {
            HttpResponse httpResponse = purchaseOrderController.posting(request, functionId);
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
