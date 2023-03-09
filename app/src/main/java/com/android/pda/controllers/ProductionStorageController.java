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
import com.android.pda.models.ProductionStorageQuery;
import com.android.pda.utils.HttpRequestUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionStorageController {
    protected static final String TAG = ProductionStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();

    public List<MaterialDocument> syncData(ProductionStorageQuery query) throws Exception {
        List<MaterialDocument> materialDocumentList = new ArrayList<>();
        MaterialDocument materialDocumentHeader = getMaterialDocumentHeader(query);
        if (materialDocumentHeader == null || materialDocumentHeader.getMaterialDocument() == null) {
            return materialDocumentList;
        } else {
            materialDocumentList = getMaterialDocumentItemList(materialDocumentHeader);
        }
        return materialDocumentList;
    }

    public static MaterialDocument getMaterialDocumentHeader(ProductionStorageQuery query) {
        MaterialDocument materialDocumentHeader = new MaterialDocument();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        String materialDocumentYear = "";
        String materialDocument = "";
        String api = "?$format=json&$filter=MaterialDocument eq " + "'" + query.getMaterialDocument() + "'";
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_doc_specific) + api;
        LogUtils.e(TAG, "MaterialDocument Request URL --------->" + url);
        try {
            String msgtxt = "";
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            LogUtils.d(TAG, "Response--->" + httpResponse.getResponseString());
            // 解析 http 的返回结果
            String result = "";
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
     * 验证查询参数：物料凭证号
     *
     * @param query
     * @return
     */
    public String verifyQuery(ProductionStorageQuery query) {
        if (query != null) {
            if (StringUtils.isEmpty(query.getMaterialDocument())) {
                return app.getString(R.string.text_input_material_doc_num);
            }
            // TODO: verify the digit
        }
        return null;
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
                for (Object object : JaResults) {          // 使用 foreach 循环遍历 ArrayList 对象
                    JSONObject objectMaterialDocument = JSONObject.parseObject(JSONObject.toJSONString(object));
                    MaterialDocument materialDocument = new MaterialDocument();
                    materialDocument.setMaterial(objectMaterialDocument.getString("Material"));
                    materialDocument.setMaterialDocument(objectMaterialDocument.getString("MaterialDocument"));
                    materialDocument.setMaterialDocumentItem(objectMaterialDocument.getString("MaterialDocumentItem"));
                    materialDocument.setBatch(objectMaterialDocument.getString("Batch"));
                    if (StringUtils.isNotEmpty(objectMaterialDocument.getString("PurchaseOrder"))) {
                        PurchaseOrder purchaseOrder = new PurchaseOrder();
                        purchaseOrder.setPurchaseOrder(objectMaterialDocument.getString("PurchaseOrder"));
                        List<PurchaseOrder> purchaseOrderList = getPOItemList(purchaseOrder);
                        if (purchaseOrderList.size() > 0) {
                            materialDocument.setDescription(purchaseOrderList.get(0).getDescription());
                        }
                    } else {
                        materialDocument.setDescription("");
                    }
                    materialDocument.setPlant(objectMaterialDocument.getString("Plant"));
                    materialDocument.setStorageLocation(objectMaterialDocument.getString("StorageLocation"));
                    materialDocument.setGoodsMovementType(objectMaterialDocument.getString("GoodsMovementType"));
                    materialDocument.setPurchaseOrder(objectMaterialDocument.getString("PurchaseOrder"));
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
     * 生产入库创建物料凭证
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
            long time = System.currentTimeMillis();
            String nowDate = "/Date(" + time + ")/";
//            param.put("CreatedByUser", "dlw004"); //loginInfo.getZuid());
            param.put("GoodsMovementCode", "04");
            param.put("DocumentDate", nowDate);
            param.put("PostingDate", nowDate);
            JSONArray jaItem = new JSONArray();
            for (MaterialDocument materialDocument : list) {
                JSONObject objectItem = new JSONObject();
                objectItem.put("Material", materialDocument.getMaterial());
                objectItem.put("Plant", materialDocument.getPlant());
                objectItem.put("StorageLocation", materialDocument.getStorageLocation());
                objectItem.put("IssuingOrReceivingStorageLoc", materialDocument.getTargetStorageLocation());
                objectItem.put("Batch", materialDocument.getBatch());
//                objectItem.put("Supplier", purchaseOrder.getSupplier());
//                objectItem.put("SupplierBatch", "1111");//purchaseOrder.getSupplierBatch());
                objectItem.put("PurchaseOrder", materialDocument.getPurchaseOrder());
                objectItem.put("PurchaseOrderItem", materialDocument.getPurchaseOrderItem());
                objectItem.put("QuantityInEntryUnit", materialDocument.getQuantityInEntryUnit());
                objectItem.put("EntryUnit", materialDocument.getEntryUnit());
                objectItem.put("GoodsMovementType", "311");
                objectItem.put("IsCompletelyDelivered", true);
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
                        Map<String, String> updateResult = updateBatchCharcValue(list);
                        if (updateResult.get("error") != null) {
                            //更新失败取消创建的物料凭证
                            String urlCancel = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_cancel) +
                                    "?MaterialDocumentYear='" + jsonD.getString("MaterialDocumentYear") + "'&MaterialDocument='" + jsonD.getString("MaterialDocument") + "'";
                            HttpResponse httpResponseCancel = httpUtil.callHttp(urlCancel, HttpRequestUtil.HTTP_POST_METHOD, "{}", null);
                            if (httpResponseCancel.getCode() != 200) {
                                LogUtils.e(TAG, "回滚物料凭证失败：" + jsonD.getString("MaterialDocument"));
                            }
                        } else {
                            result.put("materialDocument", jsonD.getString("MaterialDocument"));
                            result.put("error", "");
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
            result.put("error", app.getString(R.string.text_url_get_error));
            LogUtils.e(TAG, "function createMaterialDocument failed:" + e);
        }

        return result;
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
                if (JaResults.size() > 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));
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
                } else {
                    param.put("Material", materialDocument.getMaterial());
                    param.put("Batch", materialDocument.getBatch());
                    param.put("CharcInternalID", "4");
                    param.put("CharcValuePositionNumber", "2");
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
}