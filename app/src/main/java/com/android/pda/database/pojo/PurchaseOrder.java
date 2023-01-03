package com.android.pda.database.pojo;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder implements Serializable, Comparable<PurchaseOrder>{

    private String materialDocument;
    private String materialDocumentItem;
    private String movementType;
    private String debitCreditind;

    private String purchaseOrder;
    private String purchaseOrderItem;
    private String supplier;
    private long creationDate;
    private String material;
    private String purchaseOrderItemText;
    private String plant;
    private long deliveryDate;
    private String unit;
    private double orderQuantity;
    private double openQuantity;
    private String storageLocation;

    private String batch;
    private String quantityInEntryUnit;

    private boolean subOrder;
    private int purchaseOrderItemNr;
    private boolean batchFlag;
    private String model;

    private boolean isSet = false;

    private String serialFlag;
    private List<String> snList = new ArrayList<>();

    private String reason;
    private boolean poReturnInd;
    public PurchaseOrder() {
    }

    public PurchaseOrder(String materialDocument, String materialDocumentItem, String movementType,
                         String debitCreditind, String purchaseOrder, String purchaseOrderItem,
                         String material, String purchaseOrderItemText, String plant, double openQuantity,
                         String storageLocation, String batch, boolean batchFlag, String serialFlag) {
        this.materialDocument = materialDocument;
        this.materialDocumentItem = materialDocumentItem;
        this.movementType = movementType;
        this.debitCreditind = debitCreditind;
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderItem = purchaseOrderItem;
        this.material = material;
        this.purchaseOrderItemText = purchaseOrderItemText;
        this.plant = plant;
        this.openQuantity = openQuantity;
        this.storageLocation = storageLocation;
        this.batch = batch;
        this.batchFlag = batchFlag;
        this.serialFlag = serialFlag;
    }

    public PurchaseOrder(String purchaseOrder, String purchaseOrderItem, String supplier,
                         long creationDate, String material, String purchaseOrderItemText,
                         String plant, long deliveryDate, String unit, double orderQuantity,
                         double openQuantity, String storageLocation, boolean batchFlag,
                         String model, String serialFlag, boolean poReturnInd) {
        this.purchaseOrder = purchaseOrder;
        this.purchaseOrderItem = purchaseOrderItem;
        this.supplier = supplier;
        this.creationDate = creationDate;
        this.material = material;
        this.purchaseOrderItemText = purchaseOrderItemText;
        this.plant = plant;
        this.deliveryDate = deliveryDate;
        this.unit = unit;
        this.orderQuantity = orderQuantity;
        this.openQuantity = openQuantity;
        this.storageLocation = storageLocation;
        this.purchaseOrderItemNr = Integer.valueOf(purchaseOrderItem);
        this.batchFlag = batchFlag;
        this.model = model;
        this.serialFlag = serialFlag;
        this.poReturnInd = poReturnInd;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public String getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    public String getSupplier() {
        return supplier;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public String getMaterial() {
        return material;
    }

    public String getPurchaseOrderItemText() {
        return purchaseOrderItemText;
    }

    public String getPlant() {
        return plant;
    }

    public long getDeliveryDate() {
        return deliveryDate;
    }

    public String getUnit() {
        return unit;
    }

    public double getOrderQuantity() {
        return orderQuantity;
    }

    public double getOpenQuantity() {
        return openQuantity;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getBatch() {
        return batch == null? "": batch;
    }

    public void setOpenQuantity(double openQuantity) {
        this.openQuantity = openQuantity;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getQuantityInEntryUnit() {
        if(quantityInEntryUnit == null){
            quantityInEntryUnit = "";
        }
        if(StringUtils.isNotEmpty(quantityInEntryUnit)){
            if(Integer.valueOf(quantityInEntryUnit) <= 0){
                quantityInEntryUnit = "";
            }
        }
        return quantityInEntryUnit;
    }

    public double getScanQuantity(){
        if(StringUtils.isEmpty(quantityInEntryUnit)){
            return 0;
        }else{
            return Double.valueOf(quantityInEntryUnit);
        }
    }

    public void setQuantityInEntryUnit(String quantityInEntryUnit) {
        this.quantityInEntryUnit = quantityInEntryUnit;
    }

    public boolean isSubOrder() {
        return subOrder;
    }

    public void setSubOrder(boolean subOrder) {
        this.subOrder = subOrder;
    }

    public String getId(){
        return purchaseOrder + purchaseOrderItem;
    }

    public Integer getPurchaseOrderItemNr() {
        return purchaseOrderItemNr;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public void setPurchaseOrderItem(String purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setPurchaseOrderItemText(String purchaseOrderItemText) {
        this.purchaseOrderItemText = purchaseOrderItemText;
    }

    public void setPlant(String plant) {
        this.plant = plant;
    }

    public void setDeliveryDate(long deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setOrderQuantity(double orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public void setPurchaseOrderItemNr(int purchaseOrderItemNr) {
        this.purchaseOrderItemNr = purchaseOrderItemNr;
    }

    public boolean isBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(boolean batchFlag) {
        this.batchFlag = batchFlag;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public int compareTo(PurchaseOrder o) {
        return this.purchaseOrderItem.compareTo(o.getPurchaseOrderItem());
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public List<String> getSnList() {
        return snList;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }

    public String getMaterialDocument() {
        return materialDocument;
    }

    public void setMaterialDocument(String materialDocument) {
        this.materialDocument = materialDocument;
    }

    public String getMaterialDocumentItem() {
        return materialDocumentItem;
    }

    public void setMaterialDocumentItem(String materialDocumentItem) {
        this.materialDocumentItem = materialDocumentItem;
    }

    public String getMovementType() {
        return movementType;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public String getDebitCreditind() {
        return debitCreditind;
    }

    public void setDebitCreditind(String debitCreditind) {
        this.debitCreditind = debitCreditind;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isPoReturnInd() {
        return poReturnInd;
    }
}
