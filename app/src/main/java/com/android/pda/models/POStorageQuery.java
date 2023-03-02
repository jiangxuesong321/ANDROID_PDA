package com.android.pda.models;

import java.io.Serializable;

public class POStorageQuery implements Serializable {
    String purchaseOrderNumber;

    public POStorageQuery(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }
}
