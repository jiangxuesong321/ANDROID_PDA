package com.android.pda.models;

import java.io.Serializable;
import java.util.List;

public class TransferOrder implements Serializable, Comparable<TransferOrder>, Cloneable{
    private String reservedNo;
    private String reservedItemNo;
    private String applyType;
    private String moveType;
    private long applyDate;
    private long downloadTime;

    private String applyPerson;
    private String receivePerson;
    private String receivePlace;
    private String receiveContact;
    private String materialNo;
    private String materialDesc;
    private String spec;
    private String model;
    private String batchNo;
    private Double quantity;
    private String factory;
    private String factoryDesc;
    private String senderLoc;
    private String senderLocDesc;
    private String receiverLoc;
    private String receiverLocDesc;
    private String shipmentStatus;
    private String serialFlag;

    //扫码后，新增字段
    private int scanQuantity;
    private String storageLocation;
    private String logisticProvider;
    private String logisticNumber;
    private List<String> snList;
    private String unit;
    private String unitDescription;
    private String remark;
    private boolean batchFlag;
    private String createDate;

    private boolean sub;
    private int scanTotal;
    private String sn;

    public TransferOrder(String reservedNo, String reservedItemNo, String applyType, String moveType, long applyDate, long downloadTime, String applyPerson,
                         String receivePerson, String receivePlace, String receiveContact, String materialNo, String materialDesc, String spec, String model,
                         String batchNo, Double quantity, String factory, String factoryDesc, String senderLoc, String senderLocDesc, String receiverLoc,
                         String receiverLocDesc, String shipmentStatus, String serialFlag, String unit,
                         String unitDescription, String remark, boolean batchFlag, String createDate) {
        this.reservedNo = reservedNo;
        this.reservedItemNo = reservedItemNo;
        this.applyType = applyType;
        this.moveType = moveType;
        this.applyDate = applyDate;
        this.downloadTime = downloadTime;
        this.applyPerson = applyPerson;
        this.receivePerson = receivePerson;
        this.receivePlace = receivePlace;
        this.receiveContact = receiveContact;
        this.materialNo = materialNo;
        this.materialDesc = materialDesc;
        this.spec = spec;
        this.model = model;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.factory = factory;
        this.factoryDesc = factoryDesc;
        this.senderLoc = senderLoc;
        this.senderLocDesc = senderLocDesc;
        this.receiverLoc = receiverLoc;
        this.receiverLocDesc = receiverLocDesc;
        this.shipmentStatus = shipmentStatus;
        this.serialFlag = serialFlag;
        this.unit = unit;
        this.unitDescription = unitDescription;
        this.remark = remark;
        this.batchFlag = batchFlag;
        this.createDate = createDate;
    }

    public TransferOrder() {
    }

    public String getReservedNo() {
        return reservedNo;
    }

    public String getReservedItemNo() {
        return reservedItemNo;
    }

    public String getApplyType() {
        return applyType;
    }

    public String getMoveType() {
        return moveType;
    }

    public long getApplyDate() {
        return applyDate;
    }

    public long getDownloadTime() {
        return downloadTime;
    }

    public String getApplyPerson() {
        return applyPerson;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public String getReceivePlace() {
        return receivePlace;
    }

    public String getReceiveContact() {
        return receiveContact;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public String getSpec() {
        return spec;
    }

    public String getModel() {
        return model;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getFactory() {
        return factory;
    }

    public String getFactoryDesc() {
        return factoryDesc;
    }

    public String getSenderLoc() {
        return senderLoc;
    }

    public String getSenderLocDesc() {
        return senderLocDesc;
    }

    public String getReceiverLoc() {
        return receiverLoc;
    }

    public String getReceiverLocDesc() {
        return receiverLocDesc;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setScanQuantity(int scanQuantity) {
        this.scanQuantity = scanQuantity;
    }

    public void setLogisticProvider(String logisticProvider) {
        this.logisticProvider = logisticProvider;
    }

    public void setLogisticNumber(String logisticNumber) {
        this.logisticNumber = logisticNumber;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }

    public int getScanQuantity() {
        return scanQuantity;
    }

    public String getLogisticProvider() {
        return logisticProvider;
    }

    public String getLogisticNumber() {
        return logisticNumber;
    }

    public List<String> getSnList() {
        return snList;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
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

    public void setReservedNo(String reservedNo) {
        this.reservedNo = reservedNo;
    }

    public void setReservedItemNo(String reservedItemNo) {
        this.reservedItemNo = reservedItemNo;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public void setApplyDate(long applyDate) {
        this.applyDate = applyDate;
    }

    public void setDownloadTime(long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public void setApplyPerson(String applyPerson) {
        this.applyPerson = applyPerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public void setReceivePlace(String receivePlace) {
        this.receivePlace = receivePlace;
    }

    public void setReceiveContact(String receiveContact) {
        this.receiveContact = receiveContact;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setFactoryDesc(String factoryDesc) {
        this.factoryDesc = factoryDesc;
    }

    public void setSenderLoc(String senderLoc) {
        this.senderLoc = senderLoc;
    }

    public void setSenderLocDesc(String senderLocDesc) {
        this.senderLocDesc = senderLocDesc;
    }

    public void setReceiverLoc(String receiverLoc) {
        this.receiverLoc = receiverLoc;
    }

    public void setReceiverLocDesc(String receiverLocDesc) {
        this.receiverLocDesc = receiverLocDesc;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
    public int compareTo(TransferOrder o) {
        return this.reservedItemNo.compareTo(o.getReservedItemNo());
    }

    public String getParentItem(){
        return reservedNo + "-" + reservedItemNo + "-" + String.valueOf(isSub());
    }

    public String getItems(){
        return reservedNo + "-" + reservedItemNo;
    }


    public String groupItem(){
        return reservedItemNo + "-" + batchNo;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public Object clone() {
        TransferOrder o = null;
        try{
            o = (TransferOrder)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
