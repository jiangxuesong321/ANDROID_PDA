package com.sunmi.pda.models;

public class SalesInvoiceSN {
    private String deliveryDocument;
    private String deliveryDocumentItem;
    private String serialNo;

    public SalesInvoiceSN(String deliveryDocument, String deliveryDocumentItem, String serialNo) {
        this.deliveryDocument = deliveryDocument;
        this.deliveryDocumentItem = deliveryDocumentItem;
        this.serialNo = serialNo;
    }

    public String getDeliveryDocument() {
        return deliveryDocument;
    }

    public void setDeliveryDocument(String deliveryDocument) {
        this.deliveryDocument = deliveryDocument;
    }

    public String getDeliveryDocumentItem() {
        return deliveryDocumentItem;
    }

    public void setDeliveryDocumentItem(String deliveryDocumentItem) {
        this.deliveryDocumentItem = deliveryDocumentItem;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
}
