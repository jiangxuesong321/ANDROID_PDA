package com.android.pda.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.pda.R;
import com.android.pda.application.AndroidApplication;
import com.android.pda.database.pojo.Login;
import com.android.pda.database.pojo.Material;
import com.android.pda.database.pojo.MaterialInfo;
import com.android.pda.log.LogUtils;
import com.android.pda.models.HttpResponse;
import com.android.pda.models.MaterialPickingQuery;
import com.android.pda.utils.HttpRequestUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialPickingController {
    protected static final String TAG = ProductionStorageController.class.getSimpleName();
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();

    /**
     * 根据物料编码库存信息
     *
     * @param query
     * @return
     */
    public List<MaterialInfo> syncData(MaterialPickingQuery query) throws Exception {
        List<MaterialInfo> results = new ArrayList<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            List<Material> materialList = query.getMaterialList();
            for (int j = 0; j < materialList.size(); j++) {
                String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_get_material_stock) +
                        "?$format=json&$filter=Material eq '" + materialList.get(j).getMaterial() + "' and Plant eq '" + materialList.get(j).getPlant() + "'"
                        + " and StorageLocation eq '" + materialList.get(j).getOriStorageLocation() + "'";
                HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
                if (httpResponse != null && httpResponse.getCode() == 200) {
                    JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                    JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                    JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                    if (JaResults.size() > 0) {
                        for (int i = 0; i < JaResults.size(); i++) {
                            MaterialInfo info = new MaterialInfo();
                            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(i)));
                            System.out.println(jsonObject);
                            info.setMaterial(materialList.get(j).getMaterial());
                            info.setMaterialName(materialList.get(j).getMaterialName());
                            info.setPlant(materialList.get(j).getPlant());
                            info.setOriStorageLocation(materialList.get(j).getOriStorageLocation());
                            info.setTargetStorageLocation(materialList.get(j).getTargetStorageLocation());
                            info.setBatch(jsonObject.getString("Batch"));
                            info.setInventoryStockType(jsonObject.getString("InventoryStockType"));
                            info.setMaterialBaseUnit(jsonObject.getString("MaterialBaseUnit"));
                            info.setMatlWrhsStkQtyInMatlBaseUnit(jsonObject.getString("MatlWrhsStkQtyInMatlBaseUnit"));
                            float quantity = Float.parseFloat(info.getMatlWrhsStkQtyInMatlBaseUnit());
                            if (quantity > 0) {
                                results.add(info);
                            }
                        }
                    } else {
                        MaterialInfo info = new MaterialInfo();
                        info.setMaterial(materialList.get(j).getMaterial());
                        info.setMaterialName(materialList.get(j).getMaterialName());
                        info.setPlant(materialList.get(j).getPlant());
                        info.setOriStorageLocation(materialList.get(j).getOriStorageLocation());
                        info.setTargetStorageLocation(materialList.get(j).getTargetStorageLocation());
                        results.add(info);
                    }
                } else {
                    MaterialInfo info = new MaterialInfo();
                    info.setMaterial(materialList.get(j).getMaterial());
                    info.setMaterialName(materialList.get(j).getMaterialName());
                    info.setPlant(materialList.get(j).getPlant());
                    info.setOriStorageLocation(materialList.get(j).getOriStorageLocation());
                    info.setTargetStorageLocation(materialList.get(j).getTargetStorageLocation());
                    results.add(info);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getPOHeader failed:" + e);
        }
        return results;
    }

    /**
     * 根据物料编码获取物料描述信息
     *
     * @param material
     * @return
     */
    public Material getMaterialDescription(String material) {
        Material materialInfo = new Material();
        String description = "";
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        materialInfo.setMaterial(material);
        String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_get_material_description) + "?$format=json&$filter=Product eq '" + material + "'";
        try {
            HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
            if (httpResponse != null && httpResponse.getCode() == 200) {
                JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                if (JaResults.size() > 0) {
                    JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(0)));
                    description = jsonObject.getString("ProductDescription");
                    materialInfo.setMaterialName(description);
                } else {
                    materialInfo.setMaterialName(description);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getPOHeader failed:" + e);
        }

        return materialInfo;
    }

    /**
     * 根据物料编码获取获取信息
     *
     * @param materialList
     * @return
     */
    public List<Material> getMaterialStorageBin(List<Material> materialList) {
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            for (int j = 0; j < materialList.size(); j++) {
                String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_batch_char_value_get) +
                        "?$format=json&$filter=Batch eq '" + materialList.get(j).getBatch() + "'";
                HttpResponse httpResponse = httpUtil.callHttp(url, HttpRequestUtil.HTTP_GET_METHOD, null, null);
                if (httpResponse != null && httpResponse.getCode() == 200) {
                    JSONObject jsonResponse = JSONObject.parseObject(httpResponse.getResponseString());
                    JSONObject jsonD = JSONObject.parseObject(JSONObject.toJSONString(jsonResponse.get("d")));
                    JSONArray JaResults = JSONObject.parseArray(JSONObject.toJSONString(jsonD.get("results")));
                    if (JaResults.size() > 0) {
                        Login loginInfo = loginController.getLoginUser();
                        String city = loginInfo.getZcity();
                        String CharcInternalID = "";
                        if (city != null && city.equals(app.getString(R.string.text_login_user_city_beijing))) {
                            CharcInternalID = app.getString(R.string.text_storage_bin_city_beijing);
                        } else if (city != null && city.equals(app.getString(R.string.text_login_user_city_suzhou))) {
                            CharcInternalID = app.getString(R.string.text_storage_bin_city_suzhou);
                        }
                        for (int i = 0; i < JaResults.size(); i++) {
                            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(JaResults.get(i)));
                            if (jsonObject != null && jsonObject.getString("CharcInternalID").equals(CharcInternalID)) {
                                materialList.get(j).setStorageBin(jsonObject.getString("CharcValue"));
                            }
                        }
                    } else {
                        materialList.get(j).setStorageBin("");
                    }
                } else {
                    materialList.get(j).setStorageBin("");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "function getMaterialStorageBin failed:" + e);
        }
        return materialList;
    }

    /**
     * 物料领用创建物料凭证
     *
     * @param list
     * @return
     */
    public Map<String, String> createMaterialDocument(List<Material> list) {
        Map<String, String> result = new HashMap<>();
        HttpRequestUtil httpUtil = new HttpRequestUtil();
        try {
            String url = app.getOdataService().getHost() + app.getString(R.string.sap_url_material_create);
            Login loginInfo = loginController.getLoginUser();
            JSONObject param = new JSONObject();
            long time = System.currentTimeMillis();
            String nowDate = "/Date(" + time + ")/";
            param.put("GoodsMovementCode", "04");
            param.put("DocumentDate", nowDate);
            param.put("PostingDate", nowDate);
            JSONArray jaItem = new JSONArray();
            for (Material material : list) {
                JSONObject objectItem = new JSONObject();
                objectItem.put("Material", material.getMaterial());
                objectItem.put("Plant", material.getPlant());
                objectItem.put("StorageLocation", material.getOriStorageLocation());
                objectItem.put("Batch", material.getBatch());
                objectItem.put("QuantityInEntryUnit", "1");//materialDocument.getQuantityInEntryUnit());
                objectItem.put("EntryUnit", material.getMaterialBaseUnit());
                objectItem.put("GoodsMovementType", "311");
                objectItem.put("IsCompletelyDelivered", true);
                objectItem.put("IssgOrRcvgBatch", material.getBatch());
                objectItem.put("IssgOrRcvgMaterial", material.getMaterial());
                objectItem.put("IssuingOrReceivingPlant", material.getPlant());
                objectItem.put("IssuingOrReceivingStorageLoc", material.getTargetStorageLocation());
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
}