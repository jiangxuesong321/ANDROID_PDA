package com.sunmi.pda.database.pojo;

public class LogisticsProvider {
    private String ztId;
    private String ztName;

    public LogisticsProvider(String ztId, String ztName) {
        this.ztId = ztId;
        this.ztName = ztName;
    }

    public String getZtId() {
        return ztId;
    }

    public String getZtName() {
        return ztName;
    }
}
