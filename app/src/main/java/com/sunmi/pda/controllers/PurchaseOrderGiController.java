package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.PurchaseOrder;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.FileUtils;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.MaterialDocumentItem;
import com.sunmi.pda.models.MaterialDocumentItemResults;
import com.sunmi.pda.models.PurchaseOrderGi;
import com.sunmi.pda.models.PurchaseOrderGiPostingRequest;
import com.sunmi.pda.models.PurchaseOrderGiResult;
import com.sunmi.pda.models.PurchaseOrderPostingRequest;
import com.sunmi.pda.models.PurchaseOrderQuery;
import com.sunmi.pda.models.PurchaseOrderResult;
import com.sunmi.pda.models.PurchaseOrderReturnPostingRequest;
import com.sunmi.pda.models.PurchaseOrderSubContract;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNo;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberPoReturn;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.utils.ComparatorItem;
import com.sunmi.pda.utils.ComparatorPoSubContractItem;
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

public class PurchaseOrderGiController {
    protected static final String TAG = PurchaseOrderGiController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();
    private static final UserController userController = app.getUserController();
    public List<PurchaseOrderGi> syncData(PurchaseOrderQuery purchaseOrderQuery, String functionId) throws AuthorizationException, GeneralException {

        String filter = "";
        List<PurchaseOrderGi> all = new ArrayList<>();
        try{
            if(purchaseOrderQuery != null){
                if(StringUtils.isNotEmpty(purchaseOrderQuery.getPurchaseOrder())){
                    filter = "$filter=(PRODUCTIONORDER eq '" + purchaseOrderQuery.getPurchaseOrder() + "')";
                }
            }

            String filterPlant = userController.getUserPlantsFilter("PLANT");
            if(StringUtils.isNotEmpty(filterPlant)){
                filter = Util.addFilter(filter, filterPlant);
            }

            if(StringUtils.isNotEmpty(filter)){
                filter = "&" + filter;
            }
            String functionUrl = app.getString(R.string.sap_url_po_gi);
            if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS)){
                functionUrl = app.getString(R.string.sap_url_po_pgr);
            }

            String url = app.getOdataService().getHost() + functionUrl
                    + app.getString(R.string.url_language_param) +app.getString(R.string.sap_url_client)
                    + filter + "&$orderby=PRODUCTIONORDER";

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
                    String orderNum = objectI.getString("ORDERNUM");
                    String orderItem = objectI.getString("ORDERITEM");
                    String material = objectI.getString("MATERIAL");
                    String materialDesc = objectI.getString("materialdesc");
                    String productionOrder = objectI.getString("PRODUCTIONORDER");
                    String plant = objectI.getString("PLANT");
                    String storageLocation = objectI.getString("STORAGE_LOCATION");
                    String deletionFlag = objectI.getString("DELETION_FLAG");
                    String finalIssueFlag = objectI.getString("FINAL_ISSUE_FLAG");
                    String batch = objectI.getString("BATCH");

