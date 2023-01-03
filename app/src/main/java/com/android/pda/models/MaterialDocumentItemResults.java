package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class MaterialDocumentItemResults {
    @JSONField(name="Material")
    private String material;
    @JSONField(name="Plant")
    private String plant;
    @JSONField(name="StorageLocation")
    private String storageLocation;
    @JSONField(name="GoodsMovementRefDocType")
    private String goodsMovementRefDocType;
    @JSONField(name="GoodsMovementType")
    private String goodsMovementType;
    @JSONField(name="QuantityInEntryUnit")
    private String quantityInEntryUnit;
    @JSONField(name="PurchaseOrder")
    private String purchaseOrder;
    @JSONField(name="PurchaseOrderItem")
    private String purchaseOrderItem;
    @JSONField(name="Batch")
    private String batch;

    @JSONField(name="to_SerialNumbers")
    private SerialNumber serialNumber;

    public MaterialDocumentItemResults() {
    }

    public MaterialDocumentItemResults(String material, String plant, String storageLocation,
                                       String goodsMovementRefDocType, String goodsMovementType,
                                       String quantityInEntryUnit, String purchaseOrder,
                                       String purchaseOrderItem, String batch, SerialNumber serialNumber) {
        this.material = material;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.goodsMovementRefDocType = goodsMovementRefDocType;
        this.goodsMovementType = goodsMovementType;
        this.quantityInEntryUnit = quantityInEntryUnit;
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderItem = purchaseOrderItem;
        this.batch = batch;
        this.serialNumber = serialNumber;
    }

    public String getMaterial() {
        return material;
    }

    public String getPlant() {
        return plant;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getGoodsMovementRefDocType() {
        return goodsMovementRefDocType;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public String getQuantityInEntryUnit() {
        return quantityInEntryUnit;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public String getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    public String getBatch() {
        return batch;
    }

    public SerialNumber getSerialNumber() {
        return serialNumber;
    }
}
