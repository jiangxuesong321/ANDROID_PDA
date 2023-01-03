package com.android.pda.models;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderSubContract implements Serializable, Comparable<PurchaseOrderSubContract>, Cloneable{

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
    private double componentQty;
    private double ComponentQtyConsumed;
    private double grquantity;
    private String component;
    private String componentDesc;

    private boolean subOrder;

    private boolean batchFlag;
    private String model;

    private boolean isSet = false;

    private String serialFlag;
    private List<String> snList = new ArrayList<>();

    private String reason;
    private String sn;

    private List<Component> components;
    public PurchaseOrderSubContract() {
    }

    public PurchaseOrderSubContract(String purchaseOrder, String purchaseOrderItem, String supplier,
                                    long creationDate, String material, String purchaseOrderItemText,
                                    String plant, long deliveryDate, String unit, double orderQuantity,
                                    double openQuantity, String storageLocation, boolean batchFlag,
                                    String model, String serialFlag, double componentQty,
                                    double ComponentQtyConsumed, String component, double grquantity,
                                    String componentDesc) {
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

        this.batchFlag = batchFlag;
        this.model = model;
        this.serialFlag = serialFlag;
        this.componentQty = componentQty;
        this.ComponentQtyConsumed = ComponentQtyConsumed;
        this.component = component;
        this.grquantity = grquantity;
        this.componentDesc = componentDesc;
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
    public int compareTo(PurchaseOrderSubContract o) {
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

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String groupItem(){
        return material + "-" + batch;
    }
    public String groupMaterial(){
        return purchaseOrderItem + "-" + material;
    }
    @Override
    public Object clone() {
        PurchaseOrderSubContract o = null;
        try{
            o = (PurchaseOrderSubContract)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
    public String getParentItem(){
        return purchaseOrder + "-" + purchaseOrderItem + "-" + material + "-"+ String.valueOf(isSubOrder());
    }

    public String getItems(){
        return purchaseOrder + "-" + purchaseOrderItem + "-" + material;
    }

    public double getComponentQty() {
        return componentQty;
    }

    public double getComponentQtyConsumed() {
        return ComponentQtyConsumed;
    }

    public String getComponent() {
        return component;
    }

    public double getGrquantity() {
        return grquantity;
    }

    public List<Component> getComponents() {
        return components;
    }

    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public String getComponentDesc() {
        return componentDesc;
    }

    public void setComponentDesc(String componentDesc) {
        this.componentDesc = componentDesc;
    }
}
