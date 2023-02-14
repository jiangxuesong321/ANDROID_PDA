package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.GeneralMaterialDocumentItem;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.BusinessOrderQuery;

import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.utils.ComparatorItem;
import com.sunmi.pda.utils.ComparatorPrototypeBorrowItem;
import com.sunmi.pda.utils.ComparatorTransferOrderItem;
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

public class PrototypeBorrowController {
    protected static final String TAG = PrototypeBorrowController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();
    public List<PrototypeBorrow> syncData(BusinessOrderQuery query) throws Exception {
        String filter = buildFilter(query);  //"$filter=ReservationNumber eq '" + query.getNumber() + "'";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_prototype_borrow) + app.getString(R.string.url_language_param)  + app.getString(R.string.sap_url_client);
//        if (filter.length() > 0) {
//            url = url + "&" + filter;
//        }
        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<PrototypeBorrow> all = new ArrayList<>();
        String locations = userController.getUserLocationString();
        List<StorageLocation> storageLocations = userController.getUserLocation();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String reservationNumber = objectI.getString("ReservationNumber");
                String applicationType = objectI.getString("ApplicationType");
                String movementType = objectI.getString("MovementType");
                String applicationDate = objectI.getString("ApplicationDate");
                Long applicationDateLong = null;
                if(StringUtils.isEmpty(applicationDate)){
                    applicationDateLong = DateUtils.jsonDateToTimeStamp(applicationDate);
                }
                String downloadTime = objectI.getString("DownloadTime");
                Long downloadTimeLong = null;
                if(StringUtils.isEmpty(applicationDate)){
                    downloadTimeLong = DateUtils.jsonDateToTimeStamp(downloadTime);
                }

                String applicant = objectI.getString("Applicant");
                String consignee = objectI.getString("Consignee");
                String receiptPlace = objectI.getString("ReceiptPlace");
                String consigneeContact = objectI.getString("ConsigneeContact");
                String reservedItemNo = objectI.getString("ReservedItemNo");
                String material = objectI.getString("Material");
                String materialDescription = objectI.getString("MaterialDescription");
                String specs = objectI.getString("Specs");

                String batch = objectI.getString("Batch");
                /*if(StringUtils.isEmpty(batch)){
                    batch = DateUtils.dateToString(new Date(), DateUtils.FormatYMD) + "01";
                }*/
                double quantity = objectI.getDouble("Quantity");
                String factory = objectI.getString("Factory");
                String factoryDescription = objectI.getString("FactoryDescription");
                String deliveryStockLocation = objectI.getString("DeliveryStockLocation");
                String deliveryStockLocationDesc = objectI.getString("DeliveryStockLocationDesc");
                String receivingStockLocation = objectI.getString("ReceivingStockLocation");
                String receivingStockLocationDesc = objectI.getString("ReceivingStockLocationDesc");
                String serialFlag = objectI.getString("SerialFlag");
                String unit = objectI.getString("Unit");
                String unitDescription = objectI.getString("UnitDescription");
                String remark = objectI.getString("Remark");
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

                PrototypeBorrow prototypeBorrow = new PrototypeBorrow(reservationNumber, applicationType,
                        movementType, applicationDateLong, downloadTimeLong, applicant, consignee, receiptPlace,
                        consigneeContact, reservedItemNo, material, materialDescription, specs,
                        model, batch, quantity, factory, factoryDescription, deliveryStockLocation,
                        deliveryStockLocationDesc, receivingStockLocation, receivingStockLocationDesc,
                        serialFlag, unit, unitDescription, remark, batchFlag, date);
                if(StringUtils.isEmpty(serialFlag)){
                    double q = Double.valueOf(quantity);
                    prototypeBorrow.setScanQuantity((int) q);
                    prototypeBorrow.setScanTotal((int) q);
                }
                if (userController.userHasAllFunction() || StringUtils.isEmpty(deliveryStockLocation)
                        || StringUtils.containsIgnoreCase(locations, deliveryStockLocation)) {
                    all.add(prototypeBorrow);
                }
            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "get Prototype Borrow Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public String verifyData(List<PrototypeBorrow> list){
        String error = "";
        double require = 0;
        int scanQuantity = 0;
        for(PrototypeBorrow prototypeBorrow : list){
            if(prototypeBorrow.isBatchFlag()){
                if(StringUtils.isEmpty(prototypeBorrow.getBatch())){
                    error = app.getString(R.string.text_input_batch);
                    return error;
                }
            }

            require = prototypeBorrow.getQuantity();
            if(StringUtils.isEmpty(prototypeBorrow.getSerialFlag())){
                PrototypeBorrow order = findParentItem(list, prototypeBorrow);
                if(order != null){
                    scanQuantity = order.getScanTotal();
                }
            }else{
                scanQuantity = prototypeBorrow.getScanQuantity();
            }
            if(require > scanQuantity){
                error = app.getString(R.string.error_low_quantity);
                return error;
            }else if(scanQuantity > require){
                error = app.getString(R.string.error_more_quantity);
                return error;
            }
        }
        return error;
    }

