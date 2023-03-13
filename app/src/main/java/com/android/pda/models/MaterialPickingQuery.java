package com.android.pda.models;

import com.android.pda.database.pojo.Material;

import java.util.List;

public class MaterialPickingQuery {
    private List<Material> materialList;

    public MaterialPickingQuery(List<Material> materialList) {
        this.materialList = materialList;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void setMaterialList(List<Material> materialList) {
        this.materialList = materialList;
    }
}
