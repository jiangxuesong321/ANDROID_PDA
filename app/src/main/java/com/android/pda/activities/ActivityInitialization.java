package com.android.pda.activities;

public interface ActivityInitialization {
    void initView();
    void initData();
    void initService();
    void initListener();
    void initIntent();
}
