package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.MaterialDocument;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.POStorageQuery;
import com.android.pda.utils.HttpRequestUtil;

import java.util.ArrayList;
import java.util.List;

public class POStorageController {
    protected static final String TAG = POStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();

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
}
