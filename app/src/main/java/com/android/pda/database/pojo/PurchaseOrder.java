package com.android.pda.database.pojo;

import java.io.Serializable;

public class PurchaseOrder implements Serializable {

    String purchaseOrder;

    String purchaseOrderItem;

    String plant;

    String storageLocation;

    String batch;

    String material;

    String orderQuantity;

    String purchaseOrderQuantityUnit;

    String description;

    String supplierMaterialNumber;

    String companyCode;

    String supplier;


    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }



    public String getSupplierMaterialNumber() {
        return supplierMaterialNumber;
    }

    public void setSupplierMaterialNumber(String supplierMaterialNumber) {
        this.supplierMaterialNumber = supplierMaterialNumber;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    public void setPurchaseOrderItem(String purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getPurchaseOrderQuantityUnit() {
        return purchaseOrderQuantityUnit;
    }

    public void setPurchaseOrderQuantityUnit(String purchaseOrderQuantityUnit) {
        this.purchaseOrderQuantityUnit = purchaseOrderQuantityUnit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
