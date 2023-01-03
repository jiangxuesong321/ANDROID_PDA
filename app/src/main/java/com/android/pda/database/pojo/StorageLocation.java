package com.android.pda.database.pojo;

public class StorageLocation {
    private String plant;
    private String storageLocation;
    private String plantName;
    private String storageLocationName;

    public StorageLocation(String plant, String storageLocation, String plantName, String storageLocationName) {
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.plantName = plantName;
        this.storageLocationName = storageLocationName;
    }

    public String getPlant() {
        return plant;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getPlantName() {
        return plantName;
    }

    public String getStorageLocationName() {
        return storageLocationName;
    }

    public String getId(){
        return this.plant + "-" + this.storageLocation;
    }
}
