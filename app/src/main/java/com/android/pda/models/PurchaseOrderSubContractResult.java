package com.android.pda.models;

import java.io.Serializable;
import java.util.List;

public class PurchaseOrderSubContractResult implements Serializable, Comparable<PurchaseOrderSubContractResult>{
    private String purchaseOrder;
    private String syncDate;
    private String isReceived;
    private List<PurchaseOrderSubContract> purchaseOrders;

    public PurchaseOrderSubContractResult(String purchaseOrder, String syncDate,
                                          String isReceived, List<PurchaseOrderSubContract> purchaseOrders) {
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

    public List<PurchaseOrderSubContract> getPurchaseOrders() {
        return purchaseOrders;
    }
    @Override
    public int compareTo(PurchaseOrderSubContractResult o) {
        return this.purchaseOrder.compareTo(o.getPurchaseOrder());
    }
}
