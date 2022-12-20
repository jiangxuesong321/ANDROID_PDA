package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ComponentItem {

    @JSONField(name="Components")
    private String Components;

    @JSONField(name="Componentsqty")
    private String Components_qty;

    @JSONField(name="GoodsMovementType")
    private String GoodsMovementType;

    @JSONField(name="Specialstockint")
    private String Specialstockint;

    public ComponentItem() {
    }

    public ComponentItem(String components, String components_qty, String goodsMovementType, String specialstockint) {
        Components = components;
        Components_qty = components_qty;
        GoodsMovementType = goodsMovementType;
        Specialstockint = specialstockint;
    }

    public String getComponents() {
        return Components;
    }

    public String getComponents_qty() {
        return Components_qty;
    }

    public String getGoodsMovementType() {
        return GoodsMovementType;
    }

    public String getSpecialstockint() {
        return Specialstockint;
    }
}
