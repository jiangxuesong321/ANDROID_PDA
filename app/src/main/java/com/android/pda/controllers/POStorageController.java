package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.database.pojo.PurchaseOrder;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.POStorageQuery;
import com.android.pda.utils.HttpRequestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POStorageController {
    protected static final String TAG = POStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();

    public List<MaterialDocument> syncData(POStorageQuery query) throws Exception {
        List<MaterialDocument> materialDocumentList = new ArrayList<>();
        MaterialDocument materialDocumentHeader = getMaterialDocumentHeader(query);
        if (materialDocumentHeader == null || materialDocumentHeader.getMaterialDocument() == null) {
            return materialDocumentList;
        } else {
            materialDocumentList = getMaterialDocumentItemList(materialDocumentHeader);
        }
        return materialDocumentList;
    }

    public List<PurchaseOrder> syncPOData(POStorageQuery query) throws Exception {
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
        purchaseOrderList = getPOItemList(query);
        return purchaseOrderList;
    }

    /**
     * 获取物料凭证抬头信息
     *
     * @param query
     * @return
     */
    public MaterialDocument getMaterialDocumentHeader(POStorageQuery query) {
        MaterialDocument materialDocumentHeader = new MaterialDocument();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        String materialDocumentYear = "";
        String materialDocument = "";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_header) + "?$format=json&$filter=MaterialDocument eq '" + query.getMaterialDocument() + "'";
        try {
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponse != null && httpResponse.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                if (JaResults.size() > 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));
                    materialDocumentYear = jsonObject.getString("MaterialDocumentYear");
                    materialDocument = jsonObject.getString("MaterialDocument");
                    materialDocumentHeader.setMaterialDocument(materialDocument);
                    materialDocumentHeader.setMaterialDocumentYear(materialDocumentYear);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getMaterialDocumentHeader failed:" + e);
        }
        return materialDocumentHeader;
    }

    /**
     * 获取物料凭证行项目列表
     *
     * @param query
     * @return
     */

    public List<MaterialDocument> getMaterialDocumentItemList(MaterialDocument query) {
        List<MaterialDocument> materialDocumentItem = new ArrayList<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_item) + "?$format=json&$filter=MaterialDocument eq '" + query.getMaterialDocument() + "'" +
                    " and MaterialDocumentYear eq '" + query.getMaterialDocumentYear() + "'";
            HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponseItem != null && httpResponseItem.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                for (Object object : JaResults) {          // 使用foreach循环遍历ArravList对象
                    JSONObject objectMaterialDocument = JSONObject.parseObject(JSONObject.toJSONString(object));
                    MaterialDocument materialDocument = new MaterialDocument();
                    materialDocument.setMaterial(objectMaterialDocument.getString("Material"));
                    materialDocument.setMaterialDocument(objectMaterialDocument.getString("MaterialDocument"));
                    materialDocument.setMaterialDocumentItem(objectMaterialDocument.getString("MaterialDocumentItem"));
                    materialDocument.setBatch(objectMaterialDocument.getString("Batch"));
                    materialDocument.setDescription("");
                    materialDocument.setPlant(objectMaterialDocument.getString("Plant"));
                    materialDocument.setStorageLocation(objectMaterialDocument.getString("StorageLocation"));
                    materialDocument.setGoodsMovementType(objectMaterialDocument.getString("GoodsMovementType"));
                    materialDocument.setPurchaseOrder(objectMaterialDocument.getString("PurchaseOrder"));
                    materialDocument.setPurchaseOrderItem(objectMaterialDocument.getString("PurchaseOrderItem"));
                    materialDocument.setEntryUnit(objectMaterialDocument.getString("EntryUnit"));
                    materialDocument.setQuantityInEntryUnit(objectMaterialDocument.getString("QuantityInEntryUnit"));
                    materialDocument.setSupplier(objectMaterialDocument.getString("Supplier"));
                    materialDocumentItem.add(materialDocument);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getMaterialDocumentItemList failed:" + e);
        }
        return materialDocumentItem;
    }

    /**
     * 获取物料凭证行项目列表
     *
     * @param list
     * @return
     */

    public Map<String, String> createMaterialDocument(List<MaterialDocument> list) {
        Map<String, String> result = new HashMap<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_create);
            Login loginInfo = loginController.getLoginUser();
            JSONObject param = new JSONObject();
            param.put("CreatedByUser", "dlw004"); //loginInfo.getZuid());
            param.put("GoodsMovementCode", "101");
            JSONArray jaItem = new JSONArray();
            for (MaterialDocument materialDocument : list) {
                JSONObject objectItem = new JSONObject();
                objectItem.put("Material", materialDocument.getMaterial());
                objectItem.put("Plant", materialDocument.getPlant());
                objectItem.put("StorageLocation", materialDocument.getStorageLocation());
                objectItem.put("Batch", materialDocument.getBatch());
                objectItem.put("Supplier", materialDocument.getSupplier());
                objectItem.put("PurchaseOrder", materialDocument.getPurchaseOrder());
                objectItem.put("PurchaseOrderItem", materialDocument.getPurchaseOrderItem());
                objectItem.put("QuantityInEntryUnit", materialDocument.getQuantityInEntryUnit());
                objectItem.put("EntryUnit", materialDocument.getEntryUnit());
                jaItem.add(objectItem);
            }
            param.put("to_MaterialDocumentItem", jaItem);
            System.out.println("post json" + param.toJSONString());
            String paramJson = param.toString();
            HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, paramJson, null);
            if (httpResponseItem != null && httpResponseItem.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
            } else {
                result.put("materialDocument", "");
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonError = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("error")));
                JSONObject jsonMessage = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("message")));
                result.put("error", jsonMessage.getString("value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function createMaterialDocument failed:" + e);
        }

        return result;
    }

    /**
     * 获取物料凭证行项目列表
     *
     * @param query
     * @return
     */

    public List<PurchaseOrder> getPOItemList(POStorageQuery query) {
        List<PurchaseOrder> poItem = new ArrayList<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_item) + "?$format=json&$filter=" +
                    "PurchaseOrder eq '" + query.getPoNumber() + "'";
            HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponseItem != null && httpResponseItem.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                for (Object object : JaResults) {          // 使用foreach循环遍历ArravList对象
                    JSONObject objectPo = JSONObject.parseObject(JSONObject.toJSONString(object));
                    PurchaseOrder purchaseOrder = new PurchaseOrder();
                    purchaseOrder.setMaterial(objectPo.getString("Material"));
                    purchaseOrder.setPurchaseOrder(objectPo.getString("PurchaseOrder"));
                    purchaseOrder.setPurchaseOrderItem(objectPo.getString("PurchaseOrderItem"));
                    purchaseOrder.setBatch(objectPo.getString("Batch"));
                    purchaseOrder.setDescription("");
                    purchaseOrder.setPlant(objectPo.getString("Plant"));
                    purchaseOrder.setStorageLocation(objectPo.getString("StorageLocation"));
                    purchaseOrder.setOrderQuantity(objectPo.getString("OrderQuantity"));
                    purchaseOrder.setPurchaseOrderQuantityUnit(objectPo.getString("PurchaseOrderQuantityUnit"));
                    purchaseOrder.setSupplierMaterialNumber(objectPo.getString("SupplierMaterialNumber"));
                    poItem.add(purchaseOrder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getPOItemList failed:" + e);
        }
        return poItem;

    }
}
