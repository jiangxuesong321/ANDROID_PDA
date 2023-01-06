package com.android.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.InOutboundReportController;
import com.android.pda.exceptions.AuthorizationException;
import com.android.pda.listeners.OnTaskEventListener;
import com.android.pda.models.InOutboundReportQuery;

import org.apache.commons.lang3.StringUtils;


public class InOutboundReportTask extends AsyncTask<Void, Void, Object> {

    private static final AndroidApplication app = AndroidApplication.getInstance();
    private static final InOutboundReportController controller = app.getInOutboundReportController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private InOutboundReportQuery query;
    public InOutboundReportTask(Context context, OnTaskEventListener callback, InOutboundReportQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }
    @Override
    protected Object doInBackground(Void... params) {

        try {
            boolean success = controller.syncData(query);

            return success;

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
