package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.OrderInvoiceOthersQuery;
import com.sunmi.pda.models.OrderInvoiceOthersResult;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.models.SalesInvoicePostingRequest;
import com.sunmi.pda.models.SalesInvoiceResult;
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

public class OrderInvoiceOthersController {
        protected static final String TAG = OrderInvoiceOthersController.class.getSimpleName();
        private final static AndroidApplication app = AndroidApplication.getInstance();
        private static final UserController userController = app.getUserController();

        public final static int ERROR_REQUIRE_FIELDS = 0;
        public final static int ERROR_QUANTITY_NOT_MATCH = 1;

        public List<OrderInvoiceOthersResult> syncData(OrderInvoiceOthersQuery query) throws Exception {
            if (query == null) {
                return null;
            }
            String locations = userController.getUserLocationString();
            String filter = buildFilter(query);
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_order_invoice_others) + app.getString(R.string.url_language_param)
                    + app.getString(R.string.sap_url_client) + "&$orderby=ReservedNo,ReservedItemNo"
                    + "&" + filter;
//            String url = "https://vhsscds4ci.sap.sunmi.com:44300/sap/opu/odata/sap/ZCDS_PDA_MATERIAL_ALL_CDS/ZCDS_PDA_MATERIAL_ALL(p_language='1')/Set?$format=json&sap-client=110"
//            + "&$orderby=ReservedNo,ReservedItemNo"
//                    + "&" + filter;

