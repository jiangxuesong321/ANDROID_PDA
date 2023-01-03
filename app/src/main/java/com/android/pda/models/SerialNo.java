package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class SerialNo implements Serializable {
    @JSONField(name="SERIALNUMBER")
    private String serialNumber;

    @JSONField(name="MATERIALDOCUMENTITEM")
    private String materialDocumentItem;

    public SerialNo() {
    }

    public SerialNo(String serialNumber, String materialDocumentItem) {
        this.serialNumber = serialNumber;
        this.materialDocumentItem = materialDocumentItem;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMaterialDocumentItem() {
        return materialDocumentItem;
    }

    public void setMaterialDocumentItem(String materialDocumentItem) {
        this.materialDocumentItem = materialDocumentItem;
    }
}