    public String buildFilter(BusinessOrderQuery query){
        String filter = "$filter=(MovementType eq '311') and (ApplicationType  eq '借机出库')";
        String filterDeliveryDate = "";
        String filterStatus = "";
        String filterLocations = "";

        if(StringUtils.isNotEmpty(query.getNumber())){
            filter = Util.addFilter(filter, "(ReservationNumber eq '" + query.getNumber() + "')");
        }
        filterDeliveryDate = Util.getDateFilter("ApplicationDate", query.getDateFrom(), query.getDateTo());
        filter = Util.addFilter(filter, filterDeliveryDate);

        /*if (StringUtils.isNotEmpty(query.getShipmentStatus())) {
            filterStatus = "(ShipmentStatus eq '" + query.getShipmentStatus() + "')";
            filter = Util.addFilter(filter, filterStatus);
        }*/
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

        if(query.getStorageLocations() != null && query.getStorageLocations().size() > 0){
            filterLocations = "(";
            for(int i = 0; i < query.getStorageLocations().size(); i ++){
                if(query.getStorageLocations().get(i) != null){
                    if(i == query.getStorageLocations().size() - 1){
                        filterLocations = filterLocations + "DeliveryStockLocation eq '" + query.getStorageLocations().get(i) + "'";
                    }else{
                        filterLocations = filterLocations + "DeliveryStockLocation eq '" + query.getStorageLocations().get(i) + "' or ";
                    }
                }
            }
            filterLocations +=")";
            filter =Util.addFilter(filter, filterLocations);
        }
        String filterPlant = userController.getUserPlantsFilter("Factory");
        if(StringUtils.isNotEmpty(filterPlant)){
            filter = Util.addFilter(filter, filterPlant);
        }
        return filter;
    }