            LogUtils.d(TAG, "Url--->" + url);
            HttpRequestUtil http = new HttpRequestUtil();
            HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            //LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray jsonArray = d.getJSONArray("results");
            List<OrderInvoiceOthersResult> all = new ArrayList<>();
            for(int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject objectI = jsonArray.getJSONObject(i);
                    String reservedNo = objectI.getString("ReservedNo");
                    String reservedItemNo = objectI.getString("ReservedItemNo");
                    String applyType = objectI.getString("ApplyType");
                    String moveType = objectI.getString("MoveType");
                    String costCenter = objectI.getString("CostCenter");
                    String costCenterDesc = objectI.getString("CostCenterDesc");
                    long applyDate = DateUtils.jsonDateToTimeStamp(objectI.getString("ApplyDate"));
                    long downloadTime = DateUtils.jsonDateToTimeStamp( objectI.getString("DownloadTime"));
                    String applyPerson = objectI.getString("ApplyPerson");
                    String receivePerson = objectI.getString("ReceivePerson");
                    String receivePlace = objectI.getString("ReceivePlace");
                    String receiveContact = objectI.getString("ReceiveContact");
                    String materialNo = objectI.getString("MaterialNo");
                    String materialDesc = objectI.getString("MaterialDesc");
                    String spec = objectI.getString("Spec");
                    String batchNo = objectI.getString("BatchNo");
                    double quantity = objectI.getDouble("Quantity");
                    String factory = objectI.getString("Factory");
                    String factoryDesc = objectI.getString("FactoryDesc");
                    String storeLoc = objectI.getString("StoreLoc");
                    String storeLocDesc = objectI.getString("StoreLocDesc");
                    String receivingStockLocation = objectI.getString("ReceivingStockLocation");
                    String receivingStockLocationDesc = objectI.getString("ReceivingStockLocationDesc");
                    String serialFlag = objectI.getString("SerialFlag");
                    Double WithdrawalQuantity = Double.parseDouble(objectI.getString("WithdrawalQuantity"));
                    String shipmentStatus = objectI.getString("ShipmentStatus");
                    String RequiredDate = objectI.getString("RequiredDate");
                    String remark = objectI.getString("Remark");
                    String unit = objectI.getString("Unit");
                    String UnitDesciption = objectI.getString("UnitDesciption");
                    boolean batchFlag = objectI.getBoolean("BatchFlag");
                    String Model = objectI.getString("Model");
                    String CreateDate = objectI.getString("CreateDate");
                    String date = DateUtils.jsonDateToString(CreateDate);
                    String parameters = objectI.getString("Parameters");
                    String Logisticsprovider = objectI.getString("Logisticsprovider");

                    OrderInvoiceOthersResult orderBatchInvoiceResult = new OrderInvoiceOthersResult(reservedNo, reservedItemNo, applyType, moveType, costCenter, costCenterDesc,
                            applyDate, downloadTime, applyPerson, receivePerson, receivePlace, receiveContact, materialNo,
                            materialDesc, spec, batchNo, quantity, factory, factoryDesc, storeLoc, storeLocDesc, receivingStockLocation, receivingStockLocationDesc, serialFlag,
                            WithdrawalQuantity, shipmentStatus, RequiredDate, remark, unit, UnitDesciption, batchFlag, Model,date,parameters, Logisticsprovider);

                    if (userController.userHasAllFunction() || StringUtils.isEmpty(orderBatchInvoiceResult.getStoreLoc()) || StringUtils.containsIgnoreCase(locations, orderBatchInvoiceResult.getStoreLoc())) {
                        all.add(orderBatchInvoiceResult);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG, "get order invoice Error ------> " + e.getMessage());
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

        public String verifyQuery(OrderInvoiceOthersQuery query){
            if(query != null){
                if(StringUtils.isEmpty(query.getDeliveryDocument()) && StringUtils.isNotEmpty(query.getDeliveryDateTo())){
                    if(StringUtils.isEmpty(query.getDeliveryDateFrom())){
                        return app.getString(R.string.text_input_delivery_from_date);
                    }
                }
            }
            return null;
        }

        public int verifyData(List<SalesInvoice> list, String logisticNr, LogisticsProvider logisticsProvider, String userGroup){
            if ((StringUtils.equalsIgnoreCase(userGroup, app.getString(R.string.text_sunmi))
                    ||  StringUtils.equalsIgnoreCase(userGroup, app.getString(R.string.text_shanghai_sunmi)))
                    && (StringUtils.isEmpty(logisticNr) || logisticsProvider == null)){
                //sunmi require
                return ERROR_REQUIRE_FIELDS;
            }

            for(SalesInvoice salesInvoice : list){
                if (salesInvoice.getScanCount() - salesInvoice.getShipmentQuantity() != 0) {
                    return ERROR_QUANTITY_NOT_MATCH;
                }
            }

            //TODO: check duplicate serial for different items?

            return -1;
        }

        public HttpResponse posting(List<SalesInvoicePostingRequest> request) throws AuthorizationException, GeneralException {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_sales_invoice_posting) + app.getString(R.string.sap_url_client);
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

        public void exportExcel(List<OrderInvoiceOthersResult> orderBatchInvoiceResults) throws IOException, WriteException, BiffException {
            String[] title = { "单据日期","申请类型","库存地点","预留号","物料","物料描述","规格","型号", "单位",
                    "数量", "批次", "备注", "收货人","收货地","收货人联系方式",  "物流商"};
            //34个参数
            File file = new File(FileUtil.getSDPath() + "/Sunmi");
            FileUtil.makeDir(file);
            String fileName = "发货指令单" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
            String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
            File fileXls = new File(filePath);
            if (!fileXls.exists()) {
                fileXls.createNewFile();
            }
            ExcelUtils.initExcel(fileXls, filePath, title, "发货指令单");

            ExcelUtils.writeObjListToExcel(getRecordData(orderBatchInvoiceResults), filePath);
            FileUtils.notifySystemToScan(fileXls, app);
        }

        //TODO: TEST DATA
        private  ArrayList<ArrayList<String>> getRecordData(List<OrderInvoiceOthersResult> orderInvoiceOthersResults) {
            ArrayList<ArrayList<String>> recordList = new ArrayList<>();
            for (int i = 0; i <orderInvoiceOthersResults.size(); i++) {
                OrderInvoiceOthersResult item = orderInvoiceOthersResults.get(i);
                ArrayList<String> beanList = new ArrayList<String>();
                beanList.add(item.getCreateDate());
                beanList.add(item.getApplyType());
                beanList.add(item.getStoreLocDesc());
                beanList.add(item.getReservedNo());
                beanList.add(item.getMaterialNo());
                beanList.add(item.getMaterialDesc());
                beanList.add(item.getSpec());
                beanList.add(item.getModel());
                beanList.add(item.getUnit());
                beanList.add(String.valueOf(item.getQuantity()));
                beanList.add(item.getBatchNo());
                beanList.add(item.getRemark());
                beanList.add(item.getReceivePerson());
                beanList.add(item.getReceivePlace());
                beanList.add(item.getReceiveContact());
                beanList.add(item.getLogisticsprovider());

//                if (item.getShipmentQuantity() > 0) {
                    recordList.add(beanList);
//                }
            }
            return recordList;
        }

        public String buildFilter(OrderInvoiceOthersQuery query){
            String filter = "";
            String filterDeliveryDate = "";
            String filterStatus = "";

            String filterPlant = "";
            String filterLocations = "";

            if(StringUtils.isNotEmpty(query.getDeliveryDocument())){
                filter = Util.addFilter(filter, "(ReservedNo eq '" + query.getDeliveryDocument() + "')");
            }
            filterDeliveryDate = Util.getDateFilter("ApplyDate", query.getDeliveryDateFrom(), query.getDeliveryDateTo());
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
                            filterLocations = filterLocations + "StoreLoc eq '" + query.getStorageLocations().get(i) + "'";
                        }else{
                            filterLocations = filterLocations + "StoreLoc eq '" + query.getStorageLocations().get(i) + "' or ";
                        }
                    }
                }
                filterLocations +=")";
                filter =Util.addFilter(filter, filterLocations);
            }
            filterPlant = userController.getUserPlantsFilter("Factory");
            if(StringUtils.isNotEmpty(filterPlant)){
                filter =Util.addFilter(filter, filterPlant);
            }
            return filter;
        }
}
