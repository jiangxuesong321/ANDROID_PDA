package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrderGiResult implements Serializable, Comparable<PurchaseOrderGiResult>{
    private String purchaseOrder;
    private String syncDate;
    private String isReceived;
    private List<PurchaseOrderGi> purchaseOrders;

    public PurchaseOrderGiResult(String purchaseOrder, String syncDate, String isReceived, List<PurchaseOrderGi> purchaseOrders) {
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

    public List<PurchaseOrderGi> getPurchaseOrders() {
        return purchaseOrders;
    }
    @Override
    public int compareTo(PurchaseOrderGiResult o) {
        return this.purchaseOrder.compareTo(o.getPurchaseOrder());
    }
}
