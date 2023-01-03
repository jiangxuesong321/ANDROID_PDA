package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PurchaseOrderSubContractInPostingRequest {
    @JSONField(name="GoodsMovementCode")
    private String goodsMovementCode;

    @JSONField(name="PostingDate")
    private String postingDate;

    @JSONField(name="DocumentDate")
    private String documentDate;

    @JSONField(name="GoodsMovementType")
    private String goodsMovementType;

    @JSONField(name="GoodsMovementRefDocType")
    private String goodsMovementRefDocType;

    @JSONField(name="Material")
    private String material;

    @JSONField(name="Plant")
    private String plant;

    @JSONField(name="StorageLocation")
    private String storageLocation;

    @JSONField(name="ManufactureDate")
    private String manufactureDate;

    @JSONField(name="QuantityInEntryUnit")
    private String quantityInEntryUnit;

    @JSONField(name="PurchaseOrder")
    private String purchaseOrder;

    @JSONField(name="PurchaseOrderItem")
    private String purchaseOrderItem;

    @JSONField(name="MaterialDocumentHeaderText")
    private String materialDocumentHeaderText;

    @JSONField(name="MaterialDocumentItemText")
    private String materialDocumentItemText;

    @JSONField(name="LogisticsProvider")
    private String logisticsProvider;

    @JSONField(name="LogisticsNumber")
    private String logisticsNumber;

    @JSONField(name="DocumentType")
    private String documentType;

    @JSONField(name="SERIALNO")
    private List<SerialNumberPoReturn> serialList;

    @JSONField(name="MaterialDocumentItem")
    private List<ComponentItem> items;



    public PurchaseOrderSubContractInPostingRequest() {
    }

    public PurchaseOrderSubContractInPostingRequest(String goodsMovementCode, String postingDate,
                                                    String documentDate, String goodsMovementType,
                                                    String goodsMovementRefDocType, String material,
                                                    String plant, String storageLocation, String manufactureDate,
                                                    String quantityInEntryUnit, String purchaseOrder,
                                                    String purchaseOrderItem, String materialDocumentHeaderText,
                                                    String materialDocumentItemText, String logisticsProvider,
                                                    String logisticsNumber, String documentType,
                                                    List<SerialNumberPoReturn> serialList, List<ComponentItem> items) {
        this.goodsMovementCode = goodsMovementCode;
        this.postingDate = postingDate;
        this.documentDate = documentDate;
        this.goodsMovementType = goodsMovementType;
        this.goodsMovementRefDocType = goodsMovementRefDocType;
        this.material = material;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.manufactureDate = manufactureDate;
        this.quantityInEntryUnit = quantityInEntryUnit;
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderItem = purchaseOrderItem;
        this.materialDocumentHeaderText = materialDocumentHeaderText;
        this.materialDocumentItemText = materialDocumentItemText;
        this.logisticsProvider = logisticsProvider;
        this.logisticsNumber = logisticsNumber;
        this.documentType = documentType;
        this.serialList = serialList;
        this.items = items;
    }

    public String getGoodsMovementCode() {
        return goodsMovementCode;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public String getGoodsMovementRefDocType() {
        return goodsMovementRefDocType;
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

    public String getManufactureDate() {
        return manufactureDate;
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

    public String getMaterialDocumentHeaderText() {
        return materialDocumentHeaderText;
    }

    public String getMaterialDocumentItemText() {
        return materialDocumentItemText;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public List<SerialNumberPoReturn> getSerialList() {
        return serialList;
    }

    public List<ComponentItem> getItems() {
        return items;
    }
}
