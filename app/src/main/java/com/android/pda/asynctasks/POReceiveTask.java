package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.POStorageController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.POStorageQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class POReceiveTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final POStorageController poStorageController = app.getPoStorageController();
    private POStorageQuery query;

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;

    public POReceiveTask(Context context, OnTaskEventListener callback, POStorageQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }

    @Override
    protected Object doInBackground(Void... params) {

        try {
            List<PurchaseOrder> PurchaseOrderList = poStorageController.syncPOData(query);
            if (PurchaseOrderList != null) {
                return PurchaseOrderList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
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
