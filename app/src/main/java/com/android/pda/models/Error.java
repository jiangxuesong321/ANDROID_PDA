package com.android.pda.models;





public class Error {
    private Innererror innererror;
    private Message message;

    public Error() {
    }

    public Error(Innererror innererror, Message message) {
        this.innererror = innererror;
        this.message = message;
    }

    public Innererror getInnererror() {
        return innererror;
    }

    public void setInnererror(Innererror innererror) {
        this.innererror = innererror;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
