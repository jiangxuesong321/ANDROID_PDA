package com.sunmi.pda.models;

import com.alibaba.fastjson.annotation.JSONField;

public class SalesInvoicePostingRequest {
    @JSONField(name="VBELN") //发货单号
    private String vBELN;
    @JSONField(name="POSNR") //发货单行项目
    private String pOSNR;
    @JSONField(name="MATNR")
    private String mATNR;
    @JSONField(name="LGORG")  //库存地点
    private String gORG;

    @JSONField(name="CHARG")  //批次号
    private String cHARG;
    @JSONField(name="MENGE")  //批次数量
    private String mENGE;
    @JSONField(name="SERNR")  //序列号
    private String sERNR;

    @JSONField(name="LogisticsProvider")  //序列号
    private String logisticsProvider;
    @JSONField(name="LogisticsNumber")  //序列号
    private String logisticsNumber;

    //1-销售发货,2-样机借用发货,3-领用发货,4-样机领用,5-调拨发出,6-调拨接收,7-采购收货,8-借机还回
    @JSONField(name="DocumentType")  //单据类型
    private String documentType;

    @JSONField(name="BUDAT")  //过账日期
    private String bUDAT;

    public SalesInvoicePostingRequest() {
    }

    public SalesInvoicePostingRequest(String vBELN, String pOSNR, String mATNR, String gORG,
                                      String cHARG, String mENGE, String sERNR, String bUDAT) {
        this.vBELN = vBELN;
        this.pOSNR = pOSNR;
        this.mATNR = mATNR;
        this.gORG = gORG;
        this.cHARG = cHARG;
        this.mENGE = mENGE;
        this.sERNR = sERNR;
        this.bUDAT = bUDAT;
    }

    public String getvBELN() {
        return vBELN;
    }

    public String getpOSNR() {
        return pOSNR;
    }

    public String getmATNR() {
        return mATNR;
    }

    public String getgORG() {
        return gORG;
    }

    public String getcHARG() {
        return cHARG;
    }

    public String getmENGE() {
        return mENGE;
    }

    public String getsERNR() {
        return sERNR;
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

    public void setLogisticsProvider(String logisticsProvider) {
        this.logisticsProvider = logisticsProvider;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getbUDAT() {
        return bUDAT;
    }

    public void setbUDAT(String bUDAT) {
        this.bUDAT = bUDAT;
    }
}
