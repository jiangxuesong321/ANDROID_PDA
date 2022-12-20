package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class GeneralMaterialDocumentItem {

    @JSONField(name="results")
    private List<GeneralMaterialDocumentItemResults> results;

    public GeneralMaterialDocumentItem() {
    }

    public GeneralMaterialDocumentItem(List<GeneralMaterialDocumentItemResults> results) {
        this.results = results;
    }

    public List<GeneralMaterialDocumentItemResults> getResults() {
        return results;
    }
}
