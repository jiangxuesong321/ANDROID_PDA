package com.sunmi.pda.models;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderGi implements Serializable, Comparable<PurchaseOrderGi>, Cloneable{

    private String orderNum;
    private String orderItem;
    private String material;
    private String materialDesc;
    private String productionOrder;

    private String plant;
    private String storageLocation;
    private String deletionFlag;

    private String finalIssueFlag;
    private String batch;
    private double requireQty;
    private double withdrawnQty;
    private double qty;

    private String movementType;

    private String bomItemNo;
    private String quantityInEntryUnit;

    private boolean subOrder;
    private int purchaseOrderItemNr;
    private boolean batchFlag;
    private String model;

    private boolean isSet = false;

    private String serialFlag;
    private List<String> snList = new ArrayList<>();
    private String sn;
    public PurchaseOrderGi() {
    }

    public PurchaseOrderGi(String orderNum, String orderItem, String material, String productionOrder,
                           String plant, String storageLocation, String deletionFlag, String finalIssueFlag,
                           String batch, double requireQty, double withdrawnQty, double qty, String movementType,
                           String bomItemNo, boolean batchFlag, String serialFlag, String materialDesc) {
        this.orderNum = orderNum;
        this.orderItem = orderItem;
        this.material = material;
        this.productionOrder = productionOrder;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.deletionFlag = deletionFlag;
        this.finalIssueFlag = finalIssueFlag;
        this.batch = batch;
        this.requireQty = requireQty;
        this.withdrawnQty = withdrawnQty;
        this.qty = qty;
        this.movementType = movementType;
        this.bomItemNo = bomItemNo;
        this.batchFlag = batchFlag;
        this.serialFlag = serialFlag;
        this.materialDesc = materialDesc;
    }

    public String getPlant() {
        return plant;
    }

    public String getMaterialDesc() {
        return materialDesc;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getBatch() {
        return batch == null? "": batch;
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
        return orderNum + orderItem + material;
    }


    public void setPlant(String plant) {
        this.plant = plant;
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
    public int compareTo(PurchaseOrderGi o) {
        return this.material.compareTo(o.getMaterial());
    }

    public String getOrderNum() {
        return orderNum;
    }

    public String getOrderItem() {
        return orderItem;
    }

    public String getMaterial() {
        return material;
    }

    public String getProductionOrder() {
        return productionOrder;
    }

    public String getDeletionFlag() {
        return deletionFlag;
    }

    public String getFinalIssueFlag() {
        return finalIssueFlag;
    }

    public double getRequireQty() {
        return requireQty;
    }

    public double getWithdrawnQty() {
        return withdrawnQty;
    }

    public double getQty() {
        return qty;
    }

    public String getMovementType() {
        return movementType;
    }

    public String getBomItemNo() {
        return bomItemNo;
    }

    public int getPurchaseOrderItemNr() {
        return purchaseOrderItemNr;
    }

    public boolean isSet() {
        return isSet;
    }

    public String getSerialFlag() {
        return serialFlag;
    }

    public List<String> getSnList() {
        return snList;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public void setOrderItem(String orderItem) {
        this.orderItem = orderItem;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setProductionOrder(String productionOrder) {
        this.productionOrder = productionOrder;
    }

    public void setDeletionFlag(String deletionFlag) {
        this.deletionFlag = deletionFlag;
    }

    public void setFinalIssueFlag(String finalIssueFlag) {
        this.finalIssueFlag = finalIssueFlag;
    }

    public void setRequireQty(double requireQty) {
        this.requireQty = requireQty;
    }

    public void setWithdrawnQty(double withdrawnQty) {
        this.withdrawnQty = withdrawnQty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public void setBomItemNo(String bomItemNo) {
        this.bomItemNo = bomItemNo;
    }

    public void setSet(boolean set) {
        isSet = set;
    }

    public void setSerialFlag(String serialFlag) {
        this.serialFlag = serialFlag;
    }

    public void setSnList(List<String> snList) {
        this.snList = snList;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public Object clone() {
        PurchaseOrderGi o = null;
        try{
            o = (PurchaseOrderGi)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public String groupItem(){
        return material + "-" + batch;
    }

    public String getParentItem(){
        return orderNum + "-" + orderItem + "-" + material + "-"+ String.valueOf(isSubOrder());
    }
    public String getItems(){
        return orderNum + "-" + orderItem + "-" + material;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }
}
