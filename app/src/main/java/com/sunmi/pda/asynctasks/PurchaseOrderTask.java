package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.PurchaseOrderController;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.PurchaseOrderQuery;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

public class PurchaseOrderTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final PurchaseOrderController purchaseOrderController = app.getPurchaseOrderController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private PurchaseOrderQuery purchaseOrderQuery;
    private String function;
    public PurchaseOrderTask(Context context, OnTaskEventListener callback,
                             PurchaseOrderQuery purchaseOrderQuery, String function) {
        mCallBack = callback;
        mContext = context;
        this.purchaseOrderQuery = purchaseOrderQuery;
        this.function = function;
    }
    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->" + function);
        try {
            List<PurchaseOrder> purchaseOrders = null;
            if(StringUtils.equalsIgnoreCase(function, AppConstants.FUNCTION_ID_PURCHASE_ORDER)){
                purchaseOrders = purchaseOrderController.syncData(purchaseOrderQuery);
            }else{
                purchaseOrders = purchaseOrderController.syncReturnData(purchaseOrderQuery);
            }
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
