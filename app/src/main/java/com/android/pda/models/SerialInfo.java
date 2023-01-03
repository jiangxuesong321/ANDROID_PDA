package com.android.pda.models;

import java.io.Serializable;

public class SerialInfo implements Serializable {
    private String plant;
    private String material;
    private String materialdes;
    private String batch;
    private String serialnumber;
    private String storageLocation;

    private String plantdes;
    private String storagelocdes;
    private String quantityinentryunit;
    private String serialflag;
    private String count;

    public SerialInfo() {
    }

    public SerialInfo(String plant, String material, String materialdes, String batch, String serialnumber, String storageLocation) {
        this.plant = plant;
        this.material = material;
        this.materialdes = materialdes;
        this.batch = batch;
        this.serialnumber = serialnumber;
        this.storageLocation = storageLocation;
    }

    public SerialInfo(String plant, String material, String materialdes, String batch, String serialnumber,
                      String storageLocation, String plantdes, String storagelocdes, String quantityinentryunit,
                      String serialflag) {
        this.plant = plant;
        this.material = material;
        this.materialdes = materialdes;
        this.batch = batch;
        this.serialnumber = serialnumber;
        this.storageLocation = storageLocation;
        this.plantdes = plantdes;
        this.storagelocdes = storagelocdes;
        this.quantityinentryunit = quantityinentryunit;
        this.serialflag = serialflag;
    }

    public SerialInfo(String serialnumber) {
        this.plant = "";
        this.material = "";
        this.materialdes = "";
        this.batch = "";
        this.serialnumber = serialnumber;
        this.storageLocation = "";
        this.plantdes = "";
        this.storagelocdes = "";
        this.quantityinentryunit = "";
        this.serialflag = "";
    }

    public SerialInfo(String material, String mDesc, String serialnumber) {
        this.material = material;
        this.materialdes = mDesc;
        this.serialnumber = serialnumber;
        this.batch = "";
    }

    public String getPlant() {
        return plant;
    }

    public String getMaterial() {
        return material;
    }

    public String getMaterialDesc() {
        return materialdes;
    }

    public String getBatch() {
        return batch;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setMaterialDesc(String materialdes) {
        this.materialdes = materialdes;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getGroupFiled(){
        return material + "-" + batch;
    }

    public String getMaterialdes() {
        return materialdes;
    }

    public String getPlantdes() {
        return plantdes;
    }

    public String getStoragelocdes() {
        return storagelocdes;
    }

    public String getQuantityinentryunit() {
        return quantityinentryunit;
    }

    public String getSerialflag() {
        return serialflag;
    }

    public void setMaterialdes(String materialdes) {
        this.materialdes = materialdes;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public void setPlantdes(String plantdes) {
        this.plantdes = plantdes;
    }

    public void setStoragelocdes(String storagelocdes) {
        this.storagelocdes = storagelocdes;
    }

    public void setQuantityinentryunit(String quantityinentryunit) {
        this.quantityinentryunit = quantityinentryunit;
    }

    public void setSerialflag(String serialflag) {
        this.serialflag = serialflag;
    }
}
