package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class SalesInvoiceQuery implements Serializable {
    String deliveryDocument;
    String deliveryDateFrom;
    String deliveryDateTo;
    List<DeliveryStatus> deliveryStatuses;  // --- ShipmentStatus
    List<String> storageLocations;

    public SalesInvoiceQuery(String deliveryDocument) {
        this.deliveryDocument = deliveryDocument;
    }

    public SalesInvoiceQuery(String deliveryDocument, String deliveryDateFrom, String deliveryDateTo,
                             List<DeliveryStatus> deliveryStatuses) {
        this.deliveryDocument = deliveryDocument;
        this.deliveryDateFrom = deliveryDateFrom;
        this.deliveryDateTo = deliveryDateTo;
        this.deliveryStatuses = deliveryStatuses;
    }

    public String getDeliveryDocument() {
        return deliveryDocument;
    }

    public void setDeliveryDocument(String deliveryDocument) {
        this.deliveryDocument = deliveryDocument;
    }

    public String getDeliveryDateFrom() {
        return deliveryDateFrom;
    }

    public String getDeliveryDateTo() {
        return deliveryDateTo;
    }

    public List<DeliveryStatus> getDeliveryStatuses() {
        return deliveryStatuses;
    }

    public List<String> getStorageLocations() {
        return storageLocations;
    }

    public void setStorageLocations(List<String> storageLocations) {
        this.storageLocations = storageLocations;
    }
}

