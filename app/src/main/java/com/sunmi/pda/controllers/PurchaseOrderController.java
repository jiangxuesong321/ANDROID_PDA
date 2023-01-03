package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.MaterialDocumentItem;
import com.sunmi.pda.models.MaterialDocumentItemResults;
import com.sunmi.pda.models.PurchaseOrderPostingRequest;
import com.sunmi.pda.models.PurchaseOrderQuery;
import com.sunmi.pda.models.PurchaseOrderResult;
import com.sunmi.pda.models.PurchaseOrderReturnPostingRequest;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberPoReturn;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.ComparatorItem;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.ExcelUtils;
import com.sunmi.pda.utils.FileUtil;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;

public class PurchaseOrderController {
    protected static final String TAG = PurchaseOrderController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();
    private static final UserController userController = app.getUserController();
    public List<PurchaseOrder> syncData(PurchaseOrderQuery purchaseOrderQuery) throws AuthorizationException, GeneralException {
        String filter = "";
        String filterCreateDate = "";
        String filterDeliveryDate = "";
        String filterSupplier = "";
        if(purchaseOrderQuery != null){
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getPurchaseOrder())){
                filter = "$filter=(PurchaseOrder eq '" + purchaseOrderQuery.getPurchaseOrder() + "')";
            }
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateTo())
                    || StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateFrom())){
                if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateTo())){
                    String createDateFrom = purchaseOrderQuery.getCreateDateFrom() + "T00:00:00";
                    String createDateTo = purchaseOrderQuery.getCreateDateTo() + "T00:00:00";
                    filterCreateDate = "(CreationDate ge datetime'" + createDateFrom
                            + "' and CreationDate le datetime'" + createDateTo + "' ) ";
                }else{
                    if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateFrom())){
                        String createDateFrom = purchaseOrderQuery.getCreateDateFrom() + "T00:00:00";
                        filterCreateDate = "(CreationDate ge datetime'" + createDateFrom +"' ) ";
                    }
                }
                if(StringUtils.isNotEmpty(filter)){
                    filter = filter + " and " + filterCreateDate;
                }else{
                    filter = "$filter="  + filterCreateDate;
                }
            }
           if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateTo())
                   || StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateFrom())){
               if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateTo())){
                   String deliveryDateFrom = purchaseOrderQuery.getDeliveryDateFrom() + "T00:00:00";
                   String deliveryDateTo = purchaseOrderQuery.getDeliveryDateTo() + "T00:00:00";
                   filterDeliveryDate = "(DeliveryDate ge datetime'" + deliveryDateFrom
                           + "' and DeliveryDate le datetime'" + deliveryDateTo + "' ) ";
               }else{
                   if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateFrom())){
                       String deliveryDateFrom = purchaseOrderQuery.getDeliveryDateFrom() + "T00:00:00";
                       filterDeliveryDate = "(DeliveryDate ge datetime'" + deliveryDateFrom +"' ) ";
                   }
               }
               if(StringUtils.isNotEmpty(filter)){
                   filter = filter + " and " + filterDeliveryDate;
               }else{
                   filter = "$filter="  + filterDeliveryDate;
               }
           }
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getSupplier())){
                filterSupplier = purchaseOrderQuery.getSupplier();
                if(StringUtils.isNotEmpty(filter)){
                    filter = filter + " and " + "(Supplier eq '" + filterSupplier + "')";
                }else{
                    filter = "$filter="  + "(Supplier eq '" + filterSupplier + "')";
                }
            }
        }

        String filterPlant = userController.getUserPlantsFilter("Plant");
        if(StringUtils.isNotEmpty(filterPlant)){
            filter = Util.addFilter(filter, filterPlant);
        }

        if(StringUtils.isNotEmpty(filter)){
            filter = "&" + filter;
        }
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po) + app.getString(R.string.sap_url_client)
                + filter + "&$orderby=PurchaseOrder, PurchaseOrderItem";

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<PurchaseOrder> all = new ArrayList<>();
        String locations = userController.getUserLocationString();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String poNr = objectI.getString("PurchaseOrder");
                String poItem = objectI.getString("PurchaseOrderItem");
                String supplier = objectI.getString("Supplier");
                long creationDate = DateUtils.jsonDateToTimeStamp(objectI.getString("CreationDate"));
                String materialNr = objectI.getString("Material");
                String purchaseOrderItemText = objectI.getString("PurchaseOrderItemText");
                String plant = objectI.getString("Plant");
                String storageLocation = objectI.getString("StorageLocation");
                long deliveryDate = DateUtils.jsonDateToTimeStamp(objectI.getString("DeliveryDate"));
                String unit = objectI.getString("Unit");
                double orderQuantity = objectI.getDouble("OrderQuantity");
                double openQuantity = objectI.getDouble("OpenQuantity");
                boolean batchFlag = objectI.getBoolean("BatchFlag");
                String model = objectI.getString("Model");
                boolean poreturnind = objectI.getBoolean("poreturnind");

                if(openQuantity <= 0){
                    continue;
                }
                String serialFlag = objectI.getString("SerialFlag");

                PurchaseOrder po = new PurchaseOrder(poNr, poItem, supplier, creationDate, materialNr,
                        purchaseOrderItemText, plant, deliveryDate, unit, orderQuantity, openQuantity,
                        storageLocation, batchFlag, model, serialFlag, poreturnind) ;
                if(batchFlag){
                    po.setBatch(DateUtils.dateToString(new Date(), DateUtils.FormatYMD) + "01");
                }
                if (userController.userHasAllFunction() || StringUtils.isEmpty(storageLocation)
                        || StringUtils.containsIgnoreCase(locations, storageLocation)) {
                    all.add(po);
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "fetch purchase order Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public List<PurchaseOrderResult> groupPurchase(List<PurchaseOrder> purchaseOrders){
        String date = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
        Map<String, List<PurchaseOrder>> groupBy = purchaseOrders.stream().collect(Collectors.groupingBy(PurchaseOrder::getPurchaseOrder));

        List<PurchaseOrderResult> purchaseOrderResultList = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            List<PurchaseOrder> value = groupBy.get(key);
            PurchaseOrderResult purchaseOrderResult = new PurchaseOrderResult(key, date, "N", value);
            purchaseOrderResultList.add(purchaseOrderResult);

        }
        purchaseOrderResultList = purchaseOrderResultList.stream().sorted(Comparator.comparing(PurchaseOrderResult::getPurchaseOrder)).collect(Collectors.toList());

        return purchaseOrderResultList;
    }

    public PurchaseOrderPostingRequest buildRequest(List<PurchaseOrder> purchaseOrders, String deliveryNumber,
                                                    String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "01";
        String materialDocumentHeaderText = deliveryNumber;
        List<MaterialDocumentItemResults> results = new ArrayList<>();
        for(PurchaseOrder purchaseOrder : purchaseOrders){
            if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                continue;
            }
            String material = purchaseOrder.getMaterial();
            String plant = purchaseOrder.getPlant();
            String storageLocation = purchaseOrder.getStorageLocation();
            String goodsMovementRefDocType = "B";
            String goodsMovementType = "101";
            String quantityInEntryUnit = purchaseOrder.getQuantityInEntryUnit();
            String purchaseOrderNr = purchaseOrder.getPurchaseOrder();
            String purchaseOrderItem = purchaseOrder.getPurchaseOrderItem();
            String batch = purchaseOrder.getBatch();
            //SerialNumber serialNumber = new SerialNumber();
            SerialNumber serialNumber = new SerialNumber();

            List<SerialNumberResults> serialNumberResults = new ArrayList<>();
            if(purchaseOrder.getSnList() != null){
                for(String sn : purchaseOrder.getSnList()){
                    SerialNumberResults serialNumberResult = new SerialNumberResults(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }
            serialNumber.setResults(serialNumberResults);
            MaterialDocumentItemResults itemResults = new MaterialDocumentItemResults(material, plant,
                    storageLocation, goodsMovementRefDocType, goodsMovementType, quantityInEntryUnit,
                    purchaseOrderNr, purchaseOrderItem, batch, serialNumber);

            results.add(itemResults);
        }
        MaterialDocumentItem documentItem = new MaterialDocumentItem(results);
        PurchaseOrderPostingRequest request = new PurchaseOrderPostingRequest(documentDate, postingDate,
                goodsMovementCode, "7", materialDocumentHeaderText, documentItem);
        return request;
    }

    public HttpResponse posting(PurchaseOrderPostingRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        //Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PurchaseOrderPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        LogUtils.d("PurchaseOrderPosting", "url--->" + url);

        String json = JSON.toJSONString(request);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("PurchaseOrderPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PurchaseOrderPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONArray array = jsonObject.getJSONArray("objectMsg");
            if(array != null && array.size() > 0){
                JSONObject object = (JSONObject)array.get(0);
                if(object != null){
                    String errorMessage = object.getString("message");
                    LogUtils.e("PurchaseOrderPosting", "errorMessage-->" + errorMessage);
                    httpResponse.setError(errorMessage);
                }
            }
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());

            String MaterialDocument = jsonObject.getString("matDoc");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }

    public String verifyData(List<PurchaseOrder> purchaseOrderList){
        //System.out.println("purchaseOrderList---->" + JSON.toJSONString(purchaseOrderList));
        String error = "";
        double sum = 0;
        //未收货数量
        double openQuantity = 0;
        List<PurchaseOrder> filtered = purchaseOrderList.stream().filter(s->!StringUtils.isEmpty(s.getQuantityInEntryUnit())).collect(Collectors.toList());
        if(filtered == null || filtered.size() == 0){
            error = app.getString(R.string.text_less_receive);
            return error;
        }
        for(PurchaseOrder purchaseOrder : purchaseOrderList){
            if(purchaseOrder.isSubOrder()){
                System.out.println("isSubOrder---s-");
                if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                    error = app.getString(R.string.text_input_quantity);
                    return error;
                }
            }
            if(purchaseOrder.isBatchFlag()){
                if(StringUtils.isEmpty(purchaseOrder.getBatch())){
                    error = app.getString(R.string.text_input_batch);
                    return error;
                }
            }


            /*if(Double.valueOf(purchaseOrder.getQuantityInEntryUnit()) <= 0){
                error = app.getString(R.string.text_greater_than_zero);
                return error;
            }*/
            if(!purchaseOrder.isSubOrder()){
                sum = Double.valueOf(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())? "0" : purchaseOrder.getQuantityInEntryUnit());
            }else{
                if(purchaseOrder.isSubOrder()){
                    sum += Double.valueOf(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())? "0" : purchaseOrder.getQuantityInEntryUnit());
                }
            }
            openQuantity = purchaseOrder.getOpenQuantity();

            if(sum > openQuantity){
                error = app.getString(R.string.text_excess);
                return error;
            }
        }
        return error;
    }

    public String verifyData(PurchaseOrderQuery purchaseOrderQuery){
        if(purchaseOrderQuery != null){
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateTo())){
                if(StringUtils.isEmpty(purchaseOrderQuery.getDeliveryDateFrom())){
                   return app.getString(R.string.text_input_delivery_from_date);
                }
            }
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateTo())){
                if(StringUtils.isEmpty(purchaseOrderQuery.getCreateDateFrom())){
                    return app.getString(R.string.text_input_create_from_date);
                }
            }
        }
        return null;
    }

    public List<PurchaseOrder> splitPurchaseOrder(List<PurchaseOrder> purchaseOrderList, PurchaseOrder splitPurchaseOrder){

        PurchaseOrder sub = new PurchaseOrder(splitPurchaseOrder.getPurchaseOrder(), splitPurchaseOrder.getPurchaseOrderItem(),
                splitPurchaseOrder.getSupplier(), splitPurchaseOrder.getCreationDate(),
                splitPurchaseOrder.getMaterial(), splitPurchaseOrder.getPurchaseOrderItemText(),
                splitPurchaseOrder.getPlant(), splitPurchaseOrder.getDeliveryDate(),
                splitPurchaseOrder.getUnit(), splitPurchaseOrder.getOrderQuantity(),
                splitPurchaseOrder.getOpenQuantity(), splitPurchaseOrder.getStorageLocation(),
                splitPurchaseOrder.isBatchFlag(), splitPurchaseOrder.getModel(),
                splitPurchaseOrder.getSerialFlag(), splitPurchaseOrder.isPoReturnInd());
        sub.setSubOrder(true);
        purchaseOrderList.add(sub);
        Comparator<PurchaseOrder> comp = new ComparatorItem();
        Collections.sort(purchaseOrderList, comp);
        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return purchaseOrderList;
    }

    public void exportExcel(List<PurchaseOrder> purchaseOrderList) throws IOException, WriteException, BiffException {
        String[] title = { "PurchaseOrder","PurchaseOrderItem","Supplier","CreationDate",
                "Material","PurchaseOrderItemText","Plant","StorageLocation", "DeliveryDate",
                "Unit", "OrderQuantity", "OpenQuantity"};
        File file = new File(FileUtil.getSDPath() + "/Sunmi");
        FileUtil.makeDir(file);
        String fileName = "采购单" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "采购单");


        ExcelUtils.writeObjListToExcel(getRecordData(purchaseOrderList), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }
    private  ArrayList<ArrayList<String>> getRecordData(List<PurchaseOrder> purchaseOrderList) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i <purchaseOrderList.size(); i++) {
            PurchaseOrder purchaseOrder = purchaseOrderList.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(purchaseOrder.getPurchaseOrder());
            beanList.add(purchaseOrder.getPurchaseOrderItem());
            beanList.add(purchaseOrder.getSupplier());
            beanList.add(DateUtils.dateToString(new Date(purchaseOrder.getCreationDate()), DateUtils.FormatFullDate).replace(" ", "T"));
            beanList.add(purchaseOrder.getMaterial());
            beanList.add(purchaseOrder.getPurchaseOrderItemText());
            beanList.add(purchaseOrder.getPlant());
            beanList.add(purchaseOrder.getStorageLocation());
            beanList.add(DateUtils.dateToString(new Date(purchaseOrder.getDeliveryDate()), DateUtils.FormatFullDate).replace(" ", "T"));
            beanList.add(purchaseOrder.getUnit());
            beanList.add(String.valueOf(purchaseOrder.getOrderQuantity()));
            beanList.add(String.valueOf(purchaseOrder.getOpenQuantity()));
            recordList.add(beanList);
        }
        return recordList;
    }

    public List<PurchaseOrder> syncReturnData(PurchaseOrderQuery purchaseOrderQuery) throws AuthorizationException, GeneralException {

        List<PurchaseOrder> all = new ArrayList<>();
        try{
            String filter = "";

            String filterYear = "";
            if(purchaseOrderQuery != null){
                if(StringUtils.isNotEmpty(purchaseOrderQuery.getPurchaseOrder())){
                    filter = "$filter=(MATERIALDOCUMENT eq '" + purchaseOrderQuery.getPurchaseOrder() + "')";
                }

                if(StringUtils.isNotEmpty(purchaseOrderQuery.getYear())){
                    filterYear = purchaseOrderQuery.getYear();
                    if(StringUtils.isNotEmpty(filter)){
                        filter = filter + " and " + "(MaterialDocYear eq '" + filterYear + "')";
                    }else{
                        filter = "$filter="  + "(MaterialDocYear eq '" + filterYear + "')";
                    }
                }
            }

            String filterPlant = userController.getUserPlantsFilter("plant");
            if(StringUtils.isNotEmpty(filterPlant)){
                filter = Util.addFilter(filter, filterPlant);
            }

            if(StringUtils.isNotEmpty(filter)){
                filter = "&" + filter;
            }
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_return)
                    + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client)
                    + filter + "&$orderby=MATERIALDOCUMENT,MATERIALDOCUMENTITEM";

            LogUtils.d(TAG, "Url--->" + url);
            HttpRequestUtil http = new HttpRequestUtil();
            HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray jsonArray = d.getJSONArray("results");

            String locations = userController.getUserLocationString();
            for(int i = 0; i < jsonArray.size(); i++) {
                try {
                    JSONObject objectI = jsonArray.getJSONObject(i);

                    String materialDocument = objectI.getString("MATERIALDOCUMENT");
                    String materialDocumentItem = objectI.getString("MATERIALDOCUMENTITEM");
                    String purchaseOrder = objectI.getString("PurchaseNo");
                    String purchaseOrderItem = objectI.getString("PurchaseItemNo");
                    String material = objectI.getString("materialID");
                    String purchaseOrderItemText = objectI.getString("materialText");
                    String plant = objectI.getString("plant");
                    String storageLocation = objectI.getString("storagelocation");

                    double openQuantity = objectI.getDouble("quantity");
                    boolean batchFlag = objectI.getBoolean("BatchInd");

                    String batch = objectI.getString("batchId");
                    String movementType = objectI.getString("movementType");
                    String debitCreditind = objectI.getString("DebitCreditind");
                    String serialFlag = objectI.getString("serialind");
                    if(openQuantity <= 0){
                        continue;
                    }

                    PurchaseOrder po = new PurchaseOrder(materialDocument, materialDocumentItem, movementType,
                            debitCreditind, purchaseOrder, purchaseOrderItem, material, purchaseOrderItemText,
                            plant, openQuantity, storageLocation, batch, batchFlag, serialFlag) ;
                    if(batchFlag){
                        if(StringUtils.isNotEmpty(batch)){
                            po.setBatch(batch);
                        }
                    }
                    if (userController.userHasAllFunction() || StringUtils.isEmpty(storageLocation)
                            || StringUtils.containsIgnoreCase(locations, storageLocation)) {
                        all.add(po);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG, "fetch purchase order Error ------> " + e.getMessage());
                }
            }
        }catch (Exception e){
            throw new GeneralException();
        }

        System.out.println("all---->" + all.size());
        return all;
    }

    public List<PurchaseOrderReturnPostingRequest> buildReturnRequest(List<PurchaseOrder> purchaseOrders,
                                                    String postingDate){
        List<PurchaseOrderReturnPostingRequest> requestList = new ArrayList<>();
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementType = "122";
        for(PurchaseOrder order : purchaseOrders){
            if(StringUtils.isEmpty(order.getQuantityInEntryUnit())){
                continue;
            }
            String materialDocument = order.getMaterialDocument();
            String materialDocumentItem = order.getMaterialDocumentItem();
            String material = order.getMaterial();
            String plant = order.getPlant();
            String storageLocation = order.getStorageLocation();
            String quantityInEntryUnit = order.getQuantityInEntryUnit();
            String batch = order.getBatch();
            String reason = order.getReason();
            List<SerialNumberPoReturn> serialNumberList = new ArrayList<>();
            for(String sn : order.getSnList()){
                SerialNumberPoReturn serialNumberPoReturn = new SerialNumberPoReturn(sn);
                serialNumberList.add(serialNumberPoReturn);
            }
            String documentType = "13";
            PurchaseOrderReturnPostingRequest request = new PurchaseOrderReturnPostingRequest(postingDate,
                    materialDocument, documentDate, goodsMovementType, materialDocumentItem,
                    material, plant, storageLocation, quantityInEntryUnit, batch, reason, documentType, serialNumberList);
            requestList.add(request);
        }

        return requestList;
    }

    public HttpResponse postingReturn(List<PurchaseOrderReturnPostingRequest> request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_return_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        //Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PurchaseOrderPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        LogUtils.d("PurchaseOrderReturnPosting", "url--->" + url);

        String json = JSON.toJSONString(request);
        LogUtils.d("PurchaseOrderReturnPosting", "json--->" + json);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("PurchaseOrderReturnPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PurchaseOrderReturnPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONArray array = jsonObject.getJSONArray("objectMsg");
            if(array != null && array.size() > 0){
                JSONObject object = (JSONObject)array.get(0);
                if(object != null){
                    String errorMessage = object.getString("message");
                    LogUtils.e("PurchaseOrderPosting", "errorMessage-->" + errorMessage);
                    httpResponse.setError(errorMessage);
                }
            }
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());

            String MaterialDocument = jsonObject.getString("matDoc");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }
}
