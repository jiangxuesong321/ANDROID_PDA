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

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class POStorageController {
    protected static final String TAG = POStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();

    /**
     * 获取物料凭证行项目信息
     *
     * @param query
     * @return
     */
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

    /**
     * 获取采购订单行项目信息
     *
     * @param query
     * @return
     */
    public List<PurchaseOrder> syncPOData(POStorageQuery query) throws Exception {
        List<PurchaseOrder> purchaseOrderList = new ArrayList<>();

        PurchaseOrder purchaseOrderHeader = getPOHeader(query);
        if (purchaseOrderHeader == null || purchaseOrderHeader.getPurchaseOrder() == null) {
            return purchaseOrderList;
        } else {
            purchaseOrderList = getPOItemList(purchaseOrderHeader);
        }
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
                    if (objectMaterialDocument.getString("PurchaseOrder") != null) {
                        PurchaseOrder purchaseOrder = new PurchaseOrder();
                        purchaseOrder.setPurchaseOrder(objectMaterialDocument.getString("PurchaseOrder"));
                        List<PurchaseOrder> purchaseOrderList = getPOItemList(purchaseOrder);
                        if (purchaseOrderList.size() > 0) {
                            materialDocument.setDescription(purchaseOrderList.get(0).getDescription());
                        }
                    }
                    materialDocument.setInventoryStockType(objectMaterialDocument.getString("InventoryStockType"));
                    materialDocument.setPlant(objectMaterialDocument.getString("Plant"));
                    materialDocument.setStorageLocation(objectMaterialDocument.getString("StorageLocation"));
                    materialDocument.setGoodsMovementType(objectMaterialDocument.getString("GoodsMovementType"));
                    materialDocument.setPurchaseOrder(objectMaterialDocument.getString("PurchaseOrder"));
                    materialDocument.setPurchaseOrderItem(objectMaterialDocument.getString("PurchaseOrderItem"));
                    materialDocument.setEntryUnit(objectMaterialDocument.getString("EntryUnit"));
                    materialDocument.setQuantityInEntryUnit(objectMaterialDocument.getString("QuantityInEntryUnit"));
                    materialDocument.setSupplier(objectMaterialDocument.getString("Supplier"));
                    materialDocument.setGoodsMovementRefDocType(objectMaterialDocument.getString("GoodsMovementRefDocType"));
                    //获取供应商批次
                    if (StringUtils.isNotEmpty(materialDocument.getBatch()) && StringUtils.isNotEmpty(materialDocument.getPlant())) {
                        String supplierBatch = getSupplierBatch(materialDocument);
                        if (StringUtils.isNotEmpty(supplierBatch)) {
                            materialDocument.setSupplierBatch(supplierBatch);
                        }
                    }
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
     * 更新批次特性值货位信息
     *
     * @param list
     * @return
     */
    public Map<String, String> updateBatchCharcValue(List<MaterialDocument> list) {
        Map<String, String> result = new HashMap<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        JSONObject param = new JSONObject();
        try {
            MaterialDocument materialDocument = list.get(0);
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_batch_char_value_get) +
                    "?$format=json&$filter=Batch eq '" + materialDocument.getBatch() + "'";
            String paramJson = "{}";
            HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, paramJson, null);
            if (httpResponseItem != null && httpResponseItem.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                Login loginInfo = loginController.getLoginUser();
                String city = loginInfo.getZcity();
                String CharcInternalID = "";
                if (city != null && city.equals(app.getString(R.string.text_login_user_city_beijing))) {
                    CharcInternalID = app.getString(R.string.text_storage_bin_city_beijing);
                } else if (city != null && city.equals(app.getString(R.string.text_login_user_city_suzhou))) {
                    CharcInternalID = app.getString(R.string.text_storage_bin_city_suzhou);
                }
                if (JaResults.size() > 0) {
                    for (int i = 0; i < JaResults.size(); i++) {
                        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(i)));
                        if (jsonObject.getString("CharcInternalID") != null && jsonObject.getString("CharcInternalID").equals(CharcInternalID)) {
                            if (StringUtils.isEmpty(jsonObject.getString("CharcValue"))) {
                                param.put("Material", jsonObject.getString("Material"));
                                param.put("Batch", jsonObject.getString("Batch"));
                                param.put("CharcInternalID", jsonObject.getString("CharcInternalID"));
                                param.put("CharcValuePositionNumber", jsonObject.getString("CharcValuePositionNumber"));
                                param.put("CharcValueDependency", jsonObject.getString("CharcValueDependency"));
                                param.put("CharcValue", materialDocument.getStorageBin());
                                //开始修改批次的特征值货位的信息
                                String updateUrl = app.getOdataService().getHost() + app.getString(R.string.sap_url_batch_char_value_get);
                                HttpResponse httpResponseUpdate = httpUtil.callHttp(updateUrl, HttpRequestUtil.HTTP_POST_METHOD, param.toString(), null);
                                if (httpResponseUpdate != null && (httpResponseUpdate.getCode() == 200 || httpResponseUpdate.getCode() == 201)) {
                                    result.put("success", app.getString(R.string.text_batch_char_value_update_success));
                                    return result;
                                } else {
                                    result.put("error", app.getString(R.string.text_batch_char_value_update_failed));
                                    return result;
                                }
                            } else {
                                result.put("error", app.getString(R.string.text_batch_char_value_exist_error));
                                return result;
                            }
                        }
                    }
                } else {
                    param.put("Material", materialDocument.getMaterial());
                    param.put("Batch", materialDocument.getBatch());
                    param.put("CharcInternalID", CharcInternalID);
                    param.put("CharcValuePositionNumber", "1");
                    param.put("CharcValueDependency", "1");
                    param.put("CharcValue", materialDocument.getStorageBin());
                    //开始修改批次的特征值货位的信息
                    String updateUrl = app.getOdataService().getHost() + app.getString(R.string.sap_url_batch_char_value_get);
                    HttpResponse httpResponseUpdate = httpUtil.callHttp(updateUrl, HttpRequestUtil.HTTP_POST_METHOD, param.toString(), null);
                    if (httpResponseUpdate != null && (httpResponseUpdate.getCode() == 200 || httpResponseUpdate.getCode() == 201)) {
                        result.put("success", app.getString(R.string.text_batch_char_value_update_success));
                        return result;
                    } else {
                        result.put("error", app.getString(R.string.text_batch_char_value_update_failed));
                        return result;
                    }
                }
            } else {
                result.put("error", app.getString(R.string.text_url_get_error));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function updateBatchCharcValue failed:" + e);
            result.put("error", app.getString(R.string.text_url_get_error));
        }
        return result;
    }
    /**
     * 获取采购订单抬头信息
     *
     * @param query
     * @return
     */
    public PurchaseOrder getPOHeader(POStorageQuery query) {
        PurchaseOrder poHeader = new PurchaseOrder();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        String purchaseOrder = "";
        String companyCode = "";
        String supplier = "";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_header) + "?$format=json&$filter=PurchaseOrder eq '" + query.getPoNumber() + "'";
        try {
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponse != null && httpResponse.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                if (JaResults.size() > 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));
                    purchaseOrder = jsonObject.getString("PurchaseOrder");
                    companyCode = jsonObject.getString("CompanyCode");
                    supplier = jsonObject.getString("Supplier");
                    poHeader.setPurchaseOrder(purchaseOrder);
                    poHeader.setCompanyCode(companyCode);
                    poHeader.setSupplier(supplier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getPOHeader failed:" + e);
        }
        return poHeader;
    }

    /**
     * 获取采购订单行项目列表
     *
     * @param purchaseOrderQuery
     * @return
     */

    public List<PurchaseOrder> getPOItemList(PurchaseOrder purchaseOrderQuery) {
        List<PurchaseOrder> poItem = new ArrayList<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_po_item) + "?$format=json&$filter=" +
                    "PurchaseOrder eq '" + purchaseOrderQuery.getPurchaseOrder() + "'";
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
                    purchaseOrder.setDescription(objectPo.getString("PurchaseOrderItemText"));
                    purchaseOrder.setPlant(objectPo.getString("Plant"));
                    purchaseOrder.setStorageLocation(objectPo.getString("StorageLocation"));
