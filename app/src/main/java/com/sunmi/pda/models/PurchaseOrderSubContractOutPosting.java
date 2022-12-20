package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PurchaseOrderSubContractOutPosting {
    @JSONField(name="PSTNG_DATE")
    private String pstngDate ;
    @JSONField(name="DOC_DATE")
    private String docDate;
    @JSONField(name="MOVE_TYPE")
    private String moveType ;
    @JSONField(name="MATERIAL")
    private String material;

    @JSONField(name="PLANT")
    private String plant;

    @JSONField(name="STGE_LOC")
    private String stgeLoc;

    @JSONField(name="ENTRY_QNT")
    private String entryQnt ;

    @JSONField(name="BATCH")
    private String batch;

    @JSONField(name="VENDOR")
    private String vendor;

    @JSONField(name="SPEC_STOCK")
    private String specStock ;

    @JSONField(name="LOGISTICSNUMBER")
    private String logisticsNumber;

    @JSONField(name="LOGISTICSPROVIDER")
    private String logisticsProvider;

    @JSONField(name="DOCUMENTTYPE")
    private String documentType;

    @JSONField(name="PO_NUMBER")
    private String poNumber;

    @JSONField(name="PO_ITEM")
    private String poItem;

    @JSONField(name="SerialNo")
    List<SerialNumberPoSubContract> serialNumberList;

    public PurchaseOrderSubContractOutPosting() {
    }

    public PurchaseOrderSubContractOutPosting(String pstngDate, String docDate, String moveType,
                                              String material, String plant, String stgeLoc, String entryQnt,
                                              String batch, String vendor, String specStock, String logisticsProvider,
                                              String logisticsNumber, String documentType,
                                              List<SerialNumberPoSubContract> serialNumberList, String poNumber, String poItem) {
        this.pstngDate = pstngDate;
        this.docDate = docDate;
        this.moveType = moveType;
        this.material = material;
        this.plant = plant;
        this.stgeLoc = stgeLoc;
        this.entryQnt = entryQnt;
        this.batch = batch;
        this.vendor = vendor;
        this.specStock = specStock;
        this.logisticsNumber = logisticsNumber;
        this.logisticsProvider = logisticsProvider;
        this.documentType = documentType;
        this.poNumber = poNumber;
        this.poItem = poItem;
        this.serialNumberList = serialNumberList;
    }

    public String getPstngDate() {
        return pstngDate;
    }

    public String getDocDate() {
        return docDate;
    }

    public String getMoveType() {
        return moveType;
    }

    public String getMaterial() {
        return material;
    }

    public String getPlant() {
        return plant;
    }

    public String getStgeLoc() {
        return stgeLoc;
    }

    public String getEntryQnt() {
        return entryQnt;
    }

    public String getBatch() {
        return batch;
    }

    public String getVendor() {
        return vendor;
    }

    public String getSpecStock() {
        return specStock;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public List<SerialNumberPoSubContract> getSerialNumberList() {
        return serialNumberList;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public String getPoItem() {
        return poItem;
    }
}
