package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class GeneralMaterialDocumentItemResults {
    @JSONField(name="Reservation")
    private String reservation;

    @JSONField(name="ReservationItem")
    private String reservationItem;

    @JSONField(name="ReservationIsFinallyIssued")
    private boolean reservationIsFinallyIssued;

    @JSONField(name="Material")
    private String material;

    @JSONField(name="Batch")
    private String batch;

    @JSONField(name="Plant")
    private String plant;

    @JSONField(name="StorageLocation")
    private String storageLocation;

    @JSONField(name="GoodsMovementRefDocType")
    private String goodsMovementRefDocType;
    @JSONField(name="GoodsMovementType")
    private String goodsMovementType;

    @JSONField(name="IssuingOrReceivingStorageLoc")
    private String issuingOrReceivingStorageLoc;


    @JSONField(name="QuantityInEntryUnit")
    private String quantityInEntryUnit;

    @JSONField(name="to_SerialNumbers")
    private SerialNumber serialNumber;

    @JSONField(name="CostCenter")
    private String costCenter;

    @JSONField(name="MaterialDocumentItemText")
    private String materialDocumentItemText;

    private String materialDesc;
    public GeneralMaterialDocumentItemResults() {
    }




    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public String getReservationItem() {
        return reservationItem;
    }

    public void setReservationItem(String reservationItem) {
        this.reservationItem = reservationItem;
    }

    public boolean isReservationIsFinallyIssued() {
        return reservationIsFinallyIssued;
    }

    public void setReservationIsFinallyIssued(boolean reservationIsFinallyIssued) {
        this.reservationIsFinallyIssued = reservationIsFinallyIssued;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getPlant() {
        return plant;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getGoodsMovementRefDocType() {
        return goodsMovementRefDocType;
    }

    public void setGoodsMovementRefDocType(String goodsMovementRefDocType) {
        this.goodsMovementRefDocType = goodsMovementRefDocType;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public void setGoodsMovementType(String goodsMovementType) {
        this.goodsMovementType = goodsMovementType;
    }

    public String getIssuingOrReceivingStorageLoc() {
        return issuingOrReceivingStorageLoc;
    }

    public void setIssuingOrReceivingStorageLoc(String issuingOrReceivingStorageLoc) {
        this.issuingOrReceivingStorageLoc = issuingOrReceivingStorageLoc;
    }

    public String getQuantityInEntryUnit() {
        return quantityInEntryUnit;
    }

    public void setQuantityInEntryUnit(String quantityInEntryUnit) {
        this.quantityInEntryUnit = quantityInEntryUnit;
    }

    public SerialNumber getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(SerialNumber serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getMaterialDocumentItemText() {
        return materialDocumentItemText;
    }

    public void setMaterialDocumentItemText(String materialDocumentItemText) {
        this.materialDocumentItemText = materialDocumentItemText;
    }
}
