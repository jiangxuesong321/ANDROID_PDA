package com.sunmi.pda.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.SalesInvoiceController;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.listeners.OnTaskEventListener;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SalesInvoiceSN;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SalesInvoiceTask extends AsyncTask<Void, Void, Object> {

    private static final SunmiApplication app = SunmiApplication.getInstance();
    private static final SalesInvoiceController salesInvoiceController = app.getSalesInvoiceController();

    private OnTaskEventListener<String> mCallBack;
    private Context mContext;
    public String error;
    private SalesInvoiceQuery salesInvoiceQuery;
    private String functionId;
    public SalesInvoiceTask(Context context, OnTaskEventListener callback,
                            SalesInvoiceQuery salesInvoiceQuery, String functionId) {
        mCallBack = callback;
        mContext = context;
        this.functionId = functionId;
        this.salesInvoiceQuery = salesInvoiceQuery;
    }
    @Override
    protected Object doInBackground(Void... params) {
        System.out.println("doInBackground--->");
        try {
            List<SalesInvoice> salesInvoices = salesInvoiceController.syncData(salesInvoiceQuery);
            if(salesInvoices != null){
                if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_SALES_INVOICE)){
                    for (SalesInvoice salesInvoice : salesInvoices) {
                        if (salesInvoice.getMaterial() != null && salesInvoice.getMaterial().length() > 0) {
                            List<SalesInvoiceSN> salesInvoiceSNs = salesInvoiceController.getSN(salesInvoice.getDeliveryDocument());
                            if (salesInvoiceSNs != null) {
                                setTempSaveSNCount(salesInvoiceSNs, salesInvoice);
                            }
                        }
                    }
                }

                return salesInvoices;
            }
        } catch (AuthorizationException e){
            error = mContext.getString(R.string.text_service_authorization_error);
        } catch (Exception e) {
            e.printStackTrace();
            error = mContext.getString(R.string.text_service_failed);
        }
        return null;
    }

    /**
     * 暂存SN由另外接口获取，需将已扫个数赋值给SalesInvoice
     * @param salesInvoiceSNs
     */
    private void setTempSaveSNCount (List<SalesInvoiceSN> salesInvoiceSNs, SalesInvoice salesInvoice){
        if(salesInvoiceSNs != null && salesInvoiceSNs.size() > 0) {
            if (salesInvoice.getMaterial() != null && salesInvoice.getMaterial().length() > 0) {
                String materialFirst = salesInvoice.getMaterial().substring(0, 1);
                if (!StringUtils.equalsIgnoreCase(materialFirst, "A")) {
                    List<SalesInvoiceSN> findList = salesInvoiceSNs.stream().filter(o ->
                            StringUtils.equalsIgnoreCase(salesInvoice.getDeliveryDocumentItem(), o.getDeliveryDocumentItem())).collect(Collectors.toList());

                    List<String> sns = new ArrayList<>();
                    for (SalesInvoiceSN sn : findList) {
                        sns.add(sn.getSerialNo());
                    }
                    salesInvoice.setScanCount(findList.size());
                    salesInvoice.setScanBarcodes(sns);
                }
            }
        }

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
