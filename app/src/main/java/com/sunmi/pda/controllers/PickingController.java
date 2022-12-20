package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.Picking;
import com.sunmi.pda.models.PrototypeBorrow;
import com.sunmi.pda.models.GeneralMaterialDocumentItem;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.BusinessOrderQuery;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.ComparatorPickingItem;
import com.sunmi.pda.utils.ComparatorPrototypeBorrowItem;
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

public class PickingController {
    protected static final String TAG = PickingController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();
    public List<Picking> syncData(BusinessOrderQuery query, int functionId) throws Exception {
        String filter = buildFilter(query, functionId);//"$filter=ReservedNo eq '" + query.getNumber() + "'";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_picking) + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client);
        if (filter.length() > 0) {
            url = url + "&" + filter;
        }

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<Picking> all = new ArrayList<>();
        String locations = userController.getUserLocationString();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String reservedNo = objectI.getString("ReservedNo");
                String applyType = objectI.getString("ApplyType");
                String moveType = objectI.getString("MoveType");
                String costCenter = objectI.getString("CostCenter");
                String costCenterDesc = objectI.getString("CostCenterDesc");
                String applicationDate = objectI.getString("ApplyDate");
                Long applicationDateLong = null;
                if(StringUtils.isEmpty(applicationDate)){
                    applicationDateLong = DateUtils.jsonDateToTimeStamp(applicationDate);
                }
                String downloadTime = objectI.getString("DownloadTime");
                Long downloadTimeLong = null;
                if(StringUtils.isEmpty(applicationDate)){
                    downloadTimeLong = DateUtils.jsonDateToTimeStamp(downloadTime);
                }

                String applyPerson = objectI.getString("ApplyPerson");
                String receivePerson = objectI.getString("ReceivePerson");
                String receiptPlace = objectI.getString("ReceivePlace");
                String receiveContact = objectI.getString("ReceiveContact");
                String reservedItemNo = objectI.getString("ReservedItemNo");
                String material = objectI.getString("MaterialNo");
                String materialDescription = objectI.getString("MaterialDesc");
                String specs = objectI.getString("Spec");
                String batch = objectI.getString("BatchNo");
                /*if(StringUtils.isEmpty(batch)){
                    batch = DateUtils.dateToString(new Date(), DateUtils.FormatYMD) + "01";
                }*/
                double quantity = objectI.getDouble("Quantity");
                String factory = objectI.getString("Factory");
                String factoryDescription = objectI.getString("FactoryDesc");
                String storeLoc = objectI.getString("StoreLoc");
                String storeLocDesc = objectI.getString("StoreLocDesc");

                String unit = objectI.getString("Unit");
                String unitDescription = objectI.getString("UnitDescription");
                String remark = objectI.getString("Remark");

                String serialFlag = objectI.getString("SerialFlag");
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
                System.out.println("date--->" + date);
                Picking picking = new Picking(reservedNo, applyType, moveType, costCenter, costCenterDesc,
                        applicationDateLong, downloadTimeLong, applyPerson, receivePerson,
                        receiptPlace, receiveContact, reservedItemNo, material,
                        materialDescription, specs, model, batch, quantity, factory,
                        factoryDescription, storeLoc, storeLocDesc, serialFlag, unit, unitDescription,
                        remark, batchFlag, date);
                if(StringUtils.isEmpty(serialFlag)){
                    double q = Double.valueOf(quantity);
                    picking.setScanQuantity((int) q);
                    picking.setScanTotal((int) q);
                }
                if (userController.userHasAllFunction() || StringUtils.isEmpty(storeLoc)
                        || StringUtils.containsIgnoreCase(locations, storeLoc)) {
                    all.add(picking);
                }

            }catch (Exception e){
                e.printStackTrace();
                LogUtils.e(TAG, "get Prototype Borrow Error ------> " + e.getMessage());
            }
        }

        return all;
    }

    public String verifyData(List<Picking> list){
        String error = "";
        double require = 0;
        int scanQuantity = 0;
        for(Picking picking : list){
            if(picking.isBatchFlag()){
                if(StringUtils.isEmpty(picking.getBatch())){
                    error = app.getString(R.string.text_input_batch);
                    return error;
                }
            }

            require = picking.getQuantity();
            if(StringUtils.isEmpty(picking.getSerialFlag())){
                Picking order = findParentItem(list, picking);
                if(order != null){
                    scanQuantity = order.getScanTotal();
                }
            }else{
                scanQuantity = picking.getScanQuantity();
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

    public GeneralPostingRequest buildRequest(List<Picking> pickings, LogisticsProvider logisticsProvider,
                                              String logisticNumber, int functionId, String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "03";
        String materialDocumentHeaderText = "";
        List<GeneralMaterialDocumentItemResults> results = new ArrayList<>();
        for(Picking picking : pickings){
            String material = picking.getMaterial();
            String plant = picking.getFactory();
            String storageLocation = picking.getStorageLocation();
            /*if(StringUtils.isEmpty(storageLocation)){
                storageLocation = picking.getStorageLocation();
            }*/
            String goodsMovementRefDocType = "";
            String goodsMovementType = "201";
            String quantityInEntryUnit = String.valueOf(picking.getScanQuantity());
            String reservation = picking.getReservedNo();
            String reservationItem = picking.getReservedItemNo();
            String batch = picking.getBatch();
            boolean reservationIsFinallyIssued = true;
            String costCenter = picking.getCostCenter();
            SerialNumber serialNumber = new SerialNumber();

            List<SerialNumberResults> serialNumberResults = new ArrayList<>();
            if(picking.getSnList() != null){
                for(String sn : picking.getSnList()){
                    SerialNumberResults serialNumberResult = new SerialNumberResults(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }
            serialNumber.setResults(serialNumberResults);
            //serialNumber.setResults(picking.getSnList());
            GeneralMaterialDocumentItemResults itemResults = new GeneralMaterialDocumentItemResults();
            itemResults.setMaterial(material);
            itemResults.setPlant(plant);
            itemResults.setStorageLocation(storageLocation);
            itemResults.setGoodsMovementRefDocType(goodsMovementRefDocType);
            itemResults.setGoodsMovementType(goodsMovementType);
            itemResults.setQuantityInEntryUnit(quantityInEntryUnit);
            itemResults.setReservation(reservation);
            itemResults.setReservationItem(reservationItem);
            itemResults.setBatch(batch);
            itemResults.setReservationIsFinallyIssued(reservationIsFinallyIssued);
            itemResults.setCostCenter(costCenter);
            itemResults.setSerialNumber(serialNumber);
            results.add(itemResults);
        }
        String documentType = "3";
        if(functionId == 5){
            documentType = "4";
        }
        GeneralMaterialDocumentItem documentItem = new GeneralMaterialDocumentItem(results);
        String logistics = "";
        if(logisticsProvider != null){
            logistics = logisticsProvider.getZtName();
        }
        GeneralPostingRequest request = new GeneralPostingRequest(documentDate, postingDate,
                goodsMovementCode, materialDocumentHeaderText, logistics, Util.removeEnter(logisticNumber),
                documentType, documentItem);
        return request;
    }

    public HttpResponse posting(GeneralPostingRequest request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_posting) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PickingPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        //LogUtils.d("PickingPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        String json = JSON.toJSONString(request);
        LogUtils.d("PickingPosting", "request---->" + JSON.toJSONString(request));
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, tokenHeaders);
        LogUtils.d("PickingPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PickingPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            String errorMessage = Util.parseSapError(httpResponse.getResponseString());
            LogUtils.e("PickingPosting", "errorMessage-->" + errorMessage);
            httpResponse.setError(errorMessage);
        }else{
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            String MaterialDocument = d.getString("MaterialDocument");
            httpResponse.setResponseString(MaterialDocument);
        }
        return httpResponse;
    }

    public String buildFilter(BusinessOrderQuery query, int functionId){
        String filter = "$filter=(MoveType eq '201') and (ApplyType  eq '材料出库')";
        if(functionId == 5){
            filter = "$filter=(MoveType eq '201') and (ApplyType  eq '借机出库')";
        }
        String filterDeliveryDate = "";
        String filterStatus = "";
        String filterLocations = "";

        if(StringUtils.isNotEmpty(query.getNumber())){
            filter = Util.addFilter(filter, "(ReservedNo eq '" + query.getNumber() + "')");
        }
        filterDeliveryDate = Util.getDateFilter("ApplyDate", query.getDateFrom(), query.getDateTo());
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
                        filterLocations = filterLocations + "StoreLoc eq '" + query.getStorageLocations().get(i) + "'";
                    }else{
                        filterLocations = filterLocations + "StoreLoc eq '" + query.getStorageLocations().get(i) + "' or ";
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

    public void exportExcel(List<Picking> pickings, String functionId) throws IOException, WriteException, BiffException {
        String[] title = { "单据日期","单据类型","仓库名称", "单号","物料号","名称",
                "规格", "单位", "数量","批次", "备注", "收件人", "地址", "联系方式", "物流商", "型号"};
        File file = new File(FileUtil.getSDPath() + "/Sunmi");
        FileUtil.makeDir(file);
        String type = app.getString(R.string.text_picking);
        if (StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PICKING)){
            type = app.getString(R.string.text_picking_material);
        }
        String fileName = type + DateUtils.dateToString(new Date(), DateUtils.FormatYMDHMS) + ".xls";
        String filePath = FileUtil.getSDPath() + "/Sunmi/" + fileName;
        File fileXls = new File(filePath);
        if (!fileXls.exists()) {
            fileXls.createNewFile();
        }
        ExcelUtils.initExcel(fileXls, filePath, title, type);

        ExcelUtils.writeObjListToExcel(getRecordData(pickings, type), filePath);
        FileUtils.notifySystemToScan(fileXls, app);
    }

    private  ArrayList<ArrayList<String>> getRecordData(List<Picking> pickings, String type) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();

        for (int i = 0; i <pickings.size(); i++) {
            Picking item = pickings.get(i);
            ArrayList<String> beanList = new ArrayList<String>();
            beanList.add(item.getCreateDate());
            beanList.add(type);
            beanList.add(item.getStoreLocDesc());
            beanList.add(item.getReservedNo());
            beanList.add(item.getMaterial());
            beanList.add(item.getMaterialDescription());
            beanList.add(item.getSpecs());
            beanList.add(item.getUnit());
            beanList.add(String.valueOf(item.getQuantity()));
            beanList.add(item.getBatch());
            beanList.add(item.getRemark());
            beanList.add(item.getReceivePerson());
            beanList.add(item.getReceiptPlace());
            beanList.add(item.getReceiveContact());
            beanList.add("");
            beanList.add(item.getModel());
            if(item.getQuantity() > 0) {
                recordList.add(beanList);
            }
        }
        return recordList;
    }
    public List<Picking> splitItem(List<Picking> list, Picking splitItem){

        Picking sub = new Picking(splitItem.getReservedNo(), splitItem.getApplyType(), splitItem.getMoveType(),
                splitItem.getCostCenter(), splitItem.getCostCenterDesc(),
                splitItem.getApplicationDate(), splitItem.getDownloadTime(), splitItem.getApplyPerson(), splitItem.getReceivePerson(),
                splitItem.getReceiptPlace(), splitItem.getReceiveContact(), splitItem.getReservedItemNo(), splitItem.getMaterial(),
                splitItem.getMaterialDescription(), splitItem.getSpecs(), splitItem.getModel(), "", splitItem.getQuantity(),
                splitItem.getFactory(), splitItem.getFactoryDescription(), splitItem.getStoreLoc(), splitItem.getStoreLocDesc(),
                splitItem.getSerialFlag(), splitItem.getUnit(), splitItem.getUnitDescription(),
                splitItem.getRemark(), splitItem.isBatchFlag(), splitItem.getCreateDate());
        sub.setSub(true);
        list.add(sub);
        Comparator<Picking> comp = new ComparatorPickingItem();
        Collections.sort(list, comp);
        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return list;
    }

    public Picking findParentItem(List<Picking> list, Picking picking){
        Picking findPicking= list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(picking.getReservedNo() + "-"
                        + picking.getReservedItemNo() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findPicking;
    }

    public int getItemQuantityTotal(List<Picking> list, Picking picking){
        List<Picking> findPrototypeBorrowList = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(picking.getReservedNo() + "-"
                        + picking.getReservedItemNo(), o.getItems())).collect(Collectors.toList());
        //累加所有拆分行的扫码数量用于最后校验。
        int total = 0;
        for(Picking findPicking : findPrototypeBorrowList){
            if(findPicking != null){
                //System.out.println("ScanQuantity---->" + findPrototypeBorrow.getScanQuantity());
                total += findPicking.getScanQuantity();
            }
        }
        return total;
    }

    public boolean isShowCheckBtn(List<Picking> list){
        for(Picking picking: list){
            if(StringUtils.isNotEmpty(picking.getSerialFlag())){
                return true;
            }
        }
        return false;
    }

    private Picking findOrderBySerial(List<Picking> list, String sn){
        for(Picking order : list){
            List<String> snList = order.getSnList();
            for(String number : snList){
                if(StringUtils.equalsIgnoreCase(sn, number)){
                    System.out.println("sn----->" + sn);
                    return order;
                }
            }
        }
        return null;
    }
    public List<Picking> splitBatch(List<Picking> list, List<SerialInfo> items){
        List<Picking> batchFlagOrders = new ArrayList<>();
        List<Picking> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            Picking batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                Picking splitTransferOrder = (Picking)batchFlagOrder.clone();
                splitTransferOrder.setBatch(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<Picking>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(Picking::groupItem));
        LogUtils.d("transferOrders", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<Picking>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<Picking> orders = entry.getValue();
                Picking order = null;
                List<String> snList = new ArrayList<>();
                for(Picking t : orders){
                    if(order == null){
                        order = (Picking)t.clone();
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
        List<Picking> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(Picking picking : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, picking.getReservedItemNo())){
                picking.setSub(true);
            }
            item = picking.getReservedItemNo();
        }
        Comparator<Picking> comp = new ComparatorPickingItem();
        Collections.sort(spiltOrders, comp);
        LogUtils.d("Picking", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }
}
