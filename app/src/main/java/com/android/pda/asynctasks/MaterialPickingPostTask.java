package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.MaterialPickingController;
import com.android.pda.database.pojo.Material;
import com.android.pda.listeners.OnTaskEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialPickingPostTask extends AsyncTask<Void, Void, Object> {
    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final MaterialPickingController materialPickingController = app.getMaterialPickingController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private List<Material> materialChooseList;

    public MaterialPickingPostTask(Context context, OnTaskEventListener callback, List<Material> materialList) {
        mCallBack = callback;
        mContext = context;
        this.materialChooseList = materialList;
    }

    @Override
    protected Object doInBackground(Void... params) {
        Map<String, String> result = new HashMap<>();
        try {
            result = materialPickingController.createMaterialDocument(materialChooseList);

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
