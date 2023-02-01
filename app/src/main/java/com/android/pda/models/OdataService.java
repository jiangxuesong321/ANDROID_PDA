package com.android.pda.models;



public class OdataService {
    private String host;
    private String userName;
    private String password;

    public OdataService(String host, String userName, String password) {
        this.host = host;
        this.userName = userName;
        this.password = password;
    }

    public String getHost() {
        //host = "https://xxx.xxx.com:44300";
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
