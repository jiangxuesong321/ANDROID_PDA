package com.android.pda.database.pojo;

public class BatchStock {
    private String id;
    private String plant;
    private String material;
    private String batch;
    private String storageLocation;
    private String language;
    private String materialText;
    private String plantName;
    private String storageLocationName;
    private String unit;
    private double quantity;

    public BatchStock(String id, String plant, String material, String batch, String storageLocation, String language, String materialText, String plantName, String storageLocationName, String unit, double quantity) {
        this.id = id;
        this.plant = plant;
        this.material = material;
        this.batch = batch;
        this.storageLocation = storageLocation;
        this.language = language;
        this.materialText = materialText;
        this.plantName = plantName;
        this.storageLocationName = storageLocationName;
        this.unit = unit;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getPlant() {
        return plant;
    }

    public String getMaterial() {
        return material;
    }

    public String getBatch() {
        return batch;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getLanguage() {
        return language;
    }

    public String getMaterialText() {
        return materialText;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public String getUnit() {
        return unit;
    }

    public double getQuantity() {
        return quantity;
    }
}
