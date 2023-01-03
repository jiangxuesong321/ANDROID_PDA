package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.controllers.OrderInvoiceOthersController;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.OrderInvoiceOthersQuery;
import com.sunmi.pda.models.OrderInvoiceOthersResult;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class OrderInvoiceOthersTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final OrderInvoiceOthersController orderBatchInvoiceController = app.getOrderInvoiceOthersController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private OrderInvoiceOthersQuery orderInvoiceOthersQuery;

    public OrderInvoiceOthersTask(Context context, OnTaskEventListener callback, OrderInvoiceOthersQuery orderInvoiceOthersQuery) {
        mCallBack = callback;
        mContext = context;
        this.orderInvoiceOthersQuery = orderInvoiceOthersQuery;
    }

    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<OrderInvoiceOthersResult> orderBatchQuery = orderBatchInvoiceController.syncData(orderInvoiceOthersQuery);
            if (orderBatchQuery != null) {
                return orderBatchQuery;
            }
        } catch (AuthorizationException e) {
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("OrderInvoiceOthersTask", "Error--->" + e.getMessage());
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
