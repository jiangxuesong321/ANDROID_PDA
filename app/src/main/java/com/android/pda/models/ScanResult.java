package com.android.pda.models;

import java.io.Serializable;

public class ScanResult implements Serializable {
    private String storageLocation;
    private String materialNumber;
    private String batch;
    private Reason reason;

    public ScanResult() {
    }

    public ScanResult(String storageLocation, String materialNumber, String batch, Reason reason) {
        this.storageLocation = storageLocation;
        this.materialNumber = materialNumber;
        this.batch = batch;
        this.reason = reason;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public String getBatch() {
        return batch;
    }


    public Reason getReason() {
        return reason;
    }
}
