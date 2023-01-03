package com.android.pda.models;




import java.io.Serializable;
import java.util.List;

public class PrototypeBorrow implements Serializable, Comparable<PrototypeBorrow>, Cloneable{
    private String reservationNumber;
    private String applicationType;
    private String movementType;
    private Long applicationDate;
    private Long downloadTime;
    private String applicant;
    private String consignee;
    private String receiptPlace;
    private String consigneeContact;
    private String reservedItemNo;
    private String material;
    private String materialDescription;
    private String specs;
    private String model;
    private String batch;
    private double quantity;
    private String factory;
    private String factoryDescription;
    private String deliveryStockLocation;
    private String deliveryStockLocationDesc;
    private String receivingStockLocation;
    private String receivingStockLocationDesc;
    private String serialFlag;

    private String unit;
    private String unitDescription;
    private String remark;
    //扫码后，新增字段
    private int scanQuantity;
    private String storageLocation;
    private List<String> snList;
    private boolean batchFlag;
    private String createDate;

    private boolean sub;
    private int scanTotal;
    private String sn;
    public PrototypeBorrow(String reservationNumber, String applicationType, String movementType,
                           Long applicationDate, Long downloadTime, String applicant, String consignee,
                           String receiptPlace, String consigneeContact, String reservedItemNo,
                           String material, String materialDescription, String specs, String model,
                           String batch, double quantity, String factory, String factoryDescription,
                           String deliveryStockLocation, String deliveryStockLocationDesc,
                           String receivingStockLocation, String receivingStockLocationDesc, String serialFlag,
                           String unit, String unitDescription, String remark, boolean batchFlag, String createDate) {
        this.reservationNumber = reservationNumber;
        this.applicationType = applicationType;
        this.movementType = movementType;
        this.applicationDate = applicationDate;
        this.downloadTime = downloadTime;
        this.applicant = applicant;
        this.consignee = consignee;
        this.receiptPlace = receiptPlace;
        this.consigneeContact = consigneeContact;
        this.reservedItemNo = reservedItemNo;
        this.material = material;
        this.materialDescription = materialDescription;
        this.specs = specs;
        this.model = model;
        this.batch = batch;
        this.quantity = quantity;
        this.factory = factory;
        this.factoryDescription = factoryDescription;
        this.deliveryStockLocation = deliveryStockLocation;
        this.deliveryStockLocationDesc = deliveryStockLocationDesc;
        this.receivingStockLocation = receivingStockLocation;
        this.receivingStockLocationDesc = receivingStockLocationDesc;
        this.serialFlag = serialFlag;
        this.unit = unit;
        this.unitDescription = unitDescription;
        this.remark = remark;
        this.batchFlag = batchFlag;
        this.createDate = createDate;
    }

    public PrototypeBorrow() {
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public Long getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Long applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getReceiptPlace() {
        return receiptPlace;
    }

    public void setReceiptPlace(String receiptPlace) {
        this.receiptPlace = receiptPlace;
    }

    public String getConsigneeContact() {
        return consigneeContact;
    }

    public void setConsigneeContact(String consigneeContact) {
        this.consigneeContact = consigneeContact;
    }

    public String getReservedItemNo() {
        return reservedItemNo;
    }

    public void setReservedItemNo(String reservedItemNo) {
        this.reservedItemNo = reservedItemNo;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getFactoryDescription() {
        return factoryDescription;
    }

    public void setFactoryDescription(String factoryDescription) {
        this.factoryDescription = factoryDescription;
    }

    public String getDeliveryStockLocation() {
        return deliveryStockLocation;
    }

    public void setDeliveryStockLocation(String deliveryStockLocation) {
        this.deliveryStockLocation = deliveryStockLocation;
    }

    public String getDeliveryStockLocationDesc() {
        return deliveryStockLocationDesc;
    }

    public void setDeliveryStockLocationDesc(String deliveryStockLocationDesc) {
        this.deliveryStockLocationDesc = deliveryStockLocationDesc;
    }

    public String getReceivingStockLocation() {
        return receivingStockLocation;
    }

    public void setReceivingStockLocation(String receivingStockLocation) {
        this.receivingStockLocation = receivingStockLocation;
    }

    public String getReceivingStockLocationDesc() {
        return receivingStockLocationDesc;
    }

    public void setReceivingStockLocationDesc(String receivingStockLocationDesc) {
        this.receivingStockLocationDesc = receivingStockLocationDesc;
    }

    public int getScanQuantity() {
        return scanQuantity;
    }

    public void setScanQuantity(int scanQuantity) {
        this.scanQuantity = scanQuantity;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public List<String> getSnList() {
        return snList;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;

    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUnit() {
        return unit;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(boolean batchFlag) {
        this.batchFlag = batchFlag;
    }

    public boolean isSub() {
        return sub;
    }

    public void setSub(boolean sub) {
        this.sub = sub;
    }

    public int getScanTotal() {
        return scanTotal;
    }

    public void setScanTotal(int scanTotal) {
        this.scanTotal = scanTotal;
    }

    @Override
    public int compareTo(PrototypeBorrow o) {
        return this.reservedItemNo.compareTo(o.getReservedItemNo());
    }

    public String getParentItem(){
        return reservationNumber + "-" + reservedItemNo + "-" + String.valueOf(isSub());
    }

    public String getItems(){
        return reservationNumber + "-" + reservedItemNo;
    }


    public String groupItem(){
        return reservedItemNo + "-" + batch;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public Object clone() {
        PrototypeBorrow o = null;
        try{
            o = (PrototypeBorrow)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
