package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.TransferOrderController;
import com.android.pda.models.TransferOrder;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.exceptions.GeneralException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.SalesInvoiceQuery;

import org.apache.commons.lang3.StringUtils;
import java.util.List;

public class TransferOrderTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final TransferOrderController controller = app.getTransferOrderController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private SalesInvoiceQuery query;

    public TransferOrderTask(Context context, OnTaskEventListener callback, SalesInvoiceQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }

    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<TransferOrder> transferOrders = controller.syncData(query);
            if(transferOrders != null){
                return transferOrders;
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
