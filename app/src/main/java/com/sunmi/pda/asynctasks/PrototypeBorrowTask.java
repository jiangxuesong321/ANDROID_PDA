package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.PrototypeBorrowController;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.BusinessOrderQuery;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PrototypeBorrowTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final PrototypeBorrowController controller = app.getPrototypeBorrowController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private BusinessOrderQuery query;
    public PrototypeBorrowTask(Context context, OnTaskEventListener callback, BusinessOrderQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }
    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<PrototypeBorrow> prototypeBorrows = controller.syncData(query);
            if(prototypeBorrows != null){
                return prototypeBorrows;
            }
        }catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (Exception e) {
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
