package com.android.pda.database.pojo;

public class Material {
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
