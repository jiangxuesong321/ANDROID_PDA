package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class SerialNumberResults {
    @JSONField(name="SerialNumber")
    private String serialNumber;
    @JSONField(name="Material")
    private String material;
    @JSONField(name="Plant")
    private String plant;


    public SerialNumberResults() {
    }

    public SerialNumberResults(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public SerialNumberResults(String serialNumber, String material, String plant) {
        this.serialNumber = serialNumber;
        this.material = material;
        this.plant = plant;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }
}
