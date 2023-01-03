package com.android.pda.listeners;

public interface OnTaskEventListener<T> {
    public void onSuccess(T object);
    public void onFailure(String error);
    public void bindModel(Object o);

}
