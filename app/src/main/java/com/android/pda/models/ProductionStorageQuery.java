package com.android.pda.models;

import java.io.Serializable;

public class ProductionStorageQuery implements Serializable {
    String MaterialDocument;

    public ProductionStorageQuery(String materialDocument) {
        MaterialDocument = materialDocument;
    }

    public String getMaterialDocument() {
        return MaterialDocument;
    }

    public void setMaterialDocument(String materialDocument) {
        MaterialDocument = materialDocument;
    }
}
