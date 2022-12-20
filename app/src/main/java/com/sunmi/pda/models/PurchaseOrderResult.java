package com.sunmi.pda.models;

import com.sunmi.pda.database.pojo.PurchaseOrder;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrderResult implements Serializable, Comparable<PurchaseOrderResult>{
    private String purchaseOrder;
    private String syncDate;
    private String isReceived;
    private List<PurchaseOrder> purchaseOrders;

    public PurchaseOrderResult(String purchaseOrder, String syncDate, String isReceived, List<PurchaseOrder> purchaseOrders) {
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

    public List<com.sunmi.pda.database.pojo.PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }
    @Override
    public int compareTo(PurchaseOrderResult o) {
        return this.purchaseOrder.compareTo(o.getPurchaseOrder());
    }
}
