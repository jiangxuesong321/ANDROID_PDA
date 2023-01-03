package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunmi.pda.R;
import com.sunmi.pda.application.AndroidApplication;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.Component;
import com.sunmi.pda.models.ComponentItem;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.PurchaseOrderQuery;
import com.sunmi.pda.models.PurchaseOrderSubContract;
import com.sunmi.pda.models.PurchaseOrderSubContractInPostingRequest;
import com.sunmi.pda.models.PurchaseOrderSubContractOutPosting;
import com.sunmi.pda.models.PurchaseOrderSubContractResult;
import com.sunmi.pda.models.SerialInfo;
import com.sunmi.pda.models.SerialNumberPoReturn;
import com.sunmi.pda.models.SerialNumberPoSubContract;
import com.sunmi.pda.utils.ComparatorItemSubContract;
import com.sunmi.pda.utils.ComparatorSubContract;
import com.sunmi.pda.utils.ComparatorPoSubContractItem;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PurchaseOrderSubContractController {
    protected static final String TAG = PurchaseOrderSubContractController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final MaterialController materialController = app.getMaterialController();
    private static final UserController userController = app.getUserController();
    public List<PurchaseOrderSubContract> syncData(PurchaseOrderQuery purchaseOrderQuery) throws AuthorizationException, GeneralException {
        String filter = "";
        String filterCreateDate = "";
        String filterDeliveryDate = "";
        String filterSupplier = "";
        if(purchaseOrderQuery != null){
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getPurchaseOrder())){
                filter = "$filter=(purchaseNo eq '" + purchaseOrderQuery.getPurchaseOrder() + "')";
            }
            if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateTo())
                    || StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateFrom())){
                if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateTo())){
                    String createDateFrom = purchaseOrderQuery.getCreateDateFrom() + "T00:00:00";
                    String createDateTo = purchaseOrderQuery.getCreateDateTo() + "T00:00:00";
                    filterCreateDate = "(POCreationDate ge datetime'" + createDateFrom
                            + "' and POCreationDate le datetime'" + createDateTo + "' ) ";
                }else{
                    if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateFrom())){
                        String createDateFrom = purchaseOrderQuery.getCreateDateFrom() + "T00:00:00";
                        filterCreateDate = "(POCreationDate ge datetime'" + createDateFrom +"' ) ";
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
                   filterDeliveryDate = "(POItemDeliveryDate ge datetime'" + deliveryDateFrom
                           + "' and POItemDeliveryDate le datetime'" + deliveryDateTo + "' ) ";
               }else{
                   if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateFrom())){
                       String deliveryDateFrom = purchaseOrderQuery.getDeliveryDateFrom() + "T00:00:00";
                       filterDeliveryDate = "(POItemDeliveryDate ge datetime'" + deliveryDateFrom +"' ) ";
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
                    filter = filter + " and " + "(vendorNo eq '" + filterSupplier + "')";
                }else{
                    filter = "$filter="  + "(vendorNo '" + filterSupplier + "')";
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
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_sub_contract_out)
                + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client)
                + filter + "&$orderby=purchaseNo";

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<PurchaseOrderSubContract> all = new ArrayList<>();
        String locations = userController.getUserLocationString();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String poNr = objectI.getString("purchaseNo");
                String poItem = "";
                String supplier = objectI.getString("vendorNo");
                long creationDate = DateUtils.jsonDateToTimeStamp(objectI.getString("POCreationDate"));
                String materialNr = objectI.getString("materialno");
                String purchaseOrderItemText = objectI.getString("materialdesc");
                String plant = objectI.getString("plant");
                String storageLocation = objectI.getString("storagelocation");
                long deliveryDate = DateUtils.jsonDateToTimeStamp(objectI.getString("POItemDeliveryDate"));
                String unit = objectI.getString("Unit");
                double orderQuantity = objectI.getDouble("materialqty");
                double openQuantity = objectI.getDouble("materialqtynotposted");
                boolean batchFlag = objectI.getBoolean("batchflag");

                String model = objectI.getString("Model");
                if(openQuantity <= 0){
                    continue;
                }
                String serialFlag = objectI.getString("serialflag");

                PurchaseOrderSubContract po = new PurchaseOrderSubContract(poNr, poItem, supplier, creationDate, materialNr,
                        purchaseOrderItemText, plant, deliveryDate, unit, orderQuantity, openQuantity,
                        storageLocation, batchFlag, model, serialFlag, 0, 0, "", 0, "") ;
                /*if(batchFlag){
                    po.setBatch(DateUtils.dateToString(new Date(), DateUtils.FormatYMD) + "01");
                }*/
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

    public List<PurchaseOrderSubContractResult> groupPurchase(List<PurchaseOrderSubContract> purchaseOrders){
        String date = DateUtils.dateToString(new Date(), DateUtils.FormatFullDate);
        Map<String, List<PurchaseOrderSubContract>> groupBy = purchaseOrders.stream().collect(Collectors.groupingBy(PurchaseOrderSubContract::getPurchaseOrder));

        List<PurchaseOrderSubContractResult> purchaseOrderResultList = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            List<PurchaseOrderSubContract> value = groupBy.get(key);
            PurchaseOrderSubContractResult purchaseOrderResult = new PurchaseOrderSubContractResult(key, date, "N", value);
            purchaseOrderResultList.add(purchaseOrderResult);

        }
        purchaseOrderResultList = purchaseOrderResultList.stream().sorted(Comparator.comparing(PurchaseOrderSubContractResult::getPurchaseOrder)).collect(Collectors.toList());

        return purchaseOrderResultList;
    }

    public List<PurchaseOrderSubContractOutPosting> buildRequest(List<PurchaseOrderSubContract> purchaseOrders,
                                                                  String logisticsProvider, String logisticNumber,
                                                                  String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "541";

        List<PurchaseOrderSubContractOutPosting> results = new ArrayList<>();
        for(PurchaseOrderSubContract purchaseOrder : purchaseOrders){
            if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                continue;
            }
            String material = purchaseOrder.getMaterial();
            String plant = purchaseOrder.getPlant();
            String storageLocation = purchaseOrder.getStorageLocation();
            String quantityInEntryUnit = purchaseOrder.getQuantityInEntryUnit();
            String purchaseOrderNr = purchaseOrder.getPurchaseOrder();
            String purchaseOrderItem = purchaseOrder.getPurchaseOrderItem();
            String batch = purchaseOrder.getBatch();


            List<SerialNumberPoSubContract> serialNumberResults = new ArrayList<>();
            if(purchaseOrder.getSnList() != null && purchaseOrder.getSnList().size() > 0){
                for(String sn : purchaseOrder.getSnList()){
                    SerialNumberPoSubContract serialNumberResult = new SerialNumberPoSubContract(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }else{
                serialNumberResults = null;
            }

            PurchaseOrderSubContractOutPosting posting = new PurchaseOrderSubContractOutPosting(postingDate, documentDate, goodsMovementCode,
                    material, plant, storageLocation, quantityInEntryUnit,
                    batch, purchaseOrder.getSupplier(), "", logisticsProvider, Util.removeEnter(logisticNumber),
                    "9", serialNumberResults, purchaseOrderNr, purchaseOrderItem);

            results.add(posting);
        }

        //PurchaseOrderSubContractOutPostingRequest request = new PurchaseOrderSubContractOutPostingRequest(results);
        return results;
    }
    public HttpResponse outPosting(List<PurchaseOrderSubContractOutPosting> postingList) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_sub_contract_out_post) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        //Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PurchaseOrderPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        LogUtils.d("PurchaseOrderOutPosting", "url--->" + url);

        String json = JSON.toJSONString(postingList);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("PurchaseOrderOutPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PurchaseOrderOutPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONArray array = jsonObject.getJSONArray("objectMsg");
            if(array != null && array.size() > 0){
                JSONObject object = (JSONObject)array.get(0);
                if(object != null){
                    String errorMessage = object.getString("message");
                    LogUtils.e("PurchaseOrderOutPosting", "errorMessage-->" + errorMessage);
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

    public String verifyData(List<PurchaseOrderSubContract> purchaseOrderList){
        //System.out.println("purchaseOrderList---->" + JSON.toJSONString(purchaseOrderList));
        String error = "";
        double sum = 0;
        //未收货数量
        double openQuantity = 0;
        List<PurchaseOrderSubContract> filtered = purchaseOrderList.stream().filter(s->!StringUtils.isEmpty(s.getQuantityInEntryUnit())).collect(Collectors.toList());
        if(filtered == null || filtered.size() == 0){
            error = app.getString(R.string.text_less_receive);
            return error;
        }
        for(PurchaseOrderSubContract purchaseOrder : purchaseOrderList){
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

            if(!purchaseOrder.isSubOrder()){
                sum = Double.valueOf(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())? "0" : purchaseOrder.getQuantityInEntryUnit());
            }else{
                if(purchaseOrder.isSubOrder()){
                    sum += Double.valueOf(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())? "0" : purchaseOrder.getQuantityInEntryUnit());
                }
            }
            openQuantity = purchaseOrder.getOpenQuantity();
            System.out.println("openQuantity---->" + openQuantity);
            System.out.println("sum---->" + sum);
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

    public List<PurchaseOrderSubContract> splitPurchaseOrder(List<PurchaseOrderSubContract> purchaseOrderList, PurchaseOrderSubContract splitPurchaseOrder){

        PurchaseOrderSubContract sub = new PurchaseOrderSubContract(splitPurchaseOrder.getPurchaseOrder(), splitPurchaseOrder.getPurchaseOrderItem(),
                splitPurchaseOrder.getSupplier(), splitPurchaseOrder.getCreationDate(),
                splitPurchaseOrder.getMaterial(), splitPurchaseOrder.getPurchaseOrderItemText(),
                splitPurchaseOrder.getPlant(), splitPurchaseOrder.getDeliveryDate(),
                splitPurchaseOrder.getUnit(), splitPurchaseOrder.getOrderQuantity(),
                splitPurchaseOrder.getOpenQuantity(), splitPurchaseOrder.getStorageLocation(),
                splitPurchaseOrder.isBatchFlag(), splitPurchaseOrder.getModel(), splitPurchaseOrder.getSerialFlag(),
                splitPurchaseOrder.getComponentQty(), splitPurchaseOrder.getComponentQtyConsumed(),
                splitPurchaseOrder.getComponent(), splitPurchaseOrder.getGrquantity(), splitPurchaseOrder.getComponentDesc());
        sub.setSubOrder(true);
        purchaseOrderList.add(sub);
        Comparator<PurchaseOrderSubContract> comp = new ComparatorSubContract();
        Collections.sort(purchaseOrderList, comp);
        //purchaseOrderList = purchaseOrderList.stream().sorted(Comparator.comparing(PurchaseOrder::getPurchaseOrderItemNr)).collect(Collectors.toList());
        return purchaseOrderList;
    }



    public List<PurchaseOrderSubContract> syncInData(PurchaseOrderQuery purchaseOrderQuery) throws AuthorizationException, GeneralException {
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
                    filterCreateDate = "(POCreationDate ge datetime'" + createDateFrom
                            + "' and POCreationDate le datetime'" + createDateTo + "' ) ";
                }else{
                    if(StringUtils.isNotEmpty(purchaseOrderQuery.getCreateDateFrom())){
                        String createDateFrom = purchaseOrderQuery.getCreateDateFrom() + "T00:00:00";
                        filterCreateDate = "(POCreationDate ge datetime'" + createDateFrom +"' ) ";
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
                    filterDeliveryDate = "(POItemDeliveryDate ge datetime'" + deliveryDateFrom
                            + "' and POItemDeliveryDate le datetime'" + deliveryDateTo + "' ) ";
                }else{
                    if(StringUtils.isNotEmpty(purchaseOrderQuery.getDeliveryDateFrom())){
                        String deliveryDateFrom = purchaseOrderQuery.getDeliveryDateFrom() + "T00:00:00";
                        filterDeliveryDate = "(POItemDeliveryDate ge datetime'" + deliveryDateFrom +"' ) ";
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
                    filter = filter + " and " + "(vendorNo eq '" + filterSupplier + "')";
                }else{
                    filter = "$filter="  + "(vendorNo '" + filterSupplier + "')";
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
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_sub_contract_in)
                + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client)
                + filter + "&$orderby=PurchaseOrder, PurchaseOrderitem";

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
        JSONObject d = jsonObject.getJSONObject("d");
        JSONArray jsonArray = d.getJSONArray("results");
        List<PurchaseOrderSubContract> all = new ArrayList<>();
        String locations = userController.getUserLocationString();
        for(int i = 0; i < jsonArray.size(); i++) {
            try {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String poNr = objectI.getString("PurchaseOrder");
                String poItem = objectI.getString("PurchaseOrderitem");
                String component = objectI.getString("component");
                String componentdesc = objectI.getString("componentdesc");

                String supplier = objectI.getString("vendorNo");
                long creationDate = DateUtils.jsonDateToTimeStamp(objectI.getString("POCreationDate"));
                String materialNr = objectI.getString("materialno");
                String purchaseOrderItemText = objectI.getString("PurchaseOrderItemText");
                String plant = objectI.getString("plant");
                String storageLocation = objectI.getString("Storagelocation");
                long deliveryDate = DateUtils.jsonDateToTimeStamp(objectI.getString("POItemDeliveryDate"));
                String unit = objectI.getString("Unit");
                double orderQuantity = objectI.getDouble("orderquantity");
                double grquantity = objectI.getDouble("grquantity");
                double openQuantity = orderQuantity - grquantity;
                double componentqty = objectI.getDouble("componentqty");
                double Componentqtyconsumed = objectI.getDouble("Componentqtyconsumed");
                boolean batchFlag = objectI.getBoolean("Batchflag");

                String model = objectI.getString("Model");
                if(openQuantity <= 0){
                    continue;
                }
                String serialFlag = objectI.getString("Serialflag");

                PurchaseOrderSubContract po = new PurchaseOrderSubContract(poNr, poItem, supplier, creationDate, materialNr,
                        purchaseOrderItemText, plant, deliveryDate, unit, orderQuantity, openQuantity,
                        storageLocation, batchFlag, model, serialFlag, componentqty, Componentqtyconsumed,
                        component, grquantity, componentdesc) ;
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
        List<PurchaseOrderSubContract> ordersGrouped = new ArrayList<>();
        if(all != null && all.size() > 0){
            Map<String, List<PurchaseOrderSubContract>> resultList = all.stream().collect(Collectors.groupingBy(PurchaseOrderSubContract::groupMaterial));

            if(resultList != null && resultList.size() > 0) {
                for (Map.Entry<String, List<PurchaseOrderSubContract>> entry : resultList.entrySet()) {
                    String k = entry.getKey();
                    List<PurchaseOrderSubContract> orders = entry.getValue();

                    for(PurchaseOrderSubContract t : orders){
                        List<PurchaseOrderSubContract> findPurchaseOrderSubContractList = all.stream().filter(o ->
                                StringUtils.equalsIgnoreCase(t.getPurchaseOrderItem() , o.getPurchaseOrderItem())).collect(Collectors.toList());
                        List<Component> components = new ArrayList<>();
                        for(PurchaseOrderSubContract findPurchaseOrderSubContract : findPurchaseOrderSubContractList){
                            Component component = new Component(findPurchaseOrderSubContract.getPurchaseOrder(),
                                    findPurchaseOrderSubContract.getPurchaseOrderItem(), findPurchaseOrderSubContract.getMaterial(),
                                    findPurchaseOrderSubContract.getPurchaseOrderItemText(),
                                    findPurchaseOrderSubContract.getComponentQty(),
                                    0, findPurchaseOrderSubContract.getBatch(), findPurchaseOrderSubContract.getComponent(),
                                    findPurchaseOrderSubContract.getComponentDesc(),
                                    findPurchaseOrderSubContract.getComponentQty() - findPurchaseOrderSubContract.getComponentQtyConsumed());
                            components.add(component);
                        }
                        t.setComponents(components);
                        ordersGrouped.add(t);
                        break;
                    }

                }
            }
        }
        Comparator<PurchaseOrderSubContract> comp = new ComparatorItemSubContract();
        Collections.sort(ordersGrouped, comp);
        LogUtils.d(TAG, "ordersGrouped-----> ------> " + JSON.toJSONString(ordersGrouped));
        return ordersGrouped;
    }


    public List<PurchaseOrderSubContractInPostingRequest> buildInRequest(List<PurchaseOrderSubContract> purchaseOrders, String deliveryNumber,
                                                    String postingDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        postingDate = postingDate + "T00:00:00";
        String goodsMovementCode = "01";
        String materialDocumentHeaderText = deliveryNumber;
        String goodsMovementType = "101";
        String goodsMovementRefDocType = "B";
        String documentType = "10";
        List<PurchaseOrderSubContractInPostingRequest> results = new ArrayList<>();
        for(PurchaseOrderSubContract purchaseOrder : purchaseOrders){
            if(StringUtils.isEmpty(purchaseOrder.getQuantityInEntryUnit())){
                continue;
            }

            String material = purchaseOrder.getMaterial();
            String plant = purchaseOrder.getPlant();
            String storageLocation = purchaseOrder.getStorageLocation();
            String manufactureDate = documentDate;
            String quantityInEntryUnit = purchaseOrder.getQuantityInEntryUnit();
            String purchaseOrderNr = purchaseOrder.getPurchaseOrder();
            String purchaseOrderItem = purchaseOrder.getPurchaseOrderItem();
            String batch = purchaseOrder.getBatch();

            List<SerialNumberPoReturn> serialNumberList = new ArrayList<>();
            for(String sn : purchaseOrder.getSnList()){
                SerialNumberPoReturn serialNumberPoReturn = new SerialNumberPoReturn(sn);
                serialNumberList.add(serialNumberPoReturn);
            }

            List<ComponentItem> componentItems = new ArrayList<>();
            for(Component component : purchaseOrder.getComponents()){
                ComponentItem item = new ComponentItem(component.getComponentNo(), component.getQuantity() + "",
                        "543", "O");
                componentItems.add(item);
            }
            PurchaseOrderSubContractInPostingRequest request =
                    new PurchaseOrderSubContractInPostingRequest(goodsMovementCode, postingDate,
                            documentDate, goodsMovementType, goodsMovementRefDocType, material,
                            plant, storageLocation, manufactureDate, quantityInEntryUnit, purchaseOrderNr,
                            purchaseOrderItem, materialDocumentHeaderText, "", "",
                            "", documentType, serialNumberList, componentItems);
            results.add(request);
        }

        return results;
    }

    public HttpResponse inPosting(List<PurchaseOrderSubContractInPostingRequest> request) throws AuthorizationException, GeneralException {
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_sub_contract_in_post) + app.getString(R.string.sap_url_client);
        HttpRequestUtil http = new HttpRequestUtil();
        //Map<String, String> tokenHeaders = http.getToken(url);
        //LogUtils.d("PurchaseOrderPosting", "Headers---->" + JSON.toJSONString(tokenHeaders));
        LogUtils.d("PurchaseOrderInPosting", "url--->" + url);

        String json = JSON.toJSONString(request);
        LogUtils.d("PurchaseOrderInPosting", "json--->" + json);
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, json, null);
        LogUtils.d("PurchaseOrderInPosting", "Response--->" + httpResponse.getResponseString());
        LogUtils.d("PurchaseOrderInPosting", "Response--Code-->" + httpResponse.getCode());
        if(httpResponse.getCode() != 201){
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONArray array = jsonObject.getJSONArray("objectMsg");
            if(array != null && array.size() > 0){
                JSONObject object = (JSONObject)array.get(0);
                if(object != null){
                    String errorMessage = object.getString("message");
                    LogUtils.e("PurchaseOrderInPosting", "errorMessage-->" + errorMessage);
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

    public boolean isShowCheckBtn(List<PurchaseOrderSubContract> list){
        for(PurchaseOrderSubContract order: list){
            if(StringUtils.isNotEmpty(order.getSerialFlag())){
                return true;
            }
        }
        return false;
    }

    public List<PurchaseOrderSubContract> splitBatch(List<PurchaseOrderSubContract> list, List<SerialInfo> items){
        List<PurchaseOrderSubContract> batchFlagOrders = new ArrayList<>();
        List<PurchaseOrderSubContract> spiltOrders = new ArrayList<>();

        //按序列号分别找出批次号，有多少序列号创建多少order对象。
        for(SerialInfo serialInfo : items){
            PurchaseOrderSubContract batchFlagOrder = findOrderBySerial(list, serialInfo.getSerialnumber());
            if(batchFlagOrder != null){
                PurchaseOrderSubContract splitTransferOrder = (PurchaseOrderSubContract)batchFlagOrder.clone();
                splitTransferOrder.setBatch(serialInfo.getBatch());
                splitTransferOrder.setSn(serialInfo.getSerialnumber());
                //batchFlagOrder.setBatchNo(serialInfo.getBatch());
                //System.out.println("batchFlagOrder-------->" + JSON.toJSONString(splitTransferOrder));
                batchFlagOrders.add(splitTransferOrder);
            }
        }

        //把用序列号创建出来的order进行分组，用item号和
        Map<String, List<PurchaseOrderSubContract>> resultList = batchFlagOrders.stream().collect(Collectors.groupingBy(PurchaseOrderSubContract::groupItem));
        LogUtils.d("PurchaseOrderSubContract", "resultList---->" + JSON.toJSONString(resultList, SerializerFeature.DisableCircularReferenceDetect));
        if(resultList != null && resultList.size() > 0){
            for (Map.Entry<String, List<PurchaseOrderSubContract>> entry : resultList.entrySet()) {
                String k = entry.getKey();
                List<PurchaseOrderSubContract> orders = entry.getValue();
                PurchaseOrderSubContract order = null;
                List<String> snList = new ArrayList<>();
                for(PurchaseOrderSubContract t : orders){
                    if(order == null){
                        order = (PurchaseOrderSubContract)t.clone();
                    }
                    snList.add(t.getSn());
                }
                order.setSnList(snList);
                //order.setOpenQuantity(Double.valueOf(snList.size()));
                order.setQuantityInEntryUnit(snList.size() + "");

                spiltOrders.add(order);
            }
        }
        List<PurchaseOrderSubContract> noSerialOrderList = list.stream().filter(o ->
                StringUtils.isEmpty(o.getSerialFlag())).collect(Collectors.toList());
        spiltOrders.addAll(noSerialOrderList);
        String item = "";
        for(PurchaseOrderSubContract order : spiltOrders){
            if(StringUtils.equalsIgnoreCase(item, order.getMaterial())){
                order.setSubOrder(true);
            }
            item = order.getMaterial();
        }
        Comparator<PurchaseOrderSubContract> comp = new ComparatorPoSubContractItem();
        Collections.sort(spiltOrders, comp);
        LogUtils.d("Picking", "spiltOrders---->" + JSON.toJSONString(spiltOrders));

        return spiltOrders;
    }

    private PurchaseOrderSubContract findOrderBySerial(List<PurchaseOrderSubContract> list, String sn){
        for(PurchaseOrderSubContract order : list){
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

    public PurchaseOrderSubContract findParentItem(List<PurchaseOrderSubContract> list, PurchaseOrderSubContract order){
        PurchaseOrderSubContract findOrder = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(order.getPurchaseOrder() + "-"
                        + order.getPurchaseOrderItem() + "-" + order.getMaterial() + "-" + false, o.getParentItem())).findAny().orElse(null);
        return findOrder;
    }

    public int getItemQuantityTotal(List<PurchaseOrderSubContract> list, PurchaseOrderSubContract order){
        List<PurchaseOrderSubContract> findPurchaseOrderSubContractList = list.stream().filter(o ->
                StringUtils.equalsIgnoreCase(order.getPurchaseOrder() + "-"
                        + order.getPurchaseOrderItem() + "-" + order.getMaterial(), o.getItems())).collect(Collectors.toList());
        //累加所有拆分行的扫码数量用于最后校验。
        int total = 0;
        for(PurchaseOrderSubContract findOrder : findPurchaseOrderSubContractList){
            if(findOrder != null){
                //System.out.println("ScanQuantity---->" + findPrototypeBorrow.getScanQuantity());
                total += findOrder.getScanQuantity();
            }
        }
        return total;
    }

    public void setComponentCount(PurchaseOrderSubContract purchaseOrder){
        double percent = purchaseOrder.getOrderQuantity() / purchaseOrder.getScanQuantity();
        System.out.println("OrderQuantity---->" + purchaseOrder.getOrderQuantity()
                + " / ScanQuantity----> " + purchaseOrder.getScanQuantity() + "percent----->" + percent);

        for(Component component : purchaseOrder.getComponents()){
            if(purchaseOrder.getOpenQuantity() == purchaseOrder.getScanQuantity()){
                component.setQuantity(component.getOpenQuantity());
            }else{
                if(purchaseOrder.getScanQuantity() == 1){
                    component.setQuantity(1);
                }else{
                    System.out.println("TotalQuantity----->" + component.getTotalQuantity());
                    double componentQuantity = Math.round(component.getTotalQuantity() / percent);
                    component.setQuantity(componentQuantity);
                    if(componentQuantity < 1){
                        component.setQuantity(1);
                    }
                }
            }
        }
    }
}
