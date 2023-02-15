package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.GeneralMaterialDocumentItem;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.Picking;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.SalesInvoice;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.utils.ComparatorPrototypeBorrowItem;
import com.sunmi.pda.utils.ComparatorTransferOrderItem;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.ExcelUtils;
import com.sunmi.pda.utils.FileUtil;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.models.SalesInvoiceQuery;
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

public class TransferOrderController {
    protected static final String TAG = TransferOrderController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();

    /**
     * @param query user SalesInvoiceQuery, only orderNumber in query is used for TransferOrder
     * @return
     * @throws Exception
     */
    public List<TransferOrder> syncData(SalesInvoiceQuery query)  throws AuthorizationException, GeneralException {
        if (query == null ) {
            return null;
        }
        String locations = userController.getUserLocationString();
        String filter = buildFilter(query);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_transfer_order) + app.getString(R.string.url_language_param)
                + app.getString(R.string.sap_url_client) + "&$orderby=ReservedItemNo" + "&" + filter;

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        List<TransferOrder> all = new ArrayList<>();

        try {
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray jsonArray = d.getJSONArray("results");
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String reservedNo = objectI.getString("ReservedNo");
                String reservedItemNo = objectI.getString("ReservedItemNo");
                String applyType = objectI.getString("ApplyType");
                String moveType = objectI.getString("MoveType");
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
                Double quantity = objectI.getDouble("Quantity");
                String factory = objectI.getString("Factory");
                String factoryDesc = objectI.getString("FactoryDesc");
                String senderLoc = objectI.getString("SenderLoc");
                String senderLocDesc = objectI.getString("SenderLocDesc");
                String receiverLoc = objectI.getString("ReceiverLoc");
                String receiverLocDesc = objectI.getString("ReceiverLocDesc");
                String shipmentStatus = objectI.getString("ShipmentStatus");
                String serialFlag = objectI.getString("SerialFlag");
                String unit = objectI.getString("Unit");
                String unitDesciption = objectI.getString("UnitDesciption");
                String remark = objectI.getString("Remark");
                boolean batchFlag = objectI.getBoolean("BatchFlag");
                String model = objectI.getString("Model");
                if(batchFlag){
                    if(StringUtils.isEmpty(batchNo)){
                        batchNo = "";
                    }
                }else{
                    batchNo = "";
                }

                String CreateDate = objectI.getString("CreateDate");
                String date = DateUtils.jsonDateToString(CreateDate);
                TransferOrder materialp = new TransferOrder(reservedNo, reservedItemNo, applyType, moveType, applyDate, downloadTime, applyPerson, receivePerson, receivePlace, receiveContact, materialNo, materialDesc, spec, model
                , batchNo, quantity, factory, factoryDesc, senderLoc, senderLocDesc, receiverLoc,
                        receiverLocDesc, shipmentStatus, serialFlag, unit, unitDesciption, remark, batchFlag, date);
                if(StringUtils.isEmpty(serialFlag)){
                    double q = Double.valueOf(quantity);
                    materialp.setScanQuantity((int) q);
                    materialp.setScanTotal((int) q);
                }
                if (userController.userHasAllLocation() || StringUtils.isEmpty(materialp.getSenderLoc()) || StringUtils.containsIgnoreCase(locations, materialp.getSenderLoc())  ) {
                    all.add(materialp);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            LogUtils.e(TAG, "get transfer order Error ------> " + e.getMessage());
        }

        return all;
    }

