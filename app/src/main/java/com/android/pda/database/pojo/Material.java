package com.android.pda.database.pojo;

import java.io.Serializable;

public class Material implements Serializable {
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
    String CharcValue;
    String storageBin;
    String confirmStorageBin;
    String inputMaterial;

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

    public Material() {

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

    public String getCharcValue() {
        return CharcValue;
    }

    public void setCharcValue(String charcValue) {
        CharcValue = charcValue;
    }

    public String getConfirmStorageBin() {
        return confirmStorageBin;
    }

    public void setConfirmStorageBin(String confirmStorageBin) {
        this.confirmStorageBin = confirmStorageBin;
    }

    public String getInputMaterial() {
        return inputMaterial;
    }

    public void setInputMaterial(String inputMaterial) {
        this.inputMaterial = inputMaterial;
    }

    public String getStorageBin() {
        return storageBin;
    }

    public void setStorageBin(String storageBin) {
        this.storageBin = storageBin;
    }
}
