package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.PrintLabel;
import com.android.pda.utils.DateUtils;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class PrintController {
    protected static final String TAG = PrintController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    public final static String SHIPPING_LABEL = "ShippingLabel";
    public final static String RECEIVE_LABEL = "ReceiveLabel";

    public List<PrintLabel> syncPrintLabel(String document, String LabelFlag) throws Exception {
        LogUtils.d(TAG, "LabelFlag--->" + LabelFlag);
        String filter = "&$filter=PurchaseDocument eq '" + document + "'";
        String api = app.getString(R.string.sap_url_receive_label);
        if(StringUtils.equalsIgnoreCase(LabelFlag, SHIPPING_LABEL)){
            filter = "&$filter=DeliveryDocument eq '" + document + "'";
            api = app.getString(R.string.sap_url_shipping_label);
        }
        String url = app.getOdataService().getHost() + api +
                app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client) + filter ;

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<PrintLabel> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String PurchaseDocument = objectI.getString("PurchaseDocument");
                String PurchaseDocumentItem = objectI.getString("PurchaseDocumentItem");
                String Material = objectI.getString("Material");
                String MaterialDes = objectI.getString("MaterialDes");
                String Batch = objectI.getString("Batch");
                String Spec = objectI.getString("Spec");
                String Model = objectI.getString("Model");
                String UnitDesciption = objectI.getString("UnitDesciption");
                String Quantity = objectI.getString("Quantity");
                String PrintDate = objectI.getString("PrintDate");
                PrintDate = DateUtils.jsonDateToString(PrintDate);
                String SalesDocument = "";
                String CustomerMaterial = "";
                String Remark = "";
                if(StringUtils.equalsIgnoreCase(LabelFlag, SHIPPING_LABEL)){
                    SalesDocument = objectI.getString("SalesDocument");
                    CustomerMaterial = objectI.getString("CustomerMaterial");
                } else if(StringUtils.equalsIgnoreCase(LabelFlag, RECEIVE_LABEL)){
                    Remark = objectI.getString("Remark");
                }
                PrintLabel printLabel = new PrintLabel(PurchaseDocument, PurchaseDocumentItem, Material,
                        MaterialDes, Batch, Spec, Model, UnitDesciption, Quantity, PrintDate, SalesDocument,
                        CustomerMaterial, Remark);
                all.add(printLabel);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "fetch Shipping Label Error ------> " + e.getMessage());
            }
        }

        return all;
    }

}
