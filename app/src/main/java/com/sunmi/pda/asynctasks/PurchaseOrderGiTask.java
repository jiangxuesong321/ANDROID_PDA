package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.PurchaseOrderController;
import com.sunmi.pda.controllers.PurchaseOrderGiController;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.PurchaseOrderGi;
import com.sunmi.pda.models.PurchaseOrderQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PurchaseOrderGiTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final PurchaseOrderGiController purchaseOrderController = app.getPurchaseOrderGiController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private PurchaseOrderQuery purchaseOrderQuery;
    private String functionId;
    public PurchaseOrderGiTask(Context context, OnTaskEventListener callback,
                               PurchaseOrderQuery purchaseOrderQuery, String functionId) {
        mCallBack = callback;
        mContext = context;
        this.functionId = functionId;
        this.purchaseOrderQuery = purchaseOrderQuery;

    }
    @Override
    protected Object doInBackground(Void... params) {
        try {
            System.out.println("functionId---->" + functionId);
            List<PurchaseOrderGi> purchaseOrders = null;
            purchaseOrders = purchaseOrderController.syncData(purchaseOrderQuery, functionId);
            if(purchaseOrders != null){
                return purchaseOrders;
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
