package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.PurchaseOrderGi;
import com.sunmi.pda.models.PurchaseOrderGiPostingRequest;
import com.sunmi.pda.models.PurchaseOrderGiResult;
import com.sunmi.pda.models.PurchaseOrderGr;
import com.sunmi.pda.models.PurchaseOrderGrResult;
import com.sunmi.pda.models.PurchaseOrderQuery;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNo;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PurchaseOrderGrController {
    protected static final String TAG = PurchaseOrderGrController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();
    private static final UserController userController = app.getUserController();
    public List<PurchaseOrderGr> syncData(PurchaseOrderQuery purchaseOrderQuery, String functionId) throws AuthorizationException, GeneralException {

        String filter = "";
        List<PurchaseOrderGr> all = new ArrayList<>();
        try{
            if(purchaseOrderQuery != null){
                if(StringUtils.isNotEmpty(purchaseOrderQuery.getPurchaseOrder())){
                    filter = "$filter=(productionno eq '" + purchaseOrderQuery.getPurchaseOrder() + "')";
                }
            }

            String filterPlant = userController.getUserPlantsFilter("plant");
            if(StringUtils.isNotEmpty(filterPlant)){
                filter = Util.addFilter(filter, filterPlant);
            }

            if(StringUtils.isNotEmpty(filter)){
                filter = "&" + filter;
            }
            String functionUrl = app.getString(R.string.sap_url_po_gr);
            String url = app.getOdataService().getHost() + functionUrl
                    + app.getString(R.string.url_language_param) +app.getString(R.string.sap_url_client)
                    + filter + "&$orderby=productionno";

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
                    String productionNo = objectI.getString("productionno");
                    String productionItem = objectI.getString("productionitem");
                    String material = objectI.getString("materialno");
                    String materialDesc = objectI.getString("materialdesc");
                    String plant = objectI.getString("plant");
                    String storageLocation = objectI.getString("storagelocation");

                    String batch = "";

                    double totalQty = objectI.getDouble("totalqty");
                    double grQty = objectI.getDouble("grqty");
                    double qty = objectI.getDouble("qty");

                    boolean completion = objectI.getBoolean("completion");
                    boolean batchFlag = StringUtils.equalsIgnoreCase("X", objectI.getString("BATCH_FLAG"))? true : false;
                    String serialFlag = objectI.getString("SERIAL_FLAG");
                    if(qty <= 0){
                        continue;
                    }
                    if(batchFlag){
                        batch = "2021123101";
                    }
                    PurchaseOrderGr po = new PurchaseOrderGr(productionNo, productionItem, material, materialDesc,
                            plant, storageLocation, completion, batch, totalQty, grQty, qty, batchFlag, serialFlag) ;
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
        all = all.stream().sorted(Comparator.comparing(PurchaseOrderGr::getProductionItem)).collect(Collectors.toList());


        return all;
    }

    public List<PurchaseOrderGrResult> groupPurchase(List<PurchaseOrderGr> purchaseOrders){
        String date = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
        Map<String, List<PurchaseOrderGr>> groupBy =
                purchaseOrders.stream().collect(Collectors.groupingBy(PurchaseOrderGr::getProductionNo));

        List<PurchaseOrderGrResult> purchaseOrderResultList = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            List<PurchaseOrderGr> value = groupBy.get(key);
            PurchaseOrderGrResult purchaseOrderResult = new PurchaseOrderGrResult(key, date, "N", value);
            purchaseOrderResultList.add(purchaseOrderResult);

        }
        purchaseOrderResultList = purchaseOrderResultList.stream().sorted(Comparator.comparing(PurchaseOrderGrResult::getPurchaseOrder)).collect(Collectors.toList());

        return purchaseOrderResultList;
    }

    public List<PurchaseOrderGiPostingRequest> buildRequest(List<PurchaseOrderGr> purchaseOrders, String postingDate, String deliveryNumber){
        //String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";

        List<PurchaseOrderGiPostingRequest> results = new ArrayList<>();
        for(PurchaseOrderGr purchaseOrder : purchaseOrders){
            if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                continue;
            }

            String productionNo = purchaseOrder.getProductionNo();
            String logisticsName = "";
            String logisticsNumber = "";
            String bktxt = deliveryNumber;
            String materialNo = purchaseOrder.getMaterial();
            String plant = purchaseOrder.getPlant();
            String storageLocation = purchaseOrder.getStorageLocation();
            String batch = purchaseOrder.getBatch();
            String reversationNo = "";
            String reversationItem = "";

            String movementType = "101";
            String documentType = "16";
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
                    logisticsName, logisticsNumber, postingDate, materialNo, plant, storageLocation,
                    batch, reversationNo, reversationItem, movementType, documentType, giQty, serialNo, bktxt);

            results.add(itemResults);
        }

        return results;
    }



    public String verifyData(List<PurchaseOrderGr> purchaseOrderList){
        //System.out.println("purchaseOrderList---->" + JSON.toJSONString(purchaseOrderList));
        String error = "";
        double sum = 0;
        //未收货数量
        double openQuantity = 0;
        List<PurchaseOrderGr> filtered = purchaseOrderList.stream().filter(s->!StringUtils.isEmpty(s.getQuantityInEntryUnit())).collect(Collectors.toList());
        if(filtered == null || filtered.size() == 0){
            error = app.getString(R.string.text_less_receive);
            return error;
        }
        for(PurchaseOrderGr purchaseOrder : purchaseOrderList){
            if(purchaseOrder.isSubOrder()){
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

    public List<PurchaseOrderGr> splitPurchaseOrder(List<PurchaseOrderGr> purchaseOrderList, PurchaseOrderGr splitPurchaseOrder){

        PurchaseOrderGr sub = new PurchaseOrderGr(splitPurchaseOrder.getProductionNo(), splitPurchaseOrder.getProductionItem(),
                splitPurchaseOrder.getMaterial(), splitPurchaseOrder.getMaterialDesc(),
                splitPurchaseOrder.getPlant(), splitPurchaseOrder.getStorageLocation(), splitPurchaseOrder.isCompletion(), splitPurchaseOrder.getBatch(),
                splitPurchaseOrder.getTotalQty(), splitPurchaseOrder.getGrQty(), splitPurchaseOrder.getQty(), splitPurchaseOrder.isBatchFlag(), splitPurchaseOrder.getSerialFlag());
        sub.setSubOrder(true);
        purchaseOrderList.add(sub);


        purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrderGr::getProductionItem)).collect(Collectors.toList());

        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return purchaseOrderList;
    }

    public List<PurchaseOrderGr> splitBatch(List<PurchaseOrderGr> list, List<SerialInfo> items){
        List<PurchaseOrderGr> batchFlagOrders = new ArrayList<>();
        List<PurchaseOrderGr> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            PurchaseOrderGr batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                PurchaseOrderGr splitTransferOrder = (PurchaseOrderGr)batchFlagOrder.clone();
                splitTransferOrder.setBatch(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<PurchaseOrderGr>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(PurchaseOrderGr::groupItem));
        //LogUtils.d("PurchaseOrderGi", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<PurchaseOrderGr>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<PurchaseOrderGr> orders = entry.getValue();
                PurchaseOrderGr order = null;
                List<String> snList = new ArrayList<>();
                for(PurchaseOrderGr t : orders){
                    if(order == null){
                        order = (PurchaseOrderGr)t.clone();
                    }
                    snList.add(t.getSn());
                }
                order.setSnList(snList);
                //order.setOpenQuantity(Double.valueOf(snList.size()));
                order.setQuantityInEntryUnit(snList.size() + "");

                spiltOrders.add(order);
            }
        }
        List<PurchaseOrderGr> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(PurchaseOrderGr order : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, order.getMaterial())){
                order.setSubOrder(true);
            }
            item = order.getMaterial();
        }
        spiltOrders.stream().sorted(Comparator.comparing(PurchaseOrderGr::getProductionNo)).collect(Collectors.toList());

        LogUtils.d("Picking", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }

    private PurchaseOrderGr findOrderBySerial(List<PurchaseOrderGr> list, String sn){
        for(PurchaseOrderGr order : list){
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

    public PurchaseOrderGr findParentItem(List<PurchaseOrderGr> list, PurchaseOrderGr order){
        PurchaseOrderGr findOrder = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(order.getProductionNo() + "-"
                        + order.getProductionItem() + "-" + order.getMaterial() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findOrder;
    }

}
