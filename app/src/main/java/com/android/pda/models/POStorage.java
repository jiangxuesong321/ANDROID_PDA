package com.android.pda.models;

import java.io.Serializable;

public class POStorage implements Serializable {

    private String poNumber;
    private String lineNumber;
    private String material;
    private String materialDescription;
    private String batchNumber;
    private String quantity;
    private String storageBin;

    public POStorage(String poNumber, String lineNumber, String material, String materialDescription, String batchNumber, String quantity, String storageBin) {
        this.poNumber = poNumber;
        this.lineNumber = lineNumber;
        this.material = material;
        this.materialDescription = materialDescription;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.storageBin = storageBin;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getStorageBin() {
        return storageBin;
    }

    public void setStorageBin(String storageBin) {
        this.storageBin = storageBin;
    }
}
