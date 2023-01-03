package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderGiPostingRequest implements Serializable{

    @JSONField(name="PRODUCTIONNO")
    private String productionNo;

    @JSONField(name="LOGISTICSPROVIDER")
    private String logisticsProvider;

    @JSONField(name="LOGISTICSNUMBER")
    private String logisticsNumber;

    @JSONField(name="POSTINGDATE")
    private String postingDate;

    @JSONField(name="MATERIALNO")
    private String materialNo;

    @JSONField(name="PLANT")
    private String plant;

    @JSONField(name="STORAGELOCATION")
    private String storageLocation;

    @JSONField(name="BATCH")
    private String batch;

    @JSONField(name="REVERSATIONNO")
    private String reversationNo;

    @JSONField(name="REVERSATIONITEM")
    private String reversationItem;

    @JSONField(name="MOVEMENTTYPE")
    private String movementType;

    @JSONField(name="DOCUMENTTYPE")
    private String documentType;

    @JSONField(name="GI_QTY")
    private String giQty;

    @JSONField(name="BKTXT")
    private String bktxt;


    @JSONField(name="SERIALNO")
    private List<SerialNo> serialNo = new ArrayList<>();

    public PurchaseOrderGiPostingRequest() {
    }

    public PurchaseOrderGiPostingRequest(String productionNo, String logisticsProvider, String logisticsNumber,
                                         String postingDate, String materialNo, String plant, String storageLocation,
                                         String batch, String reversationNo, String reversationItem, String movementType,
                                         String documentType, String giQty, List<SerialNo> serialNo, String bktxt) {
        this.productionNo = productionNo;
        this.logisticsProvider = logisticsProvider;
        this.logisticsNumber = logisticsNumber;
        this.postingDate = postingDate;
        this.materialNo = materialNo;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.batch = batch;
        this.reversationNo = reversationNo;
        this.reversationItem = reversationItem;
        this.movementType = movementType;
        this.documentType = documentType;
        this.giQty = giQty;
        this.bktxt = bktxt;
        this.serialNo = serialNo;
    }

    public String getProductionNo() {
        return productionNo;
    }

    public void setProductionNo(String productionNo) {
        this.productionNo = productionNo;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getMaterialNo() {
        return materialNo;
    }

    public void setMaterialNo(String materialNo) {
        this.materialNo = materialNo;
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getReversationNo() {
        return reversationNo;
    }

    public void setReversationNo(String reversationNo) {
        this.reversationNo = reversationNo;
    }

    public String getReversationItem() {
        return reversationItem;
    }

    public void setReversationItem(String reversationItem) {
        this.reversationItem = reversationItem;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getGiQty() {
        return giQty;
    }

    public void setGiQty(String giQty) {
        this.giQty = giQty;
    }

    public List<SerialNo> getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(List<SerialNo> serialNo) {
        this.serialNo = serialNo;
    }

    public String getBktxt() {
        return bktxt;
    }

    public void setBktxt(String bktxt) {
        this.bktxt = bktxt;
    }
}
