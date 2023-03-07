package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.POStorageController;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.listeners.OnTaskEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POReceivePostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final POStorageController poStorageController = app.getPoStorageController();
    private List<PurchaseOrder> list;

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;

    public POReceivePostingTask(Context context, OnTaskEventListener callback, List<PurchaseOrder> purchaseOrder) {
        mCallBack = callback;
        mContext = context;
        this.list = purchaseOrder;
    }

    @Override
    protected Object doInBackground(Void... params) {
        Map<String, String> poInfo = new HashMap<>();
        try {
            poInfo = poStorageController.createPOMaterialDocument(list);
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return poInfo;
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