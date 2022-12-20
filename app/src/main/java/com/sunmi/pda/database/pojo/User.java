package com.sunmi.pda.database.pojo;

public class User {
    private String userId;
    private String userName;
    private String group;
    private String depart;

    public User(String userId, String userName, String group, String depart) {
        this.userId = userId;
        this.userName = userName;
        this.group = group;
        this.depart = depart;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getGroup() {
        return group;
    }

    public String getDepart() {
        return depart;
    }
}
