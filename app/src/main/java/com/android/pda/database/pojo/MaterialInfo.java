package com.android.pda.database.pojo;

import java.io.Serializable;

public class MaterialInfo implements Serializable {

    String material;
    String materialName;
    String unit;
    String batchFlag;
    String serialFlag;
    String barCode;
    long creationDate;
    long lastChangeDate;
    String plant;
    String oriStorageLocation;
    String targetStorageLocation;
    String storageLocation;
    String batch;
    String matlWrhsStkQtyInMatlBaseUnit;
    String materialBaseUnit;
    String inventoryStockType;


    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(String batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(long lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getOriStorageLocation() {
        return oriStorageLocation;
    }

    public void setOriStorageLocation(String oriStorageLocation) {
        this.oriStorageLocation = oriStorageLocation;
    }

    public String getTargetStorageLocation() {
        return targetStorageLocation;
    }

    public void setTargetStorageLocation(String targetStorageLocation) {
        this.targetStorageLocation = targetStorageLocation;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getMatlWrhsStkQtyInMatlBaseUnit() {
        return matlWrhsStkQtyInMatlBaseUnit;
    }

    public void setMatlWrhsStkQtyInMatlBaseUnit(String matlWrhsStkQtyInMatlBaseUnit) {
        this.matlWrhsStkQtyInMatlBaseUnit = matlWrhsStkQtyInMatlBaseUnit;
    }

    public String getMaterialBaseUnit() {
        return materialBaseUnit;
    }

    public void setMaterialBaseUnit(String materialBaseUnit) {
        this.materialBaseUnit = materialBaseUnit;
    }

    public String getInventoryStockType() {
        return inventoryStockType;
    }

    public void setInventoryStockType(String inventoryStockType) {
        this.inventoryStockType = inventoryStockType;
    }
}
