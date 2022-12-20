package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.MaterialVoucherController;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.MaterialVoucher;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.MaterialVoucher;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class MaterialVoucherTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final MaterialVoucherController controller = app.getMaterialVoucherController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private SalesInvoiceQuery query;

    public MaterialVoucherTask(Context context, OnTaskEventListener callback, SalesInvoiceQuery query) {
        mCallBack = callback;
        mContext = context;
        this.query = query;
    }

    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<MaterialVoucher> materialVouchers = controller.syncData(query);
            if(materialVouchers != null){
                return materialVouchers;
            }
        }catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (GeneralException e) {
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
