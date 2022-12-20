package com.sunmi.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.StorageLocation;
import com.sunmi.pda.exceptions.AuthorizationException;
import com.sunmi.pda.exceptions.GeneralException;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.GeneralMaterialDocumentItem;
import com.sunmi.pda.models.GeneralMaterialDocumentItemResults;
import com.sunmi.pda.models.GeneralPostingRequest;
import com.sunmi.pda.models.HttpResponse;
import com.sunmi.pda.models.SalesInvoiceQuery;
import com.sunmi.pda.models.SerialNumber;
import com.sunmi.pda.models.SerialNumberResults;
import com.sunmi.pda.models.MaterialVoucher;
import com.sunmi.pda.models.TransferOrder;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.HttpRequestUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MaterialVoucherController {
    protected static final String TAG = MaterialVoucherController.class.getSimpleName();
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final UserController userController = app.getUserController();

    /**
     * @param query user SalesInvoiceQuery, only orderNumber in query is used for materialVoucher
     * @return
     * @throws Exception
     */
    public List<MaterialVoucher> syncData(SalesInvoiceQuery query)  throws AuthorizationException, GeneralException {
        String filter = "" ;
        String locations = userController.getUserLocationString();
        LogUtils.d(TAG, "locations--->" + locations);
        if (query == null || StringUtils.isEmpty(query.getDeliveryDocument())) {
            return null;
        } else {
            filter += "$filter=rsnum eq '" + query.getDeliveryDocument() + "'";
        }

        String filterPlant = userController.getUserPlantsFilter("werks");
        filter = Util.addFilter(filter, filterPlant);

        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_voucher)
                + app.getString(R.string.url_language_param) + app.getString(R.string.sap_url_client)
                + "&" + app.getString(R.string.material_voucher_order_by)
                + "&" + filter;

        LogUtils.d(TAG, "Url--->" + url);
        HttpRequestUtil http = new HttpRequestUtil();
        HttpResponse httpResponse = http.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
        LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
        try {
            JSONObject jsonObject = JSONObject.parseObject(httpResponse.getResponseString());
            JSONObject d = jsonObject.getJSONObject("d");
            JSONArray jsonArray = d.getJSONArray("results");
            List<MaterialVoucher> all = new ArrayList<>();
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject objectI = jsonArray.getJSONObject(i);
                String mblnr = objectI.getString("mblnr");
                String mjahr = objectI.getString("mjahr");
                String rsnum = objectI.getString("rsnum");
                String zzrqty = objectI.getString("zzrqty");
                String bwart = objectI.getString("bwart");
                String zzrqdt = objectI.getString("zzrqdt");
                String zzrqps = objectI.getString("zzrqps");
                String zzreps = objectI.getString("zzreps");
                String zzreds = objectI.getString("zzreds");
                String zzrect = objectI.getString("zzrect");
                String rspos = objectI.getString("rspos");
                String matnr = objectI.getString("matnr");
                String maktx = objectI.getString("maktx");
                String zzspec = objectI.getString("zzspec");
                String zzvendor_ty = objectI.getString("zzvendor_ty");
                String charg = objectI.getString("charg");
                String werks = objectI.getString("werks");
                String name1 = objectI.getString("name1");
                String lgort = objectI.getString("lgort");
                String lgobe = objectI.getString("lgobe");
                String menge = objectI.getString("menge");
                String meins = objectI.getString("meins");
                String bstmg = objectI.getString("bstmg");
                String umlgo = objectI.getString("umlgo");
                String lgobe_js = objectI.getString("lgobe_js");
                String sn = objectI.getString("Serialnumber");
                boolean batchFlag = objectI.getBoolean("BatchFlag");
                String model = objectI.getString("Model");
                String serialFlag = objectI.getString("SerialFlag");
                bstmg = menge;
                MaterialVoucher materialVoucher = new MaterialVoucher(mblnr, mjahr, rsnum, zzrqty, bwart, zzrqdt, zzrqps, zzreps, zzreds, zzrect, rspos, matnr, maktx,
                        zzspec, zzvendor_ty, charg, werks, name1, lgort, lgobe, menge, meins, bstmg, umlgo, lgobe_js, sn, batchFlag, model, serialFlag);

                if (userController.userHasAllLocation() || StringUtils.isEmpty(materialVoucher.getUmlgo()) || StringUtils.containsIgnoreCase(locations, materialVoucher.getUmlgo()) ) {
                    all.add(materialVoucher);
                }

            }
            return all;

        } catch (Exception e){
            e.printStackTrace();
            LogUtils.e(TAG, "get transfer order Error ------> " + e.getMessage());
        }

        return  null;
    }

    public GeneralPostingRequest buildRequest(List<MaterialVoucher> materialVouchers, StorageLocation location,
                                              String confirmDate){
        String documentDate = DateUtils.dateToString(new Date(), DateUtils.FormatY_M_D) + "T00:00:00";
        String postingDate = confirmDate + "T00:00:00";
        String goodsMovementCode = "04";
        String  goodsMovementType = "315";
        String materialDocumentHeaderText = "";
        Map<String, List<MaterialVoucher>> groupBy = materialVouchers.stream().collect(Collectors.groupingBy(MaterialVoucher::getRspos));

        //调拨接收发送的请求，需要加工成和其它功能过账的请求结构一致
        List<MaterialVoucher> groupOrder = new ArrayList<>();
        for (String key : groupBy.keySet()) {
            MaterialVoucher findMaterialVoucher = materialVouchers.stream().filter(o ->
                    StringUtils.equalsIgnoreCase(key, o.getRspos())).findAny().orElse(null);
            List<String> snList = new ArrayList<>();
            List<MaterialVoucher> findMaterialVoucherList = materialVouchers.stream().filter(o ->
                    StringUtils.equalsIgnoreCase(key, o.getRspos())).collect(Collectors.toList());
            for(MaterialVoucher order : findMaterialVoucherList){
                if(order != null){
                    snList.add(order.getSerialNumber());
                }
            }
            findMaterialVoucher.setSnList(snList);
            //System.out.println("snList---->" + snList.size());
            groupOrder.add(findMaterialVoucher);
        }

        //System.out.println("groupOrder---->" + groupOrder.size());
        List<GeneralMaterialDocumentItemResults> results = new ArrayList<>();
        for(MaterialVoucher materialVoucher : groupOrder){
            String material = materialVoucher.getMatnr();
            String plant = materialVoucher.getWerks();
            String storageLocation = materialVoucher.getLgort();
            String issuingOrReceivingStorageLoc = location.getStorageLocation();
            String goodsMovementRefDocType = "";

            String quantityInEntryUnit = String.valueOf(materialVoucher.getSnList().size());
            if(StringUtils.isEmpty(materialVoucher.getSerialFlag())){
                quantityInEntryUnit = materialVoucher.getMenge();
            }
            String reservation = "";
            String reservationItem = "";
            String batch = materialVoucher.getCharg();
            String materialDocumentItemText =  "";
            materialDocumentHeaderText = materialVoucher.getRsnum();
            boolean reservationIsFinallyIssued = true;

            SerialNumber serialNumber = new SerialNumber();

            List<SerialNumberResults> serialNumberResults = new ArrayList<>();
            if(materialVoucher.getSerialNumber() != null){
                for(String sn : materialVoucher.getSnList()){
                    SerialNumberResults serialNumberResult = new SerialNumberResults(sn);
                    serialNumberResults.add(serialNumberResult);
                }
            }
            serialNumber.setResults(serialNumberResults);
            GeneralMaterialDocumentItemResults itemResults = new GeneralMaterialDocumentItemResults();
            itemResults.setMaterial(material);
            itemResults.setPlant(plant);
            itemResults.setStorageLocation(issuingOrReceivingStorageLoc);  //都要显示接收库位
            itemResults.setIssuingOrReceivingStorageLoc(issuingOrReceivingStorageLoc); //接收
            itemResults.setGoodsMovementRefDocType(goodsMovementRefDocType);
            itemResults.setGoodsMovementType(goodsMovementType);
            itemResults.setQuantityInEntryUnit(quantityInEntryUnit);

            itemResults.setReservation(reservation);
            itemResults.setReservationItem(reservationItem);
            itemResults.setBatch(batch);
            itemResults.setMaterialDocumentItemText(materialDocumentItemText);
            itemResults.setSerialNumber(serialNumber);
            results.add(itemResults);
        }

        GeneralMaterialDocumentItem documentItem = new GeneralMaterialDocumentItem(results);
        String logistics = "";

        GeneralPostingRequest request = new GeneralPostingRequest(documentDate, postingDate,
                goodsMovementCode, materialDocumentHeaderText, logistics, "",
                "6", documentItem);
        return request;
    }

}
