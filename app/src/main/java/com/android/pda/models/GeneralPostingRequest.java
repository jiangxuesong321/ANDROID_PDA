package com.android.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class GeneralPostingRequest {
    @JSONField(name="DocumentDate")
    private String documentDate;
    @JSONField(name="PostingDate")
    private String postingDate;
    @JSONField(name="GoodsMovementCode")
    private String goodsMovementCode;
    @JSONField(name="MaterialDocumentHeaderText")
    private String materialDocumentHeaderText;
    @JSONField(name="LogisticsProvider")
    private String logisticsProvider;
    @JSONField(name="LogisticsNumber")
    private String logisticsNumber;
    @JSONField(name="DocumentType")
    private String documentType;
    @JSONField(name="to_MaterialDocumentItem")
    private GeneralMaterialDocumentItem to_MaterialDocumentItem;

    public GeneralPostingRequest() {
    }

    public GeneralPostingRequest(String documentDate, String postingDate, String goodsMovementCode,
                                 String materialDocumentHeaderText, String logisticsProvider,
                                 String logisticsNumber, String documentType, GeneralMaterialDocumentItem to_MaterialDocumentItem) {

        this.documentDate = documentDate;
        this.postingDate = postingDate;
        this.goodsMovementCode = goodsMovementCode;
        this.materialDocumentHeaderText = materialDocumentHeaderText;
        this.logisticsProvider = logisticsProvider;
        this.logisticsNumber = logisticsNumber;
        this.documentType = documentType;
        this.to_MaterialDocumentItem = to_MaterialDocumentItem;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public String getGoodsMovementCode() {
        return goodsMovementCode;
    }

    public GeneralMaterialDocumentItem getTo_MaterialDocumentItem() {
        return to_MaterialDocumentItem;
    }

    public String getMaterialDocumentHeaderText() {
        return materialDocumentHeaderText;
    }

    public String getLogisticsProvider() {
        return logisticsProvider;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public String getDocumentType() {
        return documentType;
    }
}
