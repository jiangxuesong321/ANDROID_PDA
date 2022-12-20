package com.sunmi.pda.models;

import java.io.Serializable;

public class OrderInvoiceOthersResult implements Serializable {

    private String reservedNo;
    private String reservedItemNo;
    private String applyType;
    private String moveType;
    private String costCenter;
    private String costCenterDesc;
    private long applyDate;
    private Long downloadTime;
    private String applyPerson;
    private String receivePerson;
    private String receivePlace;
    private String receiveContact;
    private String materialNo;
    private String materialDesc;
    private String spec;
    private String batchNo;
    private Double quantity;
    private String factory;
    private String factoryDesc;
    private String storeLoc;
    private String storeLocDesc;
    private String receivingStockLocation;
    private String receivingStockLocationDesc;
    private String serialFlag;
    private Double WithdrawalQuantity;
    private String shipmentStatus;
    private String RequiredDate;
    private String remark;
    private String unit;
    private String UnitDesciption;
    private boolean batchFlag;
    private String Model;
    private String createDate;
    private String parameters;
    private String Logisticsprovider;
    public OrderInvoiceOthersResult(String reservedNo, String reservedItemNo, String applyType,
                                    String moveType, String costCenter, String costCenterDesc,
                                    long applyDate, Long downloadTime, String applyPerson,
                                    String receivePerson, String receivePlace, String receiveContact,
                                    String materialNo, String materialDesc, String spec, String batchNo,
                                    Double quantity, String factory, String factoryDesc, String storeLoc,
                                    String storeLocDesc, String receivingStockLocation, String receivingStockLocationDesc,
                                    String serialFlag, Double withdrawalQuantity, String shipmentStatus,
                                    String requiredDate, String remark, String unit, String unitDesciption,
                                    boolean batchFlag, String model, String createDate, String parameters,
                                    String Logisticsprovider) {
        this.reservedNo = reservedNo;
        this.reservedItemNo = reservedItemNo;
        this.applyType = applyType;
        this.moveType = moveType;
        this.costCenter = costCenter;
        this.costCenterDesc = costCenterDesc;
        this.applyDate = applyDate;
        this.downloadTime = downloadTime;
        this.applyPerson = applyPerson;
        this.receivePerson = receivePerson;
        this.receivePlace = receivePlace;
        this.receiveContact = receiveContact;
        this.materialNo = materialNo;
        this.materialDesc = materialDesc;
        this.spec = spec;
        this.batchNo = batchNo;
        this.quantity = quantity;
        this.factory = factory;
        this.factoryDesc = factoryDesc;
        this.storeLoc = storeLoc;
        this.storeLocDesc = storeLocDesc;
        this.receivingStockLocation = receivingStockLocation;
        this.receivingStockLocationDesc = receivingStockLocationDesc;
        this.serialFlag = serialFlag;
        this.WithdrawalQuantity = withdrawalQuantity;
        this.shipmentStatus = shipmentStatus;
        this.RequiredDate = requiredDate;
        this.remark = remark;
        this.unit = unit;
        this.UnitDesciption = unitDesciption;
        batchFlag = batchFlag;
        this.Model = model;
        this.createDate = createDate;
        this.parameters = parameters;
        this.Logisticsprovider = Logisticsprovider;
    }

//    public OrderBatchInvoiceResult(String reservedNo, String reservedItemNo, String applyType, String moveType, String costCenter, String costCenterDesc, long applyDate, long downloadTime, String applyPerson, String receivePerson, String receivePlace, String receiveContact, String unit, String materialNo, String materialDesc, String spec, String batchNo, Double quantity, String factory, String factoryDesc, String storeLoc, String storeLocDesc, String receivingStockLocation, String receivingStockLocationDesc, String serialFlag, Double withdrawalQuantity, String shipmentStatus, String requiredDate, String remark, String unit1, String unitDesciption, Boolean batchFlag, String model, String createDate, Double parameters) {
//    }



    public String getReservedNo() {
        return reservedNo;
    }

    public void setReservedNo(String reservedNo) {
        this.reservedNo = reservedNo;
    }

    public String getReservedItemNo() {
        return reservedItemNo;
    }

    public void setReservedItemNo(String reservedItemNo) {
        this.reservedItemNo = reservedItemNo;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getCostCenterDesc() {
        return costCenterDesc;
    }

    public void setCostCenterDesc(String costCenterDesc) {
        this.costCenterDesc = costCenterDesc;
    }

    public long getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(long applyDate) {
        this.applyDate = applyDate;
    }

    public Long getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(Long downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getApplyPerson() {
        return applyPerson;
    }

    public void setApplyPerson(String applyPerson) {
        this.applyPerson = applyPerson;
    }

    public String getReceivePerson() {
        return receivePerson;
    }

    public void setReceivePerson(String receivePerson) {
        this.receivePerson = receivePerson;
    }

    public String getReceivePlace() {
        return receivePlace;
    }

    public void setReceivePlace(String receivePlace) {
        this.receivePlace = receivePlace;
    }

    public String getReceiveContact() {
        return receiveContact;
    }

    public void setReceiveContact(String receiveContact) {
        this.receiveContact = receiveContact;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getFactoryDesc() {
        return factoryDesc;
    }

    public void setFactoryDesc(String factoryDesc) {
        this.factoryDesc = factoryDesc;
    }

    public String getStoreLoc() {
        return storeLoc;
    }

    public void setStoreLoc(String storeLoc) {
        this.storeLoc = storeLoc;
    }

    public String getStoreLocDesc() {
        return storeLocDesc;
    }

    public void setStoreLocDesc(String storeLocDesc) {
        this.storeLocDesc = storeLocDesc;
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

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public Double getWithdrawalQuantity() {
        return WithdrawalQuantity;
    }

    public void setWithdrawalQuantity(Double withdrawalQuantity) {
        WithdrawalQuantity = withdrawalQuantity;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public void setShipmentStatus(String shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public String getRequiredDate() {
        return RequiredDate;
    }

    public void setRequiredDate(String requiredDate) {
        RequiredDate = requiredDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitDesciption() {
        return UnitDesciption;
    }

    public void setUnitDesciption(String unitDesciption) {
        UnitDesciption = unitDesciption;
    }

    public Boolean getBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(Boolean batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getLogisticsprovider() {
        return Logisticsprovider;
    }
}
