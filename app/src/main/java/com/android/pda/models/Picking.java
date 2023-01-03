package com.android.pda.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Picking implements Serializable, Comparable<Picking>, Cloneable{
    private String reservedNo;
    private String applyType;
    private String moveType;
    private String costCenter;
    private String costCenterDesc;
    private Long applicationDate;
    private Long downloadTime;
    private String applyPerson;
    private String receivePerson;
    private String receiptPlace;
    private String receiveContact;
    private String reservedItemNo;
    private String material;
    private String materialDescription;
    private String specs;
    private String model;
    private String batch;
    private double quantity;
    private String factory;
    private String factoryDescription;
    private String storeLoc;
    private String storeLocDesc;
    private String serialFlag;

    private String unit;
    private String unitDescription;
    private String remark;
    private String createDate;

    //扫码后，新增字段
    private int scanQuantity;
    private String storageLocation;
    private List<String> snList = new ArrayList<>();
    private boolean batchFlag;

    private boolean sub;
    private int scanTotal;
    private String sn;
    public Picking() {
    }

    public Picking(String reservedNo, String applyType, String moveType, String costCenter, String costCenterDesc,
                   Long applicationDate, Long downloadTime, String applyPerson, String receivePerson,
                   String receiptPlace, String receiveContact, String reservedItemNo, String material,
                   String materialDescription, String specs, String model, String batch, double quantity,
                   String factory, String factoryDescription, String storeLoc, String storeLocDesc,
                   String serialFlag, String unit, String unitDescription, String remark, boolean batchFlag, String createDate) {
        this.reservedNo = reservedNo;
        this.applyType = applyType;
        this.moveType = moveType;
        this.costCenter = costCenter;
        this.costCenterDesc = costCenterDesc;
        this.applicationDate = applicationDate;
        this.downloadTime = downloadTime;
        this.applyPerson = applyPerson;
        this.receivePerson = receivePerson;
        this.receiptPlace = receiptPlace;
        this.receiveContact = receiveContact;
        this.reservedItemNo = reservedItemNo;
        this.material = material;
        this.materialDescription = materialDescription;
        this.specs = specs;
        this.model = model;
        this.batch = batch;
        this.quantity = quantity;
        this.factory = factory;
        this.factoryDescription = factoryDescription;
        this.storeLoc = storeLoc;
        this.storeLocDesc = storeLocDesc;
        this.serialFlag = serialFlag;

        this.unit = unit;
        this.unitDescription = unitDescription;
        this.remark = remark;
        this.batchFlag = batchFlag;
        this.createDate = createDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getReservedNo() {
        return reservedNo;
    }

    public String getApplyType() {
        return applyType;
    }

    public String getMoveType() {
        return moveType;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public String getCostCenterDesc() {
        return costCenterDesc;
    }

    public Long getApplicationDate() {
        return applicationDate;
    }

    public Long getDownloadTime() {
        return downloadTime;
    }

    public String getApplyPerson() {
        return applyPerson;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public String getReceiptPlace() {
        return receiptPlace;
    }

    public String getReceiveContact() {
        return receiveContact;
    }

    public String getReservedItemNo() {
        return reservedItemNo;
    }

    public String getMaterial() {
        return material;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public String getSpecs() {
        return specs;
    }

    public String getModel() {
        return model;
    }

    public String getBatch() {
        return batch;
    }

    public double getQuantity() {
        return quantity;
    }


    public String getFactory() {
        return factory;
    }

    public String getFactoryDescription() {
        return factoryDescription;
    }

    public String getStoreLoc() {
        return storeLoc;
    }

    public String getStoreLocDesc() {
        return storeLocDesc;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public int getScanQuantity() {
        return scanQuantity;
    }

    public void setScanQuantity(int scanQuantity) {
        this.scanQuantity = scanQuantity;
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

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public void setReservedNo(String reservedNo) {
        this.reservedNo = reservedNo;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public void setCostCenterDesc(String costCenterDesc) {
        this.costCenterDesc = costCenterDesc;
    }

    public void setApplicationDate(Long applicationDate) {
        this.applicationDate = applicationDate;
    }

    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public void setApplyPerson(String applyPerson) {
        this.applyPerson = applyPerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public void setReceiptPlace(String receiptPlace) {
        this.receiptPlace = receiptPlace;
    }

    public void setReceiveContact(String receiveContact) {
        this.receiveContact = receiveContact;
    }

    public void setReservedItemNo(String reservedItemNo) {
        this.reservedItemNo = reservedItemNo;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setFactoryDescription(String factoryDescription) {
        this.factoryDescription = factoryDescription;
    }

    public void setStoreLoc(String storeLoc) {
        this.storeLoc = storeLoc;
    }

    public void setStoreLocDesc(String storeLocDesc) {
        this.storeLocDesc = storeLocDesc;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
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
    public int compareTo(Picking o) {
        return this.reservedItemNo.compareTo(o.getReservedItemNo());
    }

    public String getParentItem(){
        return reservedNo + "-" + reservedItemNo + "-" + String.valueOf(isSub());
    }

    public String getItems(){
        return reservedNo + "-" + reservedItemNo;
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
        Picking o = null;
        try{
            o = (Picking)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
