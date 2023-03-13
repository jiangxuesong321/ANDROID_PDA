package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.MaterialPickingController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.Material;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.MaterialPickingQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaterialPickingTask extends AsyncTask<Void, Void, Object> {
    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final MaterialPickingController materialPickingController = app.getMaterialPickingController();
    private MaterialPickingQuery query;

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;

    public MaterialPickingTask(Context context, OnTaskEventListener callback, MaterialPickingQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }

    @Override
    protected Object doInBackground(Void... params) {

        try {
            List<Material> materialList = materialPickingController.syncData(query);
            if (materialList != null) {
                return materialList;
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




