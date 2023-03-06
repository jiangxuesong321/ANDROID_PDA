package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.POStorageController;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.listeners.OnTaskEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class POStoragePostingTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final POStorageController poStorageController = app.getPoStorageController();
    private List<MaterialDocument> list;

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;

    public POStoragePostingTask(Context context, OnTaskEventListener callback, List<MaterialDocument> materialDocument) {
        mCallBack = callback;
        mContext = context;
        this.list = materialDocument;
    }

    @Override
    protected Object doInBackground(Void... params) {
        String materialDocument = "";
        try {
            materialDocument = poStorageController.createMaterialDocument(list);
            if (StringUtils.isEmpty(materialDocument)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return materialDocument;
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