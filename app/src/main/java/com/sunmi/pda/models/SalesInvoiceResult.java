package com.sunmi.pda.models;

import com.sunmi.pda.models.SalesInvoice;

import java.io.Serializable;
import java.util.List;

public class SalesInvoiceResult implements Serializable, Comparable<SalesInvoiceResult>{
    private String deliveryDocument;
    private String downloadDocumentTime;
    private String deliveryDone;
    private List<SalesInvoice> salesInvoices;

    public SalesInvoiceResult(String deliveryDocument, String downloadDocumentTime, String deliveryDone, List<SalesInvoice> salesInvoices) {
        this.deliveryDocument = deliveryDocument;
        this.downloadDocumentTime = downloadDocumentTime;
        this.deliveryDone = deliveryDone;
        this.salesInvoices = salesInvoices;
    }

    public String getDeliveryDocument() {
        return deliveryDocument;
    }

    public String getDownloadDocumentTime() {
        return downloadDocumentTime;
    }

    public String getDeliveryDone() {
        return deliveryDone;
    }

    public List<SalesInvoice> getSalesInvoices() {
        return salesInvoices;
    }

    @Override
    public int compareTo(SalesInvoiceResult o) {
        return this.deliveryDocument.compareTo(o.getDeliveryDocument());
    }
}
