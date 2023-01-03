package com.android.pda.models;




public class SapError {
    Error error;

    public SapError() {
    }

    public SapError(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}
