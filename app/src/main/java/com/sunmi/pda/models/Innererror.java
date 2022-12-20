package com.sunmi.pda.models;



import java.util.List;


public class Innererror {
    private List<ErrorDetail> errordetails;

    public Innererror() {
    }

    public Innererror(List<ErrorDetail> errordetails) {
        this.errordetails = errordetails;
    }

    public List<ErrorDetail> getErrordetails() {
        return errordetails;
    }

    public void setErrordetails(List<ErrorDetail> errordetails) {
        this.errordetails = errordetails;
    }
}
