package com.android.pda.models;

import java.io.Serializable;

public class PurchaseOrderQuery implements Serializable {
    String purchaseOrder;
    String createDateFrom;
    String createDateTo;
    String deliveryDateFrom;
    String deliveryDateTo;
    String supplier;
    String year;

    public PurchaseOrderQuery(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public PurchaseOrderQuery(String purchaseOrder, String year) {
        this.purchaseOrder = purchaseOrder;
        this.year = year;
    }

    public PurchaseOrderQuery(String purchaseOrder, String createDateFrom, String createDateTo,
                              String deliveryDateFrom, String deliveryDateTo, String supplier) {
        this.purchaseOrder = purchaseOrder;
        this.createDateFrom = createDateFrom;
        this.createDateTo = createDateTo;
        this.deliveryDateFrom = deliveryDateFrom;
        this.deliveryDateTo = deliveryDateTo;
        this.supplier = supplier;
    }

    public String getYear() {
        return year;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getCreateDateFrom() {
        return createDateFrom;
    }

    public String getCreateDateTo() {
        return createDateTo;
    }

    public String getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public String getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public String getSupplier() {
        return supplier;
    }
}

