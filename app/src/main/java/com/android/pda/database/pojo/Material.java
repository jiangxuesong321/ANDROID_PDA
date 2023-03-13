package com.android.pda.database.pojo;

import java.io.Serializable;

public class Material implements Serializable {
    private String material;
    private String materialName;
    private String unit;
    private String batchFlag;
    private String serialFlag;
    private String barCode;
    private long creationDate;
    private long lastChangeDate;

    public Material(String material, String materialName, String unit, String batchFlag, String serialFlag, String barCode, long creationDate, long lastChangeDate) {
        this.material = material;
        this.materialName = materialName;
        this.unit = unit;
        this.batchFlag = batchFlag;
        this.serialFlag = serialFlag;
        this.barCode = barCode;
        this.creationDate = creationDate;
        this.lastChangeDate = lastChangeDate;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setBatchFlag(String batchFlag) {
        this.batchFlag = batchFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastChangeDate(long lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public String getMaterial() {
        return material;
    }

    public String getMaterialName() {
        return materialName;
    }

    public String getUnit() {
        return unit;
    }

    public String getBatchFlag() {
        return batchFlag;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public String getBarCode() {
        return barCode;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getLastChangeDate() {
        return lastChangeDate;
    }
}
