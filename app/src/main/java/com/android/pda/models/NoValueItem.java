package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class NoValueItem {
    @JSONField(name="Material")
    private String material;
    @JSONField(name="Batch")
    private String batch;
    @JSONField(name="Plant")
    private String plant;
    @JSONField(name="StorageLocation")
    private String storageLocation;
    @JSONField(name="IssuingOrReceivingStorageLoc")
    private String receivingStorageLoc;
    @JSONField(name="GoodsMovementRefDocType")
    private String goodsMovementRefDocType;
    @JSONField(name="GoodsMovementType")
    private String goodsMovementType;
    @JSONField(name="QuantityInEntryUnit")
    private String quantityInEntryUnit;
    @JSONField(name="MaterialDocumentItemText")
    private String materialDocumentItemText;

    @JSONField(name="to_SerialNumbers")
    private SerialNumberResults serialNumber;

    public NoValueItem(String material, String batch, String plant, String storageLocation, String receivingStorageLoc, String goodsMovementRefDocType, String goodsMovementType, String quantityInEntryUnit, String materialDocumentItemText, List<String> sNumbers) {
        this.material = material;
        this.batch = batch;
        this.plant = plant;
        this.storageLocation = storageLocation;
        this.receivingStorageLoc = receivingStorageLoc;
        this.goodsMovementRefDocType = goodsMovementRefDocType;
        this.goodsMovementType = goodsMovementType;
        this.quantityInEntryUnit = quantityInEntryUnit;
        this.materialDocumentItemText = materialDocumentItemText;
        this.serialNumber = new SerialNumberResults(sNumbers);
    }

    public String getMaterial() {
        return material;
    }

    public String getBatch() {
        return batch;
    }

    public String getPlant() {
        return plant;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public String getReceivingStorageLoc() {
        return receivingStorageLoc;
    }

    public String getGoodsMovementRefDocType() {
        return goodsMovementRefDocType;
    }

    public String getGoodsMovementType() {
        return goodsMovementType;
    }

    public String getQuantityInEntryUnit() {
        return quantityInEntryUnit;
    }

    public String getMaterialDocumentItemText() {
        return materialDocumentItemText;
    }

    public SerialNumberResults getSerialNumber() {
        return serialNumber;
    }

    public class SerialNumberResults {
        @JSONField(name="results")
        private List<SerialNumber> result= new ArrayList<>();

        public SerialNumberResults(List<String> sNumbers) {
            List<SerialNumber> _sn = new ArrayList<>();
            for (String _n: sNumbers) {
                _sn.add(new SerialNumber(_n));
            }
            this.result = _sn;
        }

        public List<SerialNumber> getResult() {
            return result;
        }
    }

    public class SerialNumber {
        @JSONField(name="SerialNumber")
        private String serialNumber;

        public SerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getSerialNumber() {
            return serialNumber;
        }
    }
}