                    double requireQty = objectI.getDouble("REQUIRE_QTY");
                    double withdrawnQty = objectI.getDouble("WITHDRAWN_QTY");
                    double qty = objectI.getDouble("QTY");
                    String movementType = objectI.getString("MOVEMENT_TYPE");
                    String bomItemNo = objectI.getString("BOM_ITEM_NO");
                    boolean batchFlag = StringUtils.equalsIgnoreCase("X", objectI.getString("BATCH_FLAG"))? true : false;
                    String serialFlag = objectI.getString("SERIAL_FLAG");
                    if(qty <= 0){
                        continue;
                    }
                    if(batchFlag && StringUtils.isEmpty(batch)){
                        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS)){
                            batch = "1";
                        }
                    }
                    PurchaseOrderGi po = new PurchaseOrderGi(orderNum, orderItem, material, productionOrder,
                            plant, storageLocation, deletionFlag, finalIssueFlag,
                            batch, requireQty, withdrawnQty, qty, movementType,
                            bomItemNo, batchFlag, serialFlag, materialDesc) ;
                /*if(batchFlag){
                    po.setBatch(DateUtils.dateToString(new Date(), DateUtils.FormatYMD) + "01");
                }*/
                    if (userController.userHasAllFunction() || StringUtils.isEmpty(storageLocation)
                            || StringUtils.containsIgnoreCase(locations, storageLocation)) {
                        all.add(po);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    LogUtils.e(TAG, "fetch purchase order Gi Error ------> " + e.getMessage());
                }
            }
        }catch (Exception e){
            throw new GeneralException();
        }
        all = all.stream().sorted(Comparator.comparing(PurchaseOrderGi::getOrderItem)).collect(Collectors.toList());


        return all;
    }

    public List<PurchaseOrderGiResult> groupPurchase(List<PurchaseOrderGi> purchaseOrders){
        String date = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
        Map<String, List<PurchaseOrderGi>> groupBy = purchaseOrders.stream().collect(Collectors.groupingBy(PurchaseOrderGi::getProductionOrder));

        List<PurchaseOrderGiResult> purchaseOrderResultList = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            List<PurchaseOrderGi> value = groupBy.get(key);
            PurchaseOrderGiResult purchaseOrderResult = new PurchaseOrderGiResult(key, date, "N", value);
            purchaseOrderResultList.add(purchaseOrderResult);

        }
        purchaseOrderResultList = purchaseOrderResultList.stream().sorted(Comparator.comparing(PurchaseOrderGiResult::getPurchaseOrder)).collect(Collectors.toList());

        return purchaseOrderResultList;
    }

    public List<PurchaseOrderGiPostingRequest> buildRequest(List<PurchaseOrderGi> purchaseOrders, LogisticsProvider logisticsProvider,
                                                            String logisticNumber, String postingDate, String deliveryNumber, String functionId){
        //String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";

        List<PurchaseOrderGiPostingRequest> results = new ArrayList<>();
        for(PurchaseOrderGi purchaseOrder : purchaseOrders){
            if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                continue;
            }

            String productionNo = purchaseOrder.getProductionOrder();
            String logisticsName = logisticsProvider == null? "" : logisticsProvider.getZtName();
            String logisticsNumber = logisticNumber;
            String bktxt = deliveryNumber;
            String materialNo = purchaseOrder.getMaterial();
            String plant = purchaseOrder.getPlant();
            String storageLocation = purchaseOrder.getStorageLocation();
            String batch = purchaseOrder.getBatch();
            String reversationNo = purchaseOrder.getOrderNum();
            String reversationItem = purchaseOrder.getOrderItem();

            String movementType = "261";
            String documentType = "";
            if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
                documentType = "14";
            }else{
                documentType = "15";
            }
            String giQty = purchaseOrder.getQuantityInEntryUnit();

            List<SerialNo> serialNo = new ArrayList<>();
            if(purchaseOrder.getSnList() != null){
                int i = 1;
                for(String sn : purchaseOrder.getSnList()){
                    SerialNo serialNumberResult = new SerialNo(sn, i+"");
                    serialNo.add(serialNumberResult);
                    i ++;
                }
            }

            PurchaseOrderGiPostingRequest itemResults = new PurchaseOrderGiPostingRequest(productionNo,
                    logisticsName, Util.removeEnter(logisticsNumber), postingDate, materialNo, plant, storageLocation,
                    batch, reversationNo, reversationItem, movementType, documentType, giQty, serialNo, bktxt);

            results.add(itemResults);
        }

        return results;
    }

    public HttpResponse posting(List<PurchaseOrderGiPostingRequest> request, String functionId) throws AuthorizationException, GeneralException {
        String functionUrl = app.getString(R.string.sap_url_po_gi_post);
        if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_PARTS)){
            functionUrl = app.getString(R.string.sap_url_po_pgr_post);
        }else if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_PGR_COMPLETE)){
            functionUrl = app.getString(R.string.sap_url_po_gr_post);
        }
        String url = app.getOdataService().getHost() + "/" + functionUrl + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        //Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PurchaseOrderPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        LogUtils.d("PurchaseOrderGiPosting", "url--->" + url);

        String json = JSON.toJSONString(request);
        LogUtils.d("PurchaseOrderGiPosting", "json--->" + json);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("PurchaseOrderGiPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PurchaseOrderGiPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONArray array = jsonObject.getJSONArray("objectMsg");
            if(array != null && array.size() > 0){
                JSONObject object = (JSONObject)array.get(0);
                if(object != null){
                    String errorMessage = object.getString("message");
                    LogUtils.e("PurchaseOrderGiPosting", "errorMessage-->" + errorMessage);
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

    public String verifyData(List<PurchaseOrderGi> purchaseOrderList, String functionId){
        //System.out.println("purchaseOrderList---->" + JSON.toJSONString(purchaseOrderList));
        String error = "";
        double sum = 0;
        //未收货数量
        double openQuantity = 0;
        List<PurchaseOrderGi> filtered = purchaseOrderList.stream().filter(s->!StringUtils.isEmpty(s.getQuantityInEntryUnit())).collect(Collectors.toList());
        if(filtered == null || filtered.size() == 0){
            error = app.getString(R.string.text_less_receive);
            return error;
        }
        for(PurchaseOrderGi purchaseOrder : purchaseOrderList){
            if(purchaseOrder.isSubOrder()){
                System.out.println("isSubOrder---s-");
                if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                    error = app.getString(R.string.text_input_quantity);
                    return error;
                }
            }
            if(purchaseOrder.isBatchFlag() && purchaseOrder.getScanQuantity() > 0){
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
            openQuantity = purchaseOrder.getQty();

            if(sum > openQuantity){
                if(StringUtils.equalsIgnoreCase(functionId, AppConstants.FUNCTION_ID_PURCHASE_ORDER_GI)){
                    error = app.getString(R.string.text_excess_gi);
                }else{
                    error = app.getString(R.string.text_excess);
                }

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

    public List<PurchaseOrderGi> splitPurchaseOrder(List<PurchaseOrderGi> purchaseOrderList, PurchaseOrderGi splitPurchaseOrder){

        PurchaseOrderGi sub = new PurchaseOrderGi(splitPurchaseOrder.getOrderNum(), splitPurchaseOrder.getOrderItem(),
                splitPurchaseOrder.getMaterial(), splitPurchaseOrder.getProductionOrder(),
                splitPurchaseOrder.getPlant(), splitPurchaseOrder.getStorageLocation(), splitPurchaseOrder.getDeletionFlag(),
                splitPurchaseOrder.getFinalIssueFlag(), splitPurchaseOrder.getBatch(), splitPurchaseOrder.getRequireQty(),
                splitPurchaseOrder.getWithdrawnQty(), splitPurchaseOrder.getQty(), splitPurchaseOrder.getMovementType(),
                splitPurchaseOrder.getBomItemNo(), splitPurchaseOrder.isBatchFlag(), splitPurchaseOrder.getSerialFlag(),
                splitPurchaseOrder.getMaterialDesc());
        sub.setSubOrder(true);
        purchaseOrderList.add(sub);

        purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrderGi::getProductionOrder)).collect(Collectors.toList());

        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return purchaseOrderList;
    }



    public boolean isShowCheckBtn(List<PurchaseOrderGi> list){
        for(PurchaseOrderGi order: list){
            if(StringUtils.isNotEmpty(order.getSerialFlag())){
                return true;
            }
        }
        return false;
    }
    public List<PurchaseOrderGi> splitBatch(List<PurchaseOrderGi> list, List<SerialInfo> items){
        List<PurchaseOrderGi> batchFlagOrders = new ArrayList<>();
        List<PurchaseOrderGi> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            PurchaseOrderGi batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                PurchaseOrderGi splitTransferOrder = (PurchaseOrderGi)batchFlagOrder.clone();
                splitTransferOrder.setBatch(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<PurchaseOrderGi>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(PurchaseOrderGi::groupItem));
        //LogUtils.d("PurchaseOrderGi", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<PurchaseOrderGi>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<PurchaseOrderGi> orders = entry.getValue();
                PurchaseOrderGi order = null;
                List<String> snList = new ArrayList<>();
                for(PurchaseOrderGi t : orders){
                    if(order == null){
                        order = (PurchaseOrderGi)t.clone();
                    }
                    snList.add(t.getSn());
                }
                order.setSnList(snList);
                //order.setOpenQuantity(Double.valueOf(snList.size()));
                order.setQuantityInEntryUnit(snList.size() + "");

                spiltOrders.add(order);
            }
        }
        List<PurchaseOrderGi> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(PurchaseOrderGi order : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, order.getMaterial())){
                order.setSubOrder(true);
            }
            item = order.getMaterial();
        }
        spiltOrders.stream().sorted(Comparator.comparing(PurchaseOrderGi::getProductionOrder)).collect(Collectors.toList());

        LogUtils.d("Picking", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }

    private PurchaseOrderGi findOrderBySerial(List<PurchaseOrderGi> list, String sn){
        for(PurchaseOrderGi order : list){
            //System.out.println("sn----->" + JSON.toJSONString(order));
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

    public PurchaseOrderGi findParentItem(List<PurchaseOrderGi> list, PurchaseOrderGi order){
        PurchaseOrderGi findOrder = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(order.getOrderNum() + "-"
                        + order.getOrderItem() + "-" + order.getMaterial() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findOrder;
    }

    public int getItemQuantityTotal(List<PurchaseOrderGi> list, PurchaseOrderGi order){
        List<PurchaseOrderGi> findPurchaseOrderSubContractList = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(order.getOrderNum() + "-"
                        + order.getOrderItem() + "-" + order.getMaterial(), o.getItems())).collect(Collectors.toList());
        //累加所有拆分行的扫码数量用于最后校验。
        int total = 0;
        for(PurchaseOrderGi findOrder : findPurchaseOrderSubContractList){
            if(findOrder != null){
                //System.out.println("ScanQuantity---->" + findPrototypeBorrow.getScanQuantity());
                total += findOrder.getScanQuantity();
            }
        }
        return total;
    }
}