    public GeneralPostingRequest buildRequest(List<PrototypeBorrow> prototypeBorrows, LogisticsProvider logisticsProvider,
                                              String logisticNumber, String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "04";
        String materialDocumentHeaderText = "";
        List<GeneralMaterialDocumentItemResults> results = new ArrayList<>();
        for(PrototypeBorrow prototypeBorrow : prototypeBorrows){
            String material = prototypeBorrow.getMaterial();
            String plant = prototypeBorrow.getFactory();
            String storageLocation = prototypeBorrow.getStorageLocation();
            /*if(StringUtils.isEmpty(storageLocation)){
                storageLocation = prototypeBorrow.getStorageLocation();
            }*/
            String goodsMovementRefDocType = "";
            String goodsMovementType = "311";
            String quantityInEntryUnit = String.valueOf(prototypeBorrow.getScanQuantity());
            String reservation = prototypeBorrow.getReservationNumber();
            String reservationItem = prototypeBorrow.getReservedItemNo();
            String batch = prototypeBorrow.getBatch();

            boolean reservationIsFinallyIssued = true;
            String issuingOrReceivingStorageLoc = prototypeBorrow.getReceivingStockLocation();
            SerialNumber serialNumber = new SerialNumber();
            List<SerialNumberResults> serialNumberResults = new ArrayList<>();
            if(prototypeBorrow.getSnList() != null){
                for(String sn : prototypeBorrow.getSnList()){
                    SerialNumberResults serialNumberResult = new SerialNumberResults(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }

            serialNumber.setResults(serialNumberResults);
            GeneralMaterialDocumentItemResults itemResults = new GeneralMaterialDocumentItemResults();
            itemResults.setMaterial(material);
            itemResults.setStorageLocation(storageLocation);
            itemResults.setGoodsMovementRefDocType(goodsMovementRefDocType);
            itemResults.setPlant(plant);
            itemResults.setGoodsMovementType(goodsMovementType);
            itemResults.setQuantityInEntryUnit(quantityInEntryUnit);
            itemResults.setReservation(reservation);
            itemResults.setReservationItem(reservationItem);
            itemResults.setBatch(batch);
            itemResults.setReservationIsFinallyIssued(reservationIsFinallyIssued);
            itemResults.setIssuingOrReceivingStorageLoc("1301");
            itemResults.setSerialNumber(serialNumber);
            results.add(itemResults);
        }
        GeneralMaterialDocumentItem documentItem = new GeneralMaterialDocumentItem(results);
        String logistics = "";
        if(logisticsProvider != null){
            logistics = logisticsProvider.getZtName();
        }
        GeneralPostingRequest request = new GeneralPostingRequest(documentDate, postingDate,
                goodsMovementCode, materialDocumentHeaderText, logistics, Util.removeEnter(logisticNumber),
                "2", documentItem);
        return request;
    }

    public HttpResponse posting(GeneralPostingRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        Map<String, String> tokenHeaders = http.getToken(url);
        tokenHeaders.put(app.getString(R.string.header_language_key), app.getString(R.string.header_language_value));
        LogUtils.d("Prototype Borrow", "url---->" + url);
        LogUtils.d("Prototype Borrow", "request---->" + JSON.toJSONString(request));

        String json = JSON.toJSONString(request);

        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, tokenHeaders);
        LogUtils.d("Prototype Borrow", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("Prototype Borrow", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            String errorMessage = Util.parseSapError(httpResponse.getResponseString());
            LogUtils.e("Prototype Borrow", "errorMessage-->" + errorMessage);
            httpResponse.setError(errorMessage);
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            String MaterialDocument = d.getString("MaterialDocument");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }

    public void exportExcel(List<PrototypeBorrow> prototypeBorrows) throws IOException, WriteException, BiffException {
        String[] title = { "单据日期","单据类型","仓库名称", "单号","物料号","名称",
                "规格", "单位", "数量","批次", "备注", "收件人", "地址", "联系方式", "物流商", "型号"};
        File file = new File(FileUtil.getSDPath() + "/Sunmi");
        FileUtil.makeDir(file);
        String fileName = "样机借用发货单" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "样机借用发货单");

        ExcelUtils.writeObjListToExcel(getRecordData(prototypeBorrows), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }

    private  ArrayList<ArrayList<String>> getRecordData(List<PrototypeBorrow> prototypeBorrows) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i <prototypeBorrows.size(); i++) {
            PrototypeBorrow item = prototypeBorrows.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(item.getCreateDate());
            beanList.add(app.getString(R.string.text_prototype_borrow));
            beanList.add(item.getDeliveryStockLocationDesc());
            beanList.add(item.getReservationNumber());
            beanList.add(item.getMaterial());
            beanList.add(item.getMaterialDescription());
            beanList.add(item.getSpecs());
            beanList.add(item.getUnit());
            beanList.add(String.valueOf(item.getQuantity()));
            beanList.add(item.getBatch());
            beanList.add(item.getRemark());
            beanList.add(item.getConsignee());
            beanList.add(item.getReceiptPlace());
            beanList.add(item.getConsigneeContact());
            beanList.add("");
            beanList.add(item.getModel());
            if(item.getQuantity() > 0) {
                recordList.add(beanList);
            }
        }
        return recordList;
    }

    public List<PrototypeBorrow> splitItem(List<PrototypeBorrow> list, PrototypeBorrow splitItem){

        PrototypeBorrow sub = new PrototypeBorrow(splitItem.getReservationNumber(), splitItem.getApplicationType(),
                splitItem.getMovementType(),
                splitItem.getApplicationDate(), splitItem.getDownloadTime(), splitItem.getApplicant(), splitItem.getConsignee(),
                splitItem.getReceiptPlace(), splitItem.getConsigneeContact(), splitItem.getReservedItemNo(),
                splitItem.getMaterial(), splitItem.getMaterialDescription(), splitItem.getSpecs(), splitItem.getModel(),
                "", splitItem.getQuantity(), splitItem.getFactory(), splitItem.getFactoryDescription(),
                splitItem.getDeliveryStockLocation(), splitItem.getDeliveryStockLocationDesc(),
                splitItem.getReceivingStockLocation(), splitItem.getReceivingStockLocationDesc(), splitItem.getSerialFlag(),
                splitItem.getUnit(), splitItem.getUnitDescription(), splitItem.getRemark(), splitItem.isBatchFlag(), splitItem.getCreateDate());
        sub.setSub(true);
        list.add(sub);
        Comparator<PrototypeBorrow> comp = new ComparatorPrototypeBorrowItem();
        Collections.sort(list, comp);
        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return list;
    }

     public PrototypeBorrow findParentItem(List<PrototypeBorrow> list, PrototypeBorrow prototypeBorrow){
        PrototypeBorrow findPrototypeBorrow = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(prototypeBorrow.getReservationNumber() + "-"
                        + prototypeBorrow.getReservedItemNo() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findPrototypeBorrow;
    }

    public int getItemQuantityTotal(List<PrototypeBorrow> list, PrototypeBorrow prototypeBorrow){
        List<PrototypeBorrow> findPrototypeBorrowList = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(prototypeBorrow.getReservationNumber() + "-"
                        + prototypeBorrow.getReservedItemNo(), o.getItems())).collect(Collectors.toList());
        //累加所有拆分行的扫码数量用于最后校验。
        int total = 0;
        for(PrototypeBorrow findPrototypeBorrow : findPrototypeBorrowList){
            if(findPrototypeBorrow != null){
                //System.out.println("ScanQuantity---->" + findPrototypeBorrow.getScanQuantity());
                total += findPrototypeBorrow.getScanQuantity();
            }
        }
        return total;
    }

    public boolean isShowCheckBtn(List<PrototypeBorrow> list){
        for(PrototypeBorrow prototypeBorrow: list){
            if(StringUtils.isNotEmpty(prototypeBorrow.getSerialFlag())){
                return true;
            }
        }
        return false;
    }

    private PrototypeBorrow findOrderBySerial(List<PrototypeBorrow> list, String sn){
        for(PrototypeBorrow order : list){
            List<String> snList = order.getSnList();
            for(String number : snList){
                if(StringUtils.equalsIgnoreCase(sn, number)){
                    //System.out.println("sn----->" + sn);
                    return order;
                }
            }
        }
        return null;
    }
    public List<PrototypeBorrow> splitBatch(List<PrototypeBorrow> list, List<SerialInfo> items){
        List<PrototypeBorrow> batchFlagOrders = new ArrayList<>();
        List<PrototypeBorrow> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            PrototypeBorrow batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                PrototypeBorrow splitTransferOrder = (PrototypeBorrow)batchFlagOrder.clone();
                splitTransferOrder.setBatch(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<PrototypeBorrow>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(PrototypeBorrow::groupItem));
        LogUtils.d("transferOrders", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<PrototypeBorrow>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<PrototypeBorrow> orders = entry.getValue();
                PrototypeBorrow order = null;
                List<String> snList = new ArrayList<>();
                for(PrototypeBorrow t : orders){
                    if(order == null){
                        order = (PrototypeBorrow)t.clone();
                    }
                    snList.add(t.getSn());
                }
                order.setSnList(snList);
                order.setQuantity(Double.valueOf(snList.size()));
                order.setScanQuantity(snList.size());
                order.setScanTotal(snList.size());
                spiltOrders.add(order);
            }
        }
        List<PrototypeBorrow> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(PrototypeBorrow prototypeBorrow : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, prototypeBorrow.getReservedItemNo())){
                prototypeBorrow.setSub(true);
            }
            item = prototypeBorrow.getReservedItemNo();
        }
        Comparator<PrototypeBorrow> comp = new ComparatorPrototypeBorrowItem();
        Collections.sort(spiltOrders, comp);
        LogUtils.d("PrototypeBorrow", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }
}
