package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.ProductionStorageController;
import com.android.pda.controllers.UserController;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.ProductionStorageQuery;

import org.apache.commons.lang3.StringUtils;

public class ProductionStorageTask extends AsyncTask<Void, Void, String> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController userController = app.getUserController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    private ProductionStorageQuery mQuery;
    public String error;

    public ProductionStorageTask(Context context, OnTaskEventListener callback, ProductionStorageQuery query) {
        mCallBack = callback;
        mContext = context;
        mQuery = query;
    }

    @Override
    protected String doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            String materialDocumentYear = ProductionStorageController.getMaterialDocumentYear(mQuery);


            HttpResponse httpResponse = null;
//            httpResponse = ProductionStorageController.syncData(mQuery);
            if (httpResponse != null) {
                if (StringUtils.isEmpty(httpResponse.getError())) {
                    return null;
                } else {
                    return httpResponse.getError();
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (mCallBack != null) {
            if (StringUtils.isEmpty(result)) {
                mCallBack.onSuccess(null);
            } else {
                mCallBack.onFailure(result);
            }
        }
    }
}
