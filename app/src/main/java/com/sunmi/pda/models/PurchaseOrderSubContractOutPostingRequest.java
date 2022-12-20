package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class PurchaseOrderSubContractOutPostingRequest {
    @JSONField(name="data")
    private List<PurchaseOrderSubContractOutPosting> postingList;


    public PurchaseOrderSubContractOutPostingRequest() {
    }

    public PurchaseOrderSubContractOutPostingRequest(List<PurchaseOrderSubContractOutPosting> postingList) {
        this.postingList = postingList;
    }

    public List<PurchaseOrderSubContractOutPosting> getPostingList() {
        return postingList;
    }

    public void setPostingList(List<PurchaseOrderSubContractOutPosting> postingList) {
        this.postingList = postingList;
    }
}
