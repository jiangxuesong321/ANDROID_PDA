package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.ProductionStorageController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.listeners.OnTaskEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionStoragePostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final ProductionStorageController productionStorageController = app.getProductionStorageController();
    private List<MaterialDocument> materialDocumentList;

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;

    public ProductionStoragePostingTask(Context context, OnTaskEventListener callback, List<MaterialDocument> materialDocumentList) {
        mCallBack = callback;
        mContext = context;
        this.materialDocumentList = materialDocumentList;
    }

    @Override
    protected Object doInBackground(Void... params) {
        Map<String, String> result = new HashMap<>();
        try {
            result = productionStorageController.createMaterialDocument(materialDocumentList);
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return result;
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
