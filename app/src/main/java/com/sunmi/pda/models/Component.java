package com.sunmi.pda.models;

import java.io.Serializable;

public class Component implements Serializable {
    private String PurchaseOrder;
    private String PurchaseOrderItem;
    private String materialNo;
    private String materialDesc;
    private double totalQuantity;
    private double openQuantity;
    private double quantity;
    private String batch;
    private String componentNo;

    private String componentDesc;
    public Component() {
    }

    public Component(String purchaseOrder, String purchaseOrderItem, String materialNo,
                     String materialDesc, double totalQuantity, double quantity, String batch,
                     String componentNo, String componentDesc, double openQuantity) {
        PurchaseOrder = purchaseOrder;
        PurchaseOrderItem = purchaseOrderItem;
        this.materialNo = materialNo;
        this.materialDesc = materialDesc;
        this.totalQuantity = totalQuantity;
        this.quantity = quantity;
        this.batch = batch;
        this.componentNo = componentNo;
        this.componentDesc = componentDesc;
        this.openQuantity = openQuantity;
    }

    public String getPurchaseOrder() {
        return PurchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        PurchaseOrder = purchaseOrder;
    }

    public String getPurchaseOrderItem() {
        return PurchaseOrderItem;
    }

    public void setPurchaseOrderItem(String purchaseOrderItem) {
        PurchaseOrderItem = purchaseOrderItem;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getComponentNo() {
        return componentNo;
    }

    public void setComponentNo(String componentNo) {
        this.componentNo = componentNo;
    }

    public String getComponentDesc() {
        return componentDesc;
    }

    public double getOpenQuantity() {
        return openQuantity;
    }

    public void setOpenQuantity(double openQuantity) {
        this.openQuantity = openQuantity;
    }
}
