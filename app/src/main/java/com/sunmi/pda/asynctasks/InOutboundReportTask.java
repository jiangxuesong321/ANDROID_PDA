package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.InOutboundReportController;
import com.sunmi.pda.controllers.PickingController;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.BusinessOrderQuery;
import com.sunmi.pda.models.InOutboundReport;
import com.sunmi.pda.models.InOutboundReportQuery;
import com.sunmi.pda.models.Picking;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class InOutboundReportTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
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
