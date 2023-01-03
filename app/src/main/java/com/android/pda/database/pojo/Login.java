package com.android.pda.database.pojo;

public class Login {
    private String zuid;
    private String zfunc;
    private String zfactory;
    private String zstore_loc;
    private String zuname;
    private String zujson;

    public Login() {
    }

    public Login(String zuid, String zfunc, String zfactory, String zstore_loc, String zuname, String zujson) {
        this.zuid = zuid;
        this.zfunc = zfunc;
        this.zfactory = zfactory;
        this.zstore_loc = zstore_loc;
        this.zuname = zuname;
        this.zujson = zujson;
    }

    public String getZuid() {
        return zuid;
    }

    public String getZfunc() {
        return zfunc;
    }

    public String getZfactory() {
        return zfactory;
    }

    public String getZstore_loc() {
        return zstore_loc;
    }

    public String getZuname() {
        return zuname;
    }

    public String getZujson() {
        return zujson;
    }

    public void setZujson(String zujson) {
        this.zujson = zujson;
    }

    public void setZuid(String zuid) {
        this.zuid = zuid;
    }

    public void setZfunc(String zfunc) {
        this.zfunc = zfunc;
    }

    public void setZfactory(String zfactory) {
        this.zfactory = zfactory;
    }

    public void setZstore_loc(String zstore_loc) {
        this.zstore_loc = zstore_loc;
    }

    public void setZuname(String zuname) {
        this.zuname = zuname;
    }
}