//                    purchaseOrder.setOrderQuantity(objectPo.getString("OrderQuantity"));
                    purchaseOrder.setOrderQuantity("");
                    purchaseOrder.setPurchaseOrderQuantityUnit(objectPo.getString("PurchaseOrderQuantityUnit"));
                    purchaseOrder.setSupplierMaterialNumber(objectPo.getString("SupplierMaterialNumber"));
                    purchaseOrder.setSupplier(purchaseOrderQuery.getSupplier());
                    poItem.add(purchaseOrder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getPOItemList failed:" + e);
        }
        return poItem;

    }

    /**
     * 采购收货创建物料凭证
     *
     * @param list
     * @return
     */

    public Map<String, String> createPOMaterialDocument(List<PurchaseOrder> list) {
        Map<String, String> result = new HashMap<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_create);
            Login loginInfo = loginController.getLoginUser();
            JSONObject param = new JSONObject();
            long time = System.currentTimeMillis();
            String nowDate = "/Date(" + time + ")/";
//            param.put("CreatedByUser", "dlw004"); //loginInfo.getZuid());
            param.put("GoodsMovementCode", "01");
            param.put("DocumentDate", nowDate);
            param.put("PostingDate", nowDate);
            JSONArray jaItem = new JSONArray();
            for (PurchaseOrder purchaseOrder : list) {
                JSONObject objectItem = new JSONObject();
                objectItem.put("Material", purchaseOrder.getMaterial());
                objectItem.put("Plant", purchaseOrder.getPlant());
                objectItem.put("StorageLocation", purchaseOrder.getStorageLocation());
                objectItem.put("Batch", purchaseOrder.getBatch());
                objectItem.put("Supplier", purchaseOrder.getSupplier());
//                objectItem.put("SupplierBatch", "1111");//purchaseOrder.getSupplierBatch());
                objectItem.put("PurchaseOrder", purchaseOrder.getPurchaseOrder());
                objectItem.put("PurchaseOrderItem", purchaseOrder.getPurchaseOrderItem());
                objectItem.put("QuantityInEntryUnit", purchaseOrder.getOrderQuantity());
                objectItem.put("EntryUnit", purchaseOrder.getPurchaseOrderQuantityUnit());
                objectItem.put("GoodsMovementType", "101");
                objectItem.put("IsCompletelyDelivered", true);
                objectItem.put("ShelfLifeExpirationDate", "/Date(1688821378000)/");
                objectItem.put("GoodsMovementRefDocType", "B");
                jaItem.add(objectItem);
            }
            param.put("to_MaterialDocumentItem", jaItem);
            System.out.println("post json" + param.toJSONString());
            String paramJson = param.toString();
            HttpResponse httpResponseItem = httpUtil.callHttp(url, HttpRequestUtil.HTTP_POST_METHOD, paramJson, null);
            if (httpResponseItem != null && (httpResponseItem.getCode() == 200 || httpResponseItem.getCode() == 201)) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                if (jsonD != null) {
                    result.put("materialDocument", jsonD.getString("MaterialDocument"));
                    //开始更新批次的供应商批次信息
                    if (StringUtils.isNotEmpty(jsonD.getString("MaterialDocument"))) {
                        MaterialDocument query = new MaterialDocument();
                        query.setMaterialDocument(jsonD.getString("MaterialDocument"));
                        query.setMaterialDocumentYear(jsonD.getString("MaterialDocumentYear"));
                        List<MaterialDocument> mdList = getMaterialDocumentItemList(query);
                        PurchaseOrder poInfo = list.get(0);
                        String urlSupplier = app.getOdataService().getHost() + app.getString(R.string.sap_url_supplier_update) +
                                "(Material='" + poInfo.getMaterial() + "',BatchIdentifyingPlant='',Batch='" + mdList.get(0).getBatch() + "')";
                        //获取etag
                        Map<String, String> map = httpUtil.getIfMatch(urlSupplier);
                        JSONObject paramUpdate = new JSONObject();
                        JSONObject paramUpdateD = new JSONObject();
                        paramUpdate.put("Supplier", poInfo.getSupplier());
                        paramUpdate.put("BatchBySupplier", poInfo.getSupplierBatch());
                        paramUpdate.put("BatchIsMarkedForDeletion", true);
                        paramUpdate.put("MatlBatchIsInRstrcdUseStock", true);
                        paramUpdateD.put("d", paramUpdate);
                        HttpResponse httpResponseSupplier = httpUtil.callHttp(urlSupplier, HttpRequestUtil.HTTP_PATCH_METHOD, paramUpdateD.toString(), map);
                        if (httpResponseSupplier.getCode() != 204) {
                            result.put("materialDocument", "");
                            result.put("error", app.getString(R.string.text_batch_suppler_error));
                            //回滚操作，取消创建的物料凭证
                            String urlCancel = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_cancel) +
                                    "?MaterialDocumentYear='" + jsonD.getString("MaterialDocumentYear") + "'&MaterialDocument='" + jsonD.getString("MaterialDocument") + "'";
                            HttpResponse httpResponseCancel = httpUtil.callHttp(urlCancel, HttpRequestUtil.HTTP_POST_METHOD, "{}", null);
                            if (httpResponseCancel.getCode() != 200) {
                                LogUtils.e(TAG, "回滚物料凭证失败：" + jsonD.getString("MaterialDocument"));
                            }
                        } else {
                            LogUtils.i(TAG, "采购收货过账凭证:" + jsonD.getString("MaterialDocument"));
                        }
                    }
                }
            } else {
                result.put("materialDocument", "");
                JSONObject jsonResponse = JSONObject.parseObject(httpResponseItem.getResponseString());
                JSONObject jsonError = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("error")));
                JSONObject jsonMessage = JSONObject.parseObject(JSONObject.toJSONString(jsonError.get("message")));
                result.put("error", jsonMessage.getString("value"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("materialDocument", "");
            LogUtils.e(TAG, "function createMaterialDocument failed:" + e);
        }

        return result;
    }

    /**
     * 根据工厂批次获取供应商批次的值
     *
     * @param materialDocument
     * @return
     */

    public String getSupplierBatch(MaterialDocument materialDocument) {
        String supplierBatch = "";
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_supplier_update) +
                    "?$format=json&$filter=Batch eq '" + materialDocument.getBatch() + "'";
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponse != null && httpResponse.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                if (JaResults.size() > 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));
                    supplierBatch = jsonObject.getString("BatchBySupplier");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getSupplierBatch failed:" + e);
        }
        return supplierBatch;
    }
}
