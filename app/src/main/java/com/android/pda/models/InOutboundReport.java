package com.android.pda.models;



public class InOutboundReport {
    private String documentType;
    private String orderNumber;
    private String materialDocumentNo;
    private String materialDocumentYear;
    private String material;
    private String materialName;
    private String specifications;
    private String batch;
    private String serialNumber;
    private String unit;
    private double quantity;
    private String contacts;
    private String address;
    private String contactNumber;
    private String logisticsProvider;
    private String logisticsOrderNo;
    private String serialFlag;
    private String postingDate;
    private String storeLocation;
    private String parameters;
    private String model;
    private String customPoNumber;
    private String customPCustomMaterialoNumber;
    private String customName;
    private String storeLocationName;
    public InOutboundReport() {
    }

    public InOutboundReport(String documentType, String orderNumber, String materialDocumentNo,
                            String materialDocumentYear, String material, String materialName,
                            String specifications, String batch, String serialNumber, String unit,
                            double quantity, String contacts, String address, String contactNumber,
                            String logisticsProvider, String logisticsOrderNo, String serialFlag,
                            String postingDate, String storeLocation, String parameters, String model,
                            String customPoNumber, String customPCustomMaterialoNumber,
                            String customName, String storeLocationName) {
        this.documentType = documentType;
        this.orderNumber = orderNumber;
        this.materialDocumentNo = materialDocumentNo;
        this.materialDocumentYear = materialDocumentYear;
        this.material = material;
        this.materialName = materialName;
        this.specifications = specifications;
        this.batch = batch;
        this.serialNumber = serialNumber;
        this.unit = unit;
        this.quantity = quantity;
        this.contacts = contacts;
        this.address = address;
        this.contactNumber = contactNumber;
        this.logisticsProvider = logisticsProvider;
        this.logisticsOrderNo = logisticsOrderNo;
        this.serialFlag = serialFlag;
        this.postingDate = postingDate;
        this.storeLocation = storeLocation;
        this.parameters = parameters;
        this.model = model;
        this.customPoNumber = customPoNumber;
        this.customPCustomMaterialoNumber = customPCustomMaterialoNumber;
        this.customName = customName;
        this.storeLocationName = storeLocationName;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getMaterialDocumentNo() {
        return materialDocumentNo;
    }

    public void setMaterialDocumentNo(String materialDocumentNo) {
        this.materialDocumentNo = materialDocumentNo;
    }

    public String getMaterialDocumentYear() {
        return materialDocumentYear;
    }

    public void setMaterialDocumentYear(String materialDocumentYear) {
        this.materialDocumentYear = materialDocumentYear;
    }

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

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

    public String getLogisticsOrderNo() {
        return logisticsOrderNo;
    }

    public void setLogisticsOrderNo(String logisticsOrderNo) {
        this.logisticsOrderNo = logisticsOrderNo;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStoreLocationName() {
        return storeLocationName;
    }

    public void setStoreLocationName(String storeLocationName) {
        this.storeLocationName = storeLocationName;
    }

    public String getCustomPoNumber() {
        return customPoNumber;
    }

    public void setCustomPoNumber(String customPoNumber) {
        this.customPoNumber = customPoNumber;
    }

    public String getCustomPCustomMaterialoNumber() {
        return customPCustomMaterialoNumber;
    }

    public void setCustomPCustomMaterialoNumber(String customPCustomMaterialoNumber) {
        this.customPCustomMaterialoNumber = customPCustomMaterialoNumber;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
