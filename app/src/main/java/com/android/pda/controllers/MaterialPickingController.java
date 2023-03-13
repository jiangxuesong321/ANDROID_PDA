package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.MaterialPickingQuery;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MaterialPickingController {
    protected static final String TAG = ProductionStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();

    public List<Material> syncData(MaterialPickingQuery query) throws Exception {
//        List<Material> materialList = new ArrayList<>();
//
////        List<Material> materialDescriptionList = getMaterialDescription(materialList);
//
////        return materialDescriptionList;
//    }
//
////    public static List<Material> getMaterialDescription(List<Material> materialList) {
////        List<Material> materialReturnList = new ArrayList<>();
////        HttpRequestUtil httpUtil = new HttpRequestUtil();
////
////        for (Material queryItem : materialList) {
////            try {
////                String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_description_specific) + "?$format=json&$filter=Product eq '" + queryItem.getMaterial() + "'" +
////                        " and Language eq 'ZH'";
////                HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
////                if (httpResponseItem != null && httpResponseItem.getCode() == 200) {
////                    JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
////                    JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
////                    JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
////                    for (Object object : JaResults) {          // 使用 foreach 循环遍历 ArrayList 对象
////                        JSONObject objectMaterial = JSONObject.parseObject(JSONObject.toJSONString(object));
//////                        Material material = new Material();
////                        queryItem(objectMaterial.getString("ProductDescription"));
////                        if (StringUtils.isNotEmpty(objectMaterial.getString("PurchaseOrder"))) {
////                            PurchaseOrder purchaseOrder = new PurchaseOrder();
////                            purchaseOrder.setPurchaseOrder(objectMaterial.getString("PurchaseOrder"));
////                            List<PurchaseOrder> purchaseOrderList = getPOItemList(purchaseOrder);
////                            if (purchaseOrderList.size() > 0) {
////                                queryItem.setDescription(purchaseOrderList.get(0).getDescription());
////                            }
////                        } else {
////                            queryItem.setDescription("");
////                        }
////                        queryItem.setPlant(objectMaterial.getString("Plant"));
////                        queryItem.setStorageLocation(objectMaterial.getString("StorageLocation"));
////                        queryItem.setGoodsMovementType(objectMaterial.getString("GoodsMovementType"));
////                        queryItem.setPurchaseOrder(objectMaterial.getString("PurchaseOrder"));
////                        queryItem.setEntryUnit(objectMaterial.getString("EntryUnit"));
////                        queryItem.setQuantityInEntryUnit(objectMaterial.getString("QuantityInEntryUnit"));
////                        queryItem.setSupplier(objectMaterial.getString("Supplier"));
////                        materialReturnList.add(material);
////                    }
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////                LogUtils.e(TAG, "function getMaterialDocumentItemList failed:" + e);
////            }
//
////        }}
        return null;
    }
}