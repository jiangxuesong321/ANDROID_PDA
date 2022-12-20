package com.sunmi.pda.models;

import java.io.Serializable;
import java.util.List;

public class PrintLabel implements Serializable {
    String PurchaseDocument;
    String PurchaseDocumentItem;
    String Material;
    String MaterialDes;
    String Batch;
    String Spec;
    String Model;
    String UnitDesciption;
    String Quantity;
    String PrintDate;
    String SalesDocument;
    String CustomerMaterial;
    String Remark;

    public PrintLabel() {
    }

    public PrintLabel(String purchaseDocument, String purchaseDocumentItem, String material, String materialDes,
                      String batch, String spec, String model, String unitDesciption, String quantity,
                      String printDate, String salesDocument, String customerMaterial, String remark) {
        PurchaseDocument = purchaseDocument;
        PurchaseDocumentItem = purchaseDocumentItem;
        Material = material;
        MaterialDes = materialDes;
        Batch = batch;
        Spec = spec;
        Model = model;
        UnitDesciption = unitDesciption;
        Quantity = quantity;
        PrintDate = printDate;
        SalesDocument = salesDocument;
        CustomerMaterial = customerMaterial;
        Remark = remark;
    }

    public String getPurchaseDocument() {
        return PurchaseDocument;
    }

    public void setPurchaseDocument(String purchaseDocument) {
        PurchaseDocument = purchaseDocument;
    }

    public String getPurchaseDocumentItem() {
        return PurchaseDocumentItem;
    }

    public void setPurchaseDocumentItem(String purchaseDocumentItem) {
        PurchaseDocumentItem = purchaseDocumentItem;
    }

    public String getMaterial() {
        return Material;
    }

    public void setMaterial(String material) {
        Material = material;
    }

    public String getMaterialDes() {
        return MaterialDes;
    }

    public void setMaterialDes(String materialDes) {
        MaterialDes = materialDes;
    }

    public String getBatch() {
        return Batch;
    }

    public void setBatch(String batch) {
        Batch = batch;
    }

    public String getSpec() {
        return Spec;
    }

    public void setSpec(String spec) {
        Spec = spec;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getUnitDesciption() {
        return UnitDesciption;
    }

    public void setUnitDesciption(String unitDesciption) {
        UnitDesciption = unitDesciption;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrintDate() {
        return PrintDate;
    }

    public void setPrintDate(String printDate) {
        PrintDate = printDate;
    }

    public String getSalesDocument() {
        return SalesDocument;
    }

    public void setSalesDocument(String salesDocument) {
        SalesDocument = salesDocument;
    }

    public String getCustomerMaterial() {
        return CustomerMaterial;
    }

    public void setCustomerMaterial(String customerMaterial) {
        CustomerMaterial = customerMaterial;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}

