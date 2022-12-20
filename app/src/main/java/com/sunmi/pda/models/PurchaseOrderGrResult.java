package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrderGrResult implements Serializable, Comparable<PurchaseOrderGrResult>{
    private String purchaseOrder;
    private String syncDate;
    private String isReceived;
    private List<PurchaseOrderGr> purchaseOrders;

    public PurchaseOrderGrResult(String purchaseOrder, String syncDate, String isReceived, List<PurchaseOrderGr> purchaseOrders) {
        this.purchaseOrder = purchaseOrder;
        this.syncDate = syncDate;
        this.isReceived = isReceived;
        this.purchaseOrders = purchaseOrders;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public String getSyncDate() {
        return syncDate;
    }

    public String getIsReceived() {
        return isReceived;
    }

    public List<PurchaseOrderGr> getPurchaseOrders() {
        return purchaseOrders;
    }
    @Override
    public int compareTo(PurchaseOrderGrResult o) {
        return this.purchaseOrder.compareTo(o.getPurchaseOrder());
    }
}
