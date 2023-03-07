package com.android.pda.database.pojo;

import java.io.Serializable;

public class MaterialDocument implements Serializable {

    String materialDocument;

    String materialDocumentYear;

    String plant;

    String storageLocation;

    String materialDocumentItem;

    String material;

    String description;

    String batch;

    String goodsMovementType;

    String supplier;

    String purchaseOrder;

    String PurchaseOrderItem;

    String quantityInEntryUnit;

    String entryUnit;

    String storageBin;

    String inventoryStockType;

    public MaterialDocument(String materialDocument, String materialDocumentYear, String plant, String storageLocation,
                            String materialDocumentItem, String material, String description, String batch,
                            String goodsMovementType, String supplier, String purchaseOrder, String quantityInEntryUnit,
                            String entryUnit, String storageBin, String PurchaseOrderItem) {
        this.materialDocument = materialDocument;
        this.materialDocumentYear = materialDocumentYear;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.materialDocumentItem = materialDocumentItem;
        this.material = material;
        this.description = description;
        this.batch = batch;
        this.goodsMovementType = goodsMovementType;
        this.supplier = supplier;
        this.purchaseOrder = purchaseOrder;
        this.quantityInEntryUnit = quantityInEntryUnit;
        this.entryUnit = entryUnit;
        this.storageBin = storageBin;
        this.PurchaseOrderItem = PurchaseOrderItem;
        this.inventoryStockType = inventoryStockType;
    }

    public String getInventoryStockType() {
        return inventoryStockType;
    }

    public void setInventoryStockType(String inventoryStockType) {
        this.inventoryStockType = inventoryStockType;
    }

    public MaterialDocument() {

    }

    public String getPurchaseOrderItem() {
        return PurchaseOrderItem;
    }

    public void setPurchaseOrderItem(String purchaseOrderItem) {
        PurchaseOrderItem = purchaseOrderItem;
    }

    public String getMaterialDocument() {
        return materialDocument;
    }

    public void setMaterialDocument(String materialDocument) {
        this.materialDocument = materialDocument;
    }

    public String getMaterialDocumentYear() {
        return materialDocumentYear;
    }

    public void setMaterialDocumentYear(String materialDocumentYear) {
        this.materialDocumentYear = materialDocumentYear;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getMaterialDocumentItem() {
        return materialDocumentItem;
    }

    public void setMaterialDocumentItem(String materialDocumentItem) {
        this.materialDocumentItem = materialDocumentItem;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public void setGoodsMovementType(String goodsMovementType) {
        this.goodsMovementType = goodsMovementType;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getQuantityInEntryUnit() {
        return quantityInEntryUnit;
    }

    public void setQuantityInEntryUnit(String quantityInEntryUnit) {
        this.quantityInEntryUnit = quantityInEntryUnit;
    }

    public String getEntryUnit() {
        return entryUnit;
    }

    public void setEntryUnit(String entryUnit) {
        this.entryUnit = entryUnit;
    }

    public String getStorageBin() {
        return storageBin;
    }

    public void setStorageBin(String storageBin) {
        this.storageBin = storageBin;
    }
}
