package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PurchaseOrderReturnPostingRequest {
    @JSONField(name="POSTINGDATE")
    private String postingDate;
    @JSONField(name="MATERIALDOCUMENT")
    private String materialDocument;
    @JSONField(name="DOCUMENTDATE")
    private String documentDate;
    @JSONField(name="GoodsMovementType")
    private String goodsMovementType;

    @JSONField(name="MATERIALDOCUMENTITEM")
    private String materialDocumentItem;

    @JSONField(name="Material")
    private String material;

    @JSONField(name="Plant")
    private String plant;

    @JSONField(name="StorageLocation")
    private String storageLocation;

    @JSONField(name="QuantityInEntryUnit")
    private String quantityInEntryUnit;

    @JSONField(name="Batch")
    private String batch;

    @JSONField(name="REASON")
    private String reason;

    @JSONField(name="DOCUMENTTYPE")
    private String documentType;

    @JSONField(name="SerialNumber")
    List<SerialNumberPoReturn> serialNumberList;

    public PurchaseOrderReturnPostingRequest() {
    }

    public PurchaseOrderReturnPostingRequest(String postingDate, String materialDocument, String documentDate,
                                             String goodsMovementType, String materialDocumentItem,
                                             String material, String plant, String storageLocation,
                                             String quantityInEntryUnit, String batch, String reason, String documentType,
                                             List<SerialNumberPoReturn> serialNumberList) {
        this.postingDate = postingDate;
        this.materialDocument = materialDocument;
        this.documentDate = documentDate;
        this.goodsMovementType = goodsMovementType;
        this.materialDocumentItem = materialDocumentItem;
        this.material = material;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.quantityInEntryUnit = quantityInEntryUnit;
        this.batch = batch;
        this.reason = reason;
        this.serialNumberList = serialNumberList;
        this.documentType = documentType;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getMaterialDocument() {
        return materialDocument;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public String getMaterialDocumentItem() {
        return materialDocumentItem;
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

    public String getQuantityInEntryUnit() {
        return quantityInEntryUnit;
    }

    public String getBatch() {
        return batch;
    }

    public String getReason() {
        return reason;
    }

    public List<SerialNumberPoReturn> getSerialNumberList() {
        return serialNumberList;
    }

    public String getDocumentType() {
        return documentType;
    }
}
