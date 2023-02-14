package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.DeliveryStatus;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.models.SalesInvoiceResult;
import com.sunmi.pda.models.SalesInvoicePostingRequest;
import com.sunmi.pda.models.SalesInvoiceSN;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.ExcelUtils;
import com.sunmi.pda.utils.FileUtil;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class SalesInvoiceController {
    protected static final String TAG = SalesInvoiceController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();

    public final static int ERROR_REQUIRE_FIELDS = 0;
    public final static int ERROR_QUANTITY_NOT_MATCH = 1;

    public List<SalesInvoice> syncData(SalesInvoiceQuery query) throws Exception {
        if (query == null) {
            return null;
        }
        String locations = userController.getUserLocationString();
        String filter = buildFilter(query);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_sales_invoice) + app.getString(R.string.url_language_param)
                + app.getString(R.string.sap_url_client); //+ "&$orderby=DeliveryDocument,DeliveryDocumentItem"
//                + "&" + filter;

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        //LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<SalesInvoice> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String deliveryDocument = objectI.getString("DeliveryDocument");
                String deliveryDocumentItem = objectI.getString("DeliveryDocumentItem");
                String shipToParty = objectI.getString("ShipToParty");
                String shipToPartyName = objectI.getString("ShipToPartyName");
                String shipToPartyAddress = objectI.getString("ShipToPartyAddress");
                long downloadDocumentTime = DateUtils.jsonDateToTimeStamp( objectI.getString("DownloadDocumentTime"));

                String material = objectI.getString("Material");
                String materialDescribe = objectI.getString("MaterialDescribe");
                String receivingPlant = objectI.getString("ReceivingPlant");
                String receivingPlantDescribe = objectI.getString("ReceivingPlantDescribe");
                String inventoryLocation = objectI.getString("InventoryLocation");
                String inventoryLocationDescribe = objectI.getString("InventoryLocationDescribe");
                String unit = objectI.getString("Unit");
                Double shipmentQuantity = objectI.getDouble("ShipmentQuantity");
                String batch = objectI.getString("Batch");
                String supplier = objectI.getString("Supplier");
                String shipmentStatus = objectI.getString("ShipmentStatus");
                long plannedDeliveryDate = DateUtils.jsonDateToTimeStamp( objectI.getString("PlannedDeliveryDate"));
                String serialFlag = objectI.getString("SerialFlag");

                String contacts = objectI.getString("Contacts");
                String contactNumber = objectI.getString("ContactNumber");
                String remark = objectI.getString("remark");
                String logisticsVendor = objectI.getString("LogisticsVendor");
                boolean batchFlag = objectI.getBoolean("BatchFlag");
                String model = objectI.getString("Model");
                if(batchFlag){
                    if(StringUtils.isEmpty(batch)){
                        batch = "";
                    }
                }else{
                    batch = "";
                }

                String CreateDate = objectI.getString("CreateDate");
                String date = DateUtils.jsonDateToString(CreateDate);
                String ItemRemark = objectI.getString("ItemRemark");
                String RestruRemark = objectI.getString("RestruRemark");
                String CustomPoNumber = objectI.getString("CustomPoNumber");
                String CustomMaterial = objectI.getString("CustomMaterial");
                String TransportMode = objectI.getString("TransportMode");
                String ShippingInfo = objectI.getString("ShippingInfo");
                String Specs = objectI.getString("Specs");

                SalesInvoice salesInvoice = new SalesInvoice(deliveryDocument, deliveryDocumentItem, shipToParty, shipToPartyName, shipToPartyAddress, downloadDocumentTime,
                        material, materialDescribe, receivingPlant, receivingPlantDescribe, inventoryLocation, inventoryLocationDescribe, unit, shipmentQuantity,
                        batch, supplier, shipmentStatus, plannedDeliveryDate, serialFlag, remark, contacts, contactNumber, 0, logisticsVendor, batchFlag, model,
                        date, ItemRemark, RestruRemark, CustomPoNumber, CustomMaterial, TransportMode, ShippingInfo, Specs);
                if(StringUtils.isEmpty(serialFlag)){
                    double q = Double.valueOf(shipmentQuantity);
                    salesInvoice.setScanCount((int) q);

                }
                if (userController.userHasAllFunction() || StringUtils.isEmpty(salesInvoice.getStorageLocation()) || StringUtils.containsIgnoreCase(locations, salesInvoice.getStorageLocation())) {
                    all.add(salesInvoice);
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "get sales invoice Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public List<SalesInvoiceSN> getSN(String deliveryDocument) throws Exception {
        if (deliveryDocument == null) {
            return null;
        }
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_sales_invoice_sn)
                + app.getString(R.string.sap_url_client) + "&$filter=DeliveryDocument eq '"
                + deliveryDocument + "'";

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<SalesInvoiceSN> all = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String dn = objectI.getString("DeliveryDocument");
                String deliveryDocumentItem = objectI.getString("DeliveryDocumentItem");
                String serialNo = objectI.getString("SerialNo");
                SalesInvoiceSN salesInvoiceSN = new SalesInvoiceSN(dn, deliveryDocumentItem, serialNo);
                all.add(salesInvoiceSN);

            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "get sales invoice Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public String verifySalesInvoiceResult(List<SalesInvoice> list){
        String error = "";
        if (list.size() == 0) {
            error = app.getString(R.string.text_service_no_result);
        }
        for(SalesInvoice salesInvoice : list){
           if (StringUtils.equalsIgnoreCase(salesInvoice.getShipmentStatus(),"C")){
               error = app.getString(R.string.error_sales_invoice_status_c);
               break;
           }
        }
        return error;
    }
    
    public String verifyQuery(SalesInvoiceQuery query){
        if(query != null){
            if(StringUtils.isEmpty(query.getDeliveryDocument()) && StringUtils.isNotEmpty(query.getDeliveryDateTo())){
                if(StringUtils.isEmpty(query.getDeliveryDateFrom())){
                    return app.getString(R.string.text_input_delivery_from_date);
                }
            }
        }
        return null;
    }

    public int verifyData(List<SalesInvoice> list, String logisticNr, LogisticsProvider logisticsProvider, String userGroup, boolean isTemp){
        if (!isTemp) {
            if ((StringUtils.equalsIgnoreCase(userGroup, app.getString(R.string.text_sunmi))
                    ||  StringUtils.equalsIgnoreCase(userGroup, app.getString(R.string.text_shanghai_sunmi)))
                    && (StringUtils.isEmpty(logisticNr) || logisticsProvider == null)){
                //sunmi require
                return ERROR_REQUIRE_FIELDS;
            }

            for (SalesInvoice salesInvoice : list) {
                if (salesInvoice.getScanCount() - salesInvoice.getShipmentQuantity() != 0) {
                    return ERROR_QUANTITY_NOT_MATCH;
                }
            }
        }

        return -1;
    }

    /**
     * 调用pda_outb_dn，销售发货过账
     * @param request
     * @param isTemp: 暂存，true调用pda_outb_dn_chg，false调用pda_outb_dn
     * @return
     * @throws AuthorizationException
     * @throws GeneralException
     */
    public HttpResponse posting(List<SalesInvoicePostingRequest> request, boolean isTemp) throws AuthorizationException, GeneralException {
        String url = "";
        if (isTemp) {
            url = app.getOdataService().getHost() + app.getString(R.string.sap_url_sales_invoice_posting_temp) + app.getString(R.string.sap_url_client);
        } else {
            url = app.getOdataService().getHost() + app.getString(R.string.sap_url_sales_invoice_posting) + app.getString(R.string.sap_url_client);
        }
        return callAPI(request, url);
    }

    /**
     * 销售发货过账、暂存核心方法，post数据使用相同结构
     * @param request
     * @param url
     * @return
     * @throws AuthorizationException
     * @throws GeneralException
     */
    public HttpResponse callAPI(List<SalesInvoicePostingRequest> request, String url) throws AuthorizationException, GeneralException {
        HttpRequestUtil http = new HttpRequestUtil();

        String arrStr = JSON.toJSONString(request);
        String json = "{ \"data\": " + arrStr + " }";
        //json = Util.rawRead(app);
        LogUtils.d("SalesInvoicePosting", "url--->" + url);
        LogUtils.d("SalesInvoicePosting", "POST--->" + json);

        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("SalesInvoicePosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("SalesInvoicePosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            String errorMessage = parseSalesInvoiceResponse(httpResponse.getResponseString());
            LogUtils.e("SalesInvoicePosting", "errorMessage-->" + errorMessage);
            httpResponse.setError(errorMessage);
        }else{
            String msg = parseSalesInvoiceResponse(httpResponse.getResponseString());
            httpResponse.setResponseString(msg);
        }
        return httpResponse;
    }

    private static String parseSalesInvoiceResponse(String responseString)throws GeneralException{
        String msg = "";
        try{
            JSONObject jsonObject = JSONObject.parseObject(responseString);
            JSONArray jsonArray = jsonObject.getJSONArray("objectMsg");
            if (jsonArray == null){
                msg = app.getString(R.string.text_service_failed);
                return msg;
            }
            for(int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject objectI = jsonArray.getJSONObject(i);
                    String type = objectI.getString("type");
                    msg = objectI.getString("message");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            throw new GeneralException();
        }

        return msg;
    }

    /**
     * 按单据号分组
     * @param salesInvoices
     * @return
     */
    public List<SalesInvoiceResult> groupSalesInvoice(List<SalesInvoice> salesInvoices){
        String date = "";
        String status = "";
        Map<String, List<SalesInvoice>> groupBy = salesInvoices.stream().collect(Collectors.groupingBy(SalesInvoice::getDeliveryDocument));

        List<SalesInvoiceResult> resultList = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            List<SalesInvoice> value = groupBy.get(key);
            if (value.size() > 0 ) {
                status = StringUtils.equalsIgnoreCase(value.get(0).getShipmentStatus(),"C") ? "Y" : "N";
                date = DateUtils.dateToString(new Date(value.get(0).getPlannedDeliveryDate()), DateUtils.FormatFullDate);
                SalesInvoiceResult result = new SalesInvoiceResult(key, date, "N", value);
                resultList.add(result);
            }
        }
        resultList = resultList.stream().sorted(Comparator.comparing(SalesInvoiceResult::getDeliveryDocument)).collect(Collectors.toList());

        return resultList;
    }

    public void exportExcel(List<SalesInvoice> salesInvoices) throws IOException, WriteException, BiffException {
        String[] title = { "单据日期","单据类型","仓库名称","单号","客户名称","物料号","名称",
                "规格", "型号", "单位", "数量","批次", "表头备注", "行备注", "改制备注", "收件人", "地址", "联系方式",
                "物流商", "客户PO号", "客户物料号", "运输方式", "般务信息补充"};
        File file = new File(FileUtil.getSDPath() + "/Sunmi");
        FileUtil.makeDir(file);
        String fileName = "销售发货单" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "销售发货单");

        ExcelUtils.writeObjListToExcel(getRecordData(salesInvoices), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }

    //TODO: TEST DATA
    private  ArrayList<ArrayList<String>> getRecordData(List<SalesInvoice> salesInvoices) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i <salesInvoices.size(); i++) {
            SalesInvoice item = salesInvoices.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(item.getCreateDate());
            beanList.add(app.getString(R.string.title_sales_invoice));
            beanList.add(item.getInventoryLocationDescribe());
            beanList.add(item.getDeliveryDocument());
            beanList.add(item.getShipToPartyName());
            beanList.add(item.getMaterial());
            beanList.add(item.getMaterialDescribe());
            beanList.add(item.getSpecs());
            beanList.add(item.getModel());
            beanList.add(item.getUnit());
            beanList.add(String.valueOf(item.getShipmentQuantity()));
            beanList.add(item.getBatch());
            beanList.add(item.getRemark());
            beanList.add(item.getItemRemark());
            beanList.add(item.getRestruRemark());
            beanList.add(item.getContacts());
            beanList.add(item.getShipToPartyAddress());
            beanList.add(item.getContactNumber());
            beanList.add(item.getLogisticsVendor());
            beanList.add(item.getCustomPoNumber());
            beanList.add(item.getCustomMaterial());
            beanList.add(item.getTransportMode());
            beanList.add(item.getShippingInfo());
            if (item.getShipmentQuantity() > 0) {
                recordList.add(beanList);
            }
        }
        return recordList;
    }

    public String buildFilter(SalesInvoiceQuery query){
        String filter = "";
        String filterDeliveryDate = "";
        String filterStatus = "";

        String filterPlant = "";
        String filterLocations = "";

        if(StringUtils.isNotEmpty(query.getDeliveryDocument())){
            filter = Util.addFilter(filter, "(DeliveryDocument eq '" + query.getDeliveryDocument() + "')");
        }
        filterDeliveryDate = Util.getDateFilter("PlannedDeliveryDate", query.getDeliveryDateFrom(), query.getDeliveryDateTo());
        filter =Util.addFilter(filter, filterDeliveryDate);
        System.out.println("query.getDeliveryStatuses()--->" + query.getDeliveryStatuses());
        if(query.getDeliveryStatuses() != null && query.getDeliveryStatuses().size() > 0){
            filterStatus = "(";
            for(int i = 0; i < query.getDeliveryStatuses().size(); i ++){
                if(query.getDeliveryStatuses().get(i) != null){
                    if(i == query.getDeliveryStatuses().size() - 1){
                        filterStatus = filterStatus + "ShipmentStatus eq '" + query.getDeliveryStatuses().get(i).getCode() + "'";
                    }else{
                        filterStatus = filterStatus + "ShipmentStatus eq '" + query.getDeliveryStatuses().get(i).getCode() + "' or ";
                    }
                }
            }
            filterStatus +=")";
            filter =Util.addFilter(filter, filterStatus);
        }
       /* if(StringUtils.isNotEmpty(query.getDeliveryStatus())){
            filterStatus = "(ShipmentStatus eq '" + query.getDeliveryStatus() + "')";
            filter =Util.addFilter(filter, filterStatus);
        }
*/
        System.out.println("query.getStorageLocations()--->" + query.getStorageLocations());
        if(query.getStorageLocations() != null && query.getStorageLocations().size() > 0){
            filterLocations = "(";
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
            filter =Util.addFilter(filter, filterLocations);
        }
        filterPlant = userController.getUserPlantsFilter("ReceivingPlant");
        if(StringUtils.isNotEmpty(filterPlant)){
            filter =Util.addFilter(filter, filterPlant);
        }
        return filter;
    }
}
