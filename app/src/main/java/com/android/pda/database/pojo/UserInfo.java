package com.android.pda.database.pojo;

public class UserInfo {
    private String userId;
    private String userName;
    private String apiKey;

    public UserInfo(String userId, String userName, String apiKey) {
        this.userId = userId;
        this.userName = userName;
        this.apiKey = apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