    public String buildFilter(SalesInvoiceQuery query){
        String filter = "$filter=(MoveType eq 'Z01') and (ApplyType  eq '调拨出库')";
        String filterDeliveryDate = "";
        String filterStatus = "";

        String filterLocations = "";
        if(StringUtils.isNotEmpty(query.getDeliveryDocument())){
            filter = Util.addFilter(filter, "(ReservedNo eq '" + query.getDeliveryDocument() + "')");
        }
        filterDeliveryDate = Util.getDateFilter("RequiredDate", query.getDeliveryDateFrom(), query.getDeliveryDateTo());
        filter = Util.addFilter(filter, filterDeliveryDate);

        /*if(StringUtils.isNotEmpty(query.getDeliveryStatus())){
            filterStatus = "(ShipmentStatus eq '" + query.getDeliveryStatus() + "')";
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
                        filterLocations = filterLocations + "SenderLoc eq '" + query.getStorageLocations().get(i) + "'";
                    }else{
                        filterLocations = filterLocations + "SenderLoc eq '" + query.getStorageLocations().get(i) + "' or ";
                    }
                }
            }
            filterLocations +=")";
            filter =Util.addFilter(filter, filterLocations);
        }

        String filterPlant = userController.getUserPlantsFilter("Factory");
        if(StringUtils.isNotEmpty(filterPlant)){
            filter =Util.addFilter(filter, filterPlant);
        }
        return filter;
    }

    public String verifyData(List<TransferOrder> list){
        String error = "";
        double require = 0;
        int scanQuantity = 0;
        for(TransferOrder transferOrder : list){

            if(transferOrder.isBatchFlag()){
                if(StringUtils.isEmpty(transferOrder.getBatchNo())){
                    error = app.getString(R.string.text_input_batch);
                    return error;
                }
            }
            require = transferOrder.getQuantity();
            if(StringUtils.isEmpty(transferOrder.getSerialFlag())){
                TransferOrder findTransferOrder = findParentItem(list, transferOrder);
                if(findTransferOrder != null){
                    scanQuantity = findTransferOrder.getScanTotal();
                }
            }else{
                scanQuantity = transferOrder.getScanQuantity();
            }

            //System.out.println("require---->" + require);
            //System.out.println("scanQuantity---->" + scanQuantity);
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

    public GeneralPostingRequest buildRequest(List<TransferOrder> transferOrders, StorageLocation location, LogisticsProvider logisticsProvider,
                                              String logisticNumber, int functionId, String confirmDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        String postingDate = confirmDate + "T00:00:00";
        String goodsMovementCode = "04";

        String goodsMovementType = "313";
        if (functionId - 9 == 0) {
            goodsMovementType = "315";
        }

        String materialDocumentHeaderText = "";

        List<GeneralMaterialDocumentItemResults> results = new ArrayList<>();
        for(TransferOrder transferOrder : transferOrders){
            String material = transferOrder.getMaterialNo();
            String plant = transferOrder.getFactory();
            String storageLocation = transferOrder.getSenderLoc();
            String issuingOrReceivingStorageLoc = location.getStorageLocation(); ;
            if(StringUtils.isEmpty(issuingOrReceivingStorageLoc)){
                issuingOrReceivingStorageLoc = transferOrder.getReceiverLoc();
            }
            if(StringUtils.isEmpty(storageLocation)){
                storageLocation = transferOrder.getStorageLocation();
            }
            String goodsMovementRefDocType = "";

            String quantityInEntryUnit = String.valueOf(transferOrder.getScanQuantity());
            String reservation = transferOrder.getReservedNo();
            String reservationItem = transferOrder.getReservedItemNo();
            String batch = transferOrder.getBatchNo();
            boolean reservationIsFinallyIssued = true;

            SerialNumber serialNumber = new SerialNumber();

            List<SerialNumberResults> serialNumberResults = new ArrayList<>();
            if(transferOrder.getSnList() != null){
                for(String sn : transferOrder.getSnList()){
                    SerialNumberResults serialNumberResult = new SerialNumberResults(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }
            serialNumber.setResults(serialNumberResults);
            //serialNumber.setResults(picking.getSnList());
            GeneralMaterialDocumentItemResults itemResults = new GeneralMaterialDocumentItemResults();
            itemResults.setMaterial(material);
            itemResults.setPlant(plant);
            itemResults.setStorageLocation(storageLocation);  //发出库存地点
            itemResults.setIssuingOrReceivingStorageLoc(issuingOrReceivingStorageLoc);    // 接收
            itemResults.setGoodsMovementRefDocType(goodsMovementRefDocType);
            itemResults.setGoodsMovementType(goodsMovementType);
            itemResults.setQuantityInEntryUnit(quantityInEntryUnit);

            itemResults.setReservation(reservation);
            itemResults.setReservationItem(reservationItem);
            itemResults.setBatch(batch);

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
                "5", documentItem);
        return request;
    }

    public void exportExcel(List<TransferOrder> transferOrders) throws IOException, WriteException, BiffException {
        String[] title = { "单据日期","单据类型","仓库名称", "单号","物料号","名称",
                "规格", "单位", "数量","批次", "备注", "收件人", "地址", "联系方式", "物流商", "型号"};
        File file = new File(FileUtil.getSDPath() + "/Pda");
        FileUtil.makeDir(file);
        String fileName = "调拨单" + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Pda/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, "调拨单");

        ExcelUtils.writeObjListToExcel(getRecordData(transferOrders), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }

    private  ArrayList<ArrayList<String>> getRecordData(List<TransferOrder> items) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i <items.size(); i++) {
            TransferOrder item = items.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(item.getCreateDate());
            beanList.add(app.getString(R.string.title_transfer_order));
            beanList.add(item.getSenderLocDesc());
            beanList.add(item.getReservedNo());
            beanList.add(item.getMaterialNo());
            beanList.add(item.getMaterialDesc());
            beanList.add(item.getSpec());
            beanList.add(item.getUnit());
            beanList.add(String.valueOf(item.getQuantity()));
            beanList.add(item.getBatchNo());
            beanList.add(item.getRemark());
            beanList.add(item.getReceivePerson());
            beanList.add(item.getReceivePlace());
            beanList.add(item.getReceiveContact());
            beanList.add("");
            beanList.add(item.getModel());
            if(item.getQuantity() > 0) {
                recordList.add(beanList);
            }
        }
        return recordList;
    }

    public List<TransferOrder> splitItem(List<TransferOrder> list, TransferOrder splitItem){

        TransferOrder sub = new TransferOrder(splitItem.getReservedNo(), splitItem.getReservedItemNo(),
                splitItem.getApplyType(), splitItem.getMoveType(), splitItem.getApplyDate(), splitItem.getDownloadTime(),
                splitItem.getApplyPerson(), splitItem.getReceivePerson(), splitItem.getReceivePlace(),
                splitItem.getReceiveContact(), splitItem.getMaterialNo(), splitItem.getMaterialDesc(),
                splitItem.getSpec(), splitItem.getModel(), "", splitItem.getQuantity(),
                splitItem.getFactory(), splitItem.getFactoryDesc(), splitItem.getSenderLoc(), splitItem.getSenderLocDesc(),
                splitItem.getReceiverLoc(), splitItem.getReceiverLocDesc(), splitItem.getShipmentStatus(),
                splitItem.getSerialFlag(), splitItem.getUnit(), splitItem.getUnitDescription(),
                splitItem.getRemark(), splitItem.isBatchFlag(), splitItem.getCreateDate());
        sub.setSub(true);
        list.add(sub);
        Comparator<TransferOrder> comp = new ComparatorTransferOrderItem();
        Collections.sort(list, comp);
        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return list;
    }

    public TransferOrder findParentItem(List<TransferOrder> list, TransferOrder transferOrder){
        TransferOrder findTransferOrder = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(transferOrder.getReservedNo() + "-"
                        + transferOrder.getReservedItemNo() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findTransferOrder;
    }

    public int getItemQuantityTotal(List<TransferOrder> list, TransferOrder transferOrder){
        List<TransferOrder> findTransferOrderList = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(transferOrder.getReservedNo() + "-"
                        + transferOrder.getReservedItemNo(), o.getItems())).collect(Collectors.toList());
        //累加所有拆分行的扫码数量用于最后校验。
        int total = 0;
        for(TransferOrder findTransferOrder : findTransferOrderList){
            if(findTransferOrder != null){
                //System.out.println("ScanQuantity---->" + findPrototypeBorrow.getScanQuantity());
                total += findTransferOrder.getScanQuantity();
            }
        }
        return total;
    }
    public boolean isShowCheckBtn(List<TransferOrder> list){
        for(TransferOrder transferOrder: list){
            if(StringUtils.isNotEmpty(transferOrder.getSerialFlag())){
                return true;
            }
        }
        return false;
    }

    private TransferOrder findOrderBySerial(List<TransferOrder> list, String sn){
        for(TransferOrder transferOrder : list){
            List<String> snList = transferOrder.getSnList();
            for(String number : snList){
                if(StringUtils.equalsIgnoreCase(sn, number)){
                    System.out.println("sn----->" + sn);
                    return transferOrder;
                }
            }
        }
        return null;
    }

    public List<TransferOrder> splitBatch(List<TransferOrder> list, List<SerialInfo> items){
        List<TransferOrder> batchFlagOrders = new ArrayList<>();
        List<TransferOrder> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            TransferOrder batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                TransferOrder splitTransferOrder = (TransferOrder)batchFlagOrder.clone();
                splitTransferOrder.setBatchNo(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<TransferOrder>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(TransferOrder::groupItem));
        LogUtils.d("transferOrders", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<TransferOrder>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<TransferOrder> transferOrders = entry.getValue();
                TransferOrder transferOrder = null;
                List<String> snList = new ArrayList<>();
                for(TransferOrder t : transferOrders){
                    if(transferOrder == null){
                        transferOrder = (TransferOrder)t.clone();
                    }
                    snList.add(t.getSn());
                }
                transferOrder.setSnList(snList);
                transferOrder.setQuantity(Double.valueOf(snList.size()));
                transferOrder.setScanQuantity(snList.size());
                transferOrder.setScanTotal(snList.size());
                spiltOrders.add(transferOrder);
            }
        }
        List<TransferOrder> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(TransferOrder transferOrder : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, transferOrder.getReservedItemNo())){
                transferOrder.setSub(true);
            }
            item = transferOrder.getReservedItemNo();
        }
        Comparator<TransferOrder> comp = new ComparatorTransferOrderItem();
        Collections.sort(spiltOrders, comp);
        LogUtils.d("transferOrders", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }
}
