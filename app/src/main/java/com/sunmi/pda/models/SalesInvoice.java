package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class SalesInvoice implements Serializable {

    private String deliveryDocument;
    private String deliveryDocumentItem;
    private String shipToParty;
    private String shipToPartyName;
    private String shipToPartyAddress;
    private long downloadDocumentTime;

    private String material;
    private String materialDescribe;
    private String serialFlag;
    private String receivingPlant;
    private String receivingPlantDescribe;
    private String inventoryLocation;
    private String inventoryLocationDescribe;
    private String unit;
    private Double shipmentQuantity;
    private String batch;

    private String contacts;
    private String contactNumber;
    private String remark;

    private int scanCount;
    private String supplier;
    private String shipmentStatus;
    private long plannedDeliveryDate;
    private List<String> scanBarcodes;
    private String storageLocation;
    private String logisticsVendor;
    private boolean batchFlag;
    private String model;
    private String specs;
    private String createDate;
    private String itemRemark;
    private String restruRemark;
    private String customPoNumber;
    private String customMaterial;
    private String transportMode;
    private String shippingInfo;
    public SalesInvoice() {
    }

    public SalesInvoice(String deliveryDocument, String deliveryDocumentItem, String shipToParty, String shipToPartyName, String shipToPartyAddress,
                        long downloadDocumentTime, String material, String materialDescribe, String receivingPlant, String receivingPlantDescribe,
                        String inventoryLocation, String inventoryLocationDescribe, String unit, Double shipmentQuantity, String batch, String supplier,
                        String shipmentStatus, long plannedDeliveryDate, String serialFlag, String remark, String contacts, String contactNumber,
                        int scanCount, String logisticsVendor, boolean batchFlag, String model, String createDate,
                        String itemRemark, String restruRemark, String customPoNumber, String customMaterial,
                        String transportMode, String shippingInfo, String specs) {
        this.deliveryDocument = deliveryDocument;
        this.deliveryDocumentItem = deliveryDocumentItem;
        this.shipToParty = shipToParty;
        this.shipToPartyName = shipToPartyName;
        this.shipToPartyAddress = shipToPartyAddress;
        this.downloadDocumentTime = downloadDocumentTime;
        this.material = material;
        this.materialDescribe = materialDescribe;
        this.receivingPlant = receivingPlant;
        this.receivingPlantDescribe = receivingPlantDescribe;
        this.inventoryLocation = inventoryLocation;
        this.inventoryLocationDescribe = inventoryLocationDescribe;
        this.unit = unit;
        this.shipmentQuantity = shipmentQuantity;
        this.batch = batch;
        this.supplier = supplier;
        this.shipmentStatus = shipmentStatus;
        this.plannedDeliveryDate = plannedDeliveryDate;
        this.serialFlag = serialFlag;
        this.remark = remark;
        this.contacts = contacts;
        this.contactNumber = contactNumber;
        this.scanCount = scanCount;
        this.logisticsVendor = logisticsVendor;
        this.batchFlag = batchFlag;
        this.model = model;
        this.createDate = createDate;
        this.itemRemark = itemRemark;
        this.restruRemark = restruRemark;
        this.customPoNumber = customPoNumber;
        this.customMaterial = customMaterial;
        this.transportMode = transportMode;
        this.shippingInfo = shippingInfo;
        this.specs = specs;
        }

    public String getDeliveryDocument() {
        return deliveryDocument;
    }

    public String getDeliveryDocumentItem() {
        return deliveryDocumentItem;
    }

    public String getShipToParty() {
        return shipToParty;
    }

    public String getShipToPartyName() {
        return shipToPartyName;
    }

    public String getShipToPartyAddress() {
        return shipToPartyAddress;
    }

    public long getDownloadDocumentTime() {
        return downloadDocumentTime;
    }

    public String getMaterial() {
        return material;
    }

    public String getMaterialDescribe() {
        return materialDescribe;
    }

    public String getReceivingPlant() {
        return receivingPlant;
    }

    public String getReceivingPlantDescribe() {
        return receivingPlantDescribe;
    }

    public String getInventoryLocation() {
        return inventoryLocation;
    }

    public String getInventoryLocationDescribe() {
        return inventoryLocationDescribe;
    }

    public String getUnit() {
        return unit;
    }

    public Double getShipmentQuantity() {
        return shipmentQuantity;
    }

    public String getBatch() {
        return batch;
    }

    public int getScanCount() {
        return scanCount;
    }

    public void setScanCount(int scanCount) {
        this.scanCount = scanCount;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public long getPlannedDeliveryDate() {
        return plannedDeliveryDate;
    }

    public List<String> getScanBarcodes() {
        return scanBarcodes;
    }

    public void setScanBarcodes(List<String> scanBarcodes) {
        this.scanBarcodes = scanBarcodes;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getContacts() {
        return contacts;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setDeliveryDocument(String deliveryDocument) {
        this.deliveryDocument = deliveryDocument;
    }

    public void setDeliveryDocumentItem(String deliveryDocumentItem) {
        this.deliveryDocumentItem = deliveryDocumentItem;
    }

    public void setShipToParty(String shipToParty) {
        this.shipToParty = shipToParty;
    }

    public void setShipToPartyName(String shipToPartyName) {
        this.shipToPartyName = shipToPartyName;
    }

    public void setShipToPartyAddress(String shipToPartyAddress) {
        this.shipToPartyAddress = shipToPartyAddress;
    }

    public void setDownloadDocumentTime(long downloadDocumentTime) {
        this.downloadDocumentTime = downloadDocumentTime;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setMaterialDescribe(String materialDescribe) {
        this.materialDescribe = materialDescribe;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public void setReceivingPlant(String receivingPlant) {
        this.receivingPlant = receivingPlant;
    }

    public void setReceivingPlantDescribe(String receivingPlantDescribe) {
        this.receivingPlantDescribe = receivingPlantDescribe;
    }

    public void setInventoryLocation(String inventoryLocation) {
        this.inventoryLocation = inventoryLocation;
    }

    public void setInventoryLocationDescribe(String inventoryLocationDescribe) {
        this.inventoryLocationDescribe = inventoryLocationDescribe;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setShipmentQuantity(Double shipmentQuantity) {
        this.shipmentQuantity = shipmentQuantity;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public void setPlannedDeliveryDate(long plannedDeliveryDate) {
        this.plannedDeliveryDate = plannedDeliveryDate;
    }

    public String getLogisticsVendor() {
        return logisticsVendor;
    }

    public void setLogisticsVendor(String logisticsVendor) {
        this.logisticsVendor = logisticsVendor;
    }

    public boolean isBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(boolean batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getItemRemark() {
        return itemRemark;
    }

    public void setItemRemark(String itemRemark) {
        this.itemRemark = itemRemark;
    }

    public String getRestruRemark() {
        return restruRemark;
    }

    public void setRestruRemark(String restruRemark) {
        this.restruRemark = restruRemark;
    }

    public String getCustomPoNumber() {
        return customPoNumber;
    }

    public void setCustomPoNumber(String customPoNumber) {
        this.customPoNumber = customPoNumber;
    }

    public String getCustomMaterial() {
        return customMaterial;
    }

    public void setCustomMaterial(String customMaterial) {
        this.customMaterial = customMaterial;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(String shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
