package com.android.pda.models;

import java.io.Serializable;

public class POStorageQuery implements Serializable {
    String MaterialDocument;

    String poNumber;

    public POStorageQuery(String materialDocument, String poNumber) {
        MaterialDocument = materialDocument;
        this.poNumber = poNumber;
    }

    public String getMaterialDocument() {
        return MaterialDocument;
    }

    public void setMaterialDocument(String materialDocument) {
        MaterialDocument = materialDocument;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }
}
