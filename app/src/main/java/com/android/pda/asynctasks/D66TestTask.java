package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.D66TestController;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.MaterialInfo;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class D66TestTask extends AsyncTask<Void, Void, Object> {
    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final D66TestController controller = app.getD66TestController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private MaterialInfo query;
    private String material;

    public D66TestTask(Context context, OnTaskEventListener callback, String material) {
        mCallBack = callback;
        mContext = context;
        this.material = material;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            List<MaterialInfo> materialInfoList = controller.getMaterialList(material);
            return materialInfoList;
        } catch (Exception e) {
            e.printStackTrace();
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
