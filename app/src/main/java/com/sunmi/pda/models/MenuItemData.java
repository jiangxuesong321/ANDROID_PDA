package com.sunmi.pda.models;

public class MenuItemData {
    private String id;
    private String menuName;

    public MenuItemData(String id, String menuName) {
        this.id = id;
        this.menuName = menuName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }
}
