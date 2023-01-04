package com.android.pda.controllers;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;

import com.android.pda.log.FileUtils;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.InOutboundReport;
import com.android.pda.models.InOutboundReportQuery;

import com.android.pda.utils.DateUtils;
import com.android.pda.utils.ExcelUtils;
import com.android.pda.utils.FileUtil;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class InOutboundReportController {
    protected static final String TAG = InOutboundReportController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

    public boolean syncData(InOutboundReportQuery query) throws Exception {
        String filter = "";
        if(query.getOrderIndices() != null && query.getOrderIndices().size() > 0){
            filter = "$filter=(";
            System.out.println("OrderIndices--->" + query.getOrderIndices());
            for(int i = 0; i < query.getOrderIndices().size(); i ++){
                int id = query.getOrderIndices().get(i) + 1;
                if(i == 0){
                    filter = filter + "DocumentType eq '" + id + "'";
                }else{
                    filter = filter + " or DocumentType eq '" + id + "'";
                }
            }
            filter = filter + ")";
        }

        if(query.getStorageLocations() != null && query.getStorageLocations().size() >0){
            if(StringUtils.isNotEmpty(filter)){
                filter += " and (";
            }else{
                filter += " (";
            }
            for(int i = 0; i < query.getStorageLocations().size(); i ++){
                if(query.getStorageLocations().get(i) != null){
                    if(i == query.getStorageLocations().size() - 1){
                        filter = filter + "StoreLocation eq '" + query.getStorageLocations().get(i) + "'";
                    }else{
                        filter = filter + "StoreLocation eq '" + query.getStorageLocations().get(i) + "' or ";
                    }
                }
            }
            filter +=")";
        }

        /*filterLocations = "(";
        for(int i = 0; i < query.getStorageLocations().size(); i ++){
            if(query.getStorageLocations().get(i) != null){
                if(i == query.getStorageLocations().size() - 1){
                    filterLocations = filterLocations + "InventoryLocation eq '" + query.getStorageLocations().get(i) + "'";
                }else{
                    filterLocations = filterLocations + "InventoryLocation eq '" + query.getStorageLocations().get(i) + "' or ";
                }
            }
        }
        filterLocations +=")";
        if(StringUtils.isNotEmpty(query.getStorageLocation())){
            if(StringUtils.isNotEmpty(filter)){
                filter = filter + " and StoreLocation eq '" + query.getStorageLocation() + "'";
            }else{
                filter = filter + "StoreLocation eq '" + query.getStorageLocation() + "'";
            }
        }*/
        if(StringUtils.isNotEmpty(query.getDeliveryDateFromTo())
                || StringUtils.isNotEmpty(query.getDeliveryDateFrom())){
            if(StringUtils.isNotEmpty(query.getDeliveryDateFromTo())){
                String createDateFrom = query.getDeliveryDateFrom() + "T00:00:00";
                String createDateTo = query.getDeliveryDateFromTo() + "T00:00:00";
                if(StringUtils.isNotEmpty(filter)){
                    filter = filter + " and ";
                }
                filter = filter + "(PostingDate ge datetime'" + createDateFrom
                        + "' and PostingDate le datetime'" + createDateTo + "' ) ";
            }else{
                if(StringUtils.isNotEmpty(query.getDeliveryDateFrom())){
                    String createDateFrom = query.getDeliveryDateFrom() + "T00:00:00";
                    if(StringUtils.isNotEmpty(filter)){
                        filter = filter + " and ";
                    }
                    filter = filter + "(PostingDate ge datetime'" + createDateFrom +"' ) ";
                }
            }
        }
        if(StringUtils.isNotEmpty(filter)){
            filter = "&" + filter;
        }
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_stock_in_out)
                + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client) + filter ;

        int index = 0;
        int top = 1000;
        int skip = 0;
        boolean isFinished = false;

        File fileXls = null;
        boolean success = false;
        while (!isFinished) {
            List<InOutboundReport> all = new ArrayList<>();
            String urlTop = "";
            if (index == 0) {
                skip = 0;
            } else {
                skip = top * index;
            }
            index = index + 1;
            urlTop += "&$top=" + top + "&$skip=" + skip;
            ;
            all = sync(url + urlTop, all);

            if(all != null && all.size() > 0){
                if(skip == 0){
                    fileXls = exportExcel();
                }
                success = true;
                exportRow(all, fileXls.getAbsolutePath(), fileXls);
            }else{
                isFinished = true;
            }
        }
        return success;
    }

    private List<InOutboundReport> sync(String url, List<InOutboundReport> all)throws Exception{
        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        //LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        if(jsonArray.size() == 0){
            LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
            return null;
        }
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String documentType = objectI.getString("DocumentType");
                documentType = getDocumentTypeDesc(documentType);
                String orderNumber = objectI.getString("OrderNumber");
                String materialDocumentNo = objectI.getString("MaterialDocumentNo");
                String materialDocumentYear = objectI.getString("MaterialDocumentYear");
                String material = objectI.getString("Material");
                String materialName = objectI.getString("MaterialName");
                String specifications = objectI.getString("Specifications");
                String batch = objectI.getString("Batch");
                String serialNumber = objectI.getString("SerialNumber");
                String unit = objectI.getString("Unit");
                double quantity = objectI.getDouble("Quantity");
                String contacts = objectI.getString("Contacts");
                String address = objectI.getString("Address");
                String contactNumber = objectI.getString("ContactNumber");
                String logisticsProvider = objectI.getString("LogisticsProvider");
                String logisticsOrderNo = objectI.getString("LogisticsOrderNo");
                String serialFlag = objectI.getString("SerialFlag");
                String postingDate = objectI.getString("PostingDate");
                postingDate = DateUtils.jsonDateToString(postingDate);
                String storeLocation = objectI.getString("StoreLocation");
                String parameters = objectI.getString("Parameters");
                String model = objectI.getString("Model");
                String customPoNumber = objectI.getString("CustomPoNumber");
                String customPCustomMaterialoNumber = objectI.getString("CustomMaterial");
                String customName = objectI.getString("CustomName");
                String storeLocationName = objectI.getString("StoreLocationName");

                //System.out.println("model----->" + model);
                InOutboundReport inOutboundReport = new InOutboundReport(documentType, orderNumber, materialDocumentNo,
                        materialDocumentYear, material, materialName, specifications, batch, serialNumber, unit,
                        quantity, contacts, address, contactNumber, logisticsProvider, logisticsOrderNo, serialFlag,
                        postingDate, storeLocation, parameters, model, customPoNumber, customPCustomMaterialoNumber,
                        customName, storeLocationName) ;
                all.add(inOutboundReport);
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "In Out Report Error------> " + e.getMessage());
            }
        }
        return all;
    }

    public File exportExcel() throws IOException, WriteException, BiffException {
        String[] title = { "单据日期", "单据类型","仓库名称", "单号","客户名称", "物料号","名称",
                "规格","单位","数量","收件人", "地址", "联系方式",
                "物流商", "物流单号", "批次", "序列号", "型号", "客户PO号"};
        File file = new File(FileUtil.getSDPath() + "/Pda");
        FileUtil.makeDir(file);
        String fileName = "出入库报表" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Pda/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "出入库报表");

        //ExcelUtils.writeObjListToExcel(getRecordData(reports), filePath);

        return fileXls;
    }

    public void exportRow(List<InOutboundReport> reports, String filePath, File fileXls) throws IOException, WriteException, BiffException {

        ExcelUtils.writeObjListToExcel(getRecordData(reports), filePath);

        FileUtils.notifySystemToScan(fileXls, app);

    }


    private  ArrayList<ArrayList<String>> getRecordData(List<InOutboundReport> reports) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i <reports.size(); i++) {
            InOutboundReport item = reports.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(item.getPostingDate());
            beanList.add(item.getDocumentType());
            beanList.add(item.getStoreLocationName());
            beanList.add(item.getOrderNumber());
            beanList.add(item.getCustomName());
            beanList.add(item.getMaterial());
            beanList.add(item.getMaterialName());
            beanList.add(item.getSpecifications());
            beanList.add(String.valueOf(item.getUnit()));
            beanList.add(String.valueOf(item.getQuantity()));
            beanList.add(item.getContacts());
            beanList.add(item.getAddress());
            beanList.add(item.getContactNumber());
            beanList.add(item.getLogisticsProvider());
            beanList.add(item.getLogisticsOrderNo());
            beanList.add(item.getBatch());
            beanList.add(item.getSerialNumber());
            beanList.add(item.getModel());
            beanList.add(item.getCustomPoNumber());
            recordList.add(beanList);
        }
        return recordList;
    }

    private String getDocumentTypeDesc(String documentType){
        if(StringUtils.equalsIgnoreCase(documentType, "1")){
            return app.getString(R.string.title_sales_invoice);
        }else if(StringUtils.equalsIgnoreCase(documentType, "2")){
            return app.getString(R.string.text_prototype_borrow);
        }else if(StringUtils.equalsIgnoreCase(documentType, "3")){
            return app.getString(R.string.text_picking);
        }else if(StringUtils.equalsIgnoreCase(documentType, "4")){
            return app.getString(R.string.text_picking_material);
        }else if(StringUtils.equalsIgnoreCase(documentType, "5")){
            return app.getString(R.string.title_transfer_order);
        }else if(StringUtils.equalsIgnoreCase(documentType, "6")){
            return app.getString(R.string.title_transfer_order_detail_receive);
        }else if(StringUtils.equalsIgnoreCase(documentType, "7")){
            return app.getString(R.string.text_po_receiving);
        }else if(StringUtils.equalsIgnoreCase(documentType, "8")){
            return app.getString(R.string.text_returning_material);
        }
        return "";
    }
}
