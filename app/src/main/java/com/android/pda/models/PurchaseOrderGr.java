package com.android.pda.models;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderGr implements Serializable, Comparable<PurchaseOrderGr>, Cloneable{

    private String productionNo;
    private String productionItem;
    private String material;
    private String materialDesc;


    private String plant;
    private String storageLocation;
    private boolean completion;


    private String batch;
    private double totalQty;
    private double grQty;
    private double qty;
    private boolean batchFlag;
    private String serialFlag;

    private String quantityInEntryUnit;

    private boolean subOrder;
    private int purchaseOrderItemNr;

    private String model;

    private boolean isSet = false;


    private List<String> snList = new ArrayList<>();
    private String sn;
    public PurchaseOrderGr() {
    }

    public PurchaseOrderGr(String productionNo, String productionItem, String material, String materialDesc,
                           String plant, String storageLocation, boolean completion, String batch,
                           double totalQty, double grQty, double qty, boolean batchFlag, String serialFlag) {
        this.productionNo = productionNo;
        this.productionItem = productionItem;
        this.material = material;
        this.materialDesc = materialDesc;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.completion = completion;
        this.batch = batch;
        this.totalQty = totalQty;
        this.grQty = grQty;
        this.qty = qty;
        this.batchFlag = batchFlag;
        this.serialFlag = serialFlag;
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
        return productionNo + productionItem + material;
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
    public int compareTo(PurchaseOrderGr o) {
        return this.material.compareTo(o.getMaterial());
    }



    public String getMaterial() {
        return material;
    }






    public double getQty() {
        return qty;
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

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setQty(double qty) {
        this.qty = qty;
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
        PurchaseOrderGr o = null;
        try{
            o = (PurchaseOrderGr)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    public String groupItem(){
        return material + "-" + batch;
    }

    public String getParentItem(){
        return productionNo + "-" + productionItem + "-" + material + "-"+ String.valueOf(isSubOrder());
    }
    public String getItems(){
        return productionNo + "-" + productionItem + "-" + material;
    }

    public void setMaterialDesc(String materialDesc) {
        this.materialDesc = materialDesc;
    }

    public String getProductionNo() {
        return productionNo;
    }

    public void setProductionNo(String productionNo) {
        this.productionNo = productionNo;
    }

    public String getProductionItem() {
        return productionItem;
    }

    public void setProductionItem(String productionItem) {
        this.productionItem = productionItem;
    }

    public boolean isCompletion() {
        return completion;
    }

    public void setCompletion(boolean completion) {
        this.completion = completion;
    }

    public double getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(double totalQty) {
        this.totalQty = totalQty;
    }

    public double getGrQty() {
        return grQty;
    }

    public void setGrQty(double grQty) {
        this.grQty = grQty;
    }
}
