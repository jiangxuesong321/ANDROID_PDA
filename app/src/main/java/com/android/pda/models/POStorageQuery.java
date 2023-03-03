package com.android.pda.models;

import java.io.Serializable;

public class POStorageQuery implements Serializable {
    String MaterialDocument;

    public POStorageQuery(String materialDocument) {
        MaterialDocument = materialDocument;
    }

    public String getMaterialDocument() {
        return MaterialDocument;
    }

    public void setMaterialDocument(String materialDocument) {
        MaterialDocument = materialDocument;
    }
}
