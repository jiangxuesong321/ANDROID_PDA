package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class OrderInvoiceOthersQuery implements Serializable {
    String deliveryDocument;
    String deliveryDateFrom;
    String deliveryDateTo;
    List<DeliveryStatus> deliveryStatuses;  // --- ShipmentStatus
    List<String> storageLocations;
    private List<Integer> orderIndices;


    public OrderInvoiceOthersQuery(String deliveryDocument) {
        this.deliveryDocument = deliveryDocument;
    }

    public OrderInvoiceOthersQuery(String deliveryDocument, String deliveryDateFrom, String deliveryDateTo,
                                   List<DeliveryStatus> deliveryStatuses, List<Integer> orderIndices) {
        this.deliveryDocument = deliveryDocument;
        this.deliveryDateFrom = deliveryDateFrom;
        this.deliveryDateTo = deliveryDateTo;
        this.deliveryStatuses = deliveryStatuses;
        this.orderIndices = orderIndices;
    }

    public List<Integer> getOrderIndices() {
        return orderIndices;
    }

    public void setOrderIndices(List<Integer> orderIndices) {
        this.orderIndices = orderIndices;
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
