package com.sunmi.pda.database;

import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.log.LogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

public class DatabaseServiceMaterial implements DatabaseConstants{

    private final DatabaseHelper dbHelper;
    private final static SunmiApplication app = SunmiApplication.getInstance();
    protected static final String TAG = DatabaseServiceMaterial.class.getSimpleName();

    public DatabaseServiceMaterial() {
        this.dbHelper = new DatabaseHelper();
    }

    public void createData(List<Material> materials){
        PojoDBObjectHelper.update(dbHelper.material, materials);
        LogUtils.d(TAG, "Created Material master....");

    }

    public Material getDataByKey(String material){
        return PojoDBObjectHelper.fetchById(dbHelper.material, new String[] { material});
    }

    public Material getDataByBarCode(String barCode){
        List<Material> _materials = PojoDBObjectHelper.fetchByColumnConstraint(dbHelper.material, "barCode", barCode);
        return _materials.size() > 0 ? _materials.get(0) : null;
    }

    public List<Material> getAllData(){
        return PojoDBObjectHelper.fetchAllInList(dbHelper.material);
    }

    public void createData(Material material){
        PojoDBObjectHelper.createOrUpdate(dbHelper.material, material);
        LogUtils.d(TAG, "Created material ....");
    }

    public void deleteDataByKey(String id){
        PojoDBObjectHelper.delete(
                dbHelper.material,
                MATERIAL_MATERIAL + " = ?  ",
                new String[] { id }
        );
    }
    public void deleteData(){
        PojoDBObjectHelper.deleteAll(dbHelper.material);
    }
    public List<Material> getAllData(String material){
        if(material != null){
            material = material.toUpperCase();
        }
        String sql = "select * from " + TABLE_MATERIAL + " where Material like '" +  material + "%' order by Material";
        if(StringUtils.isEmpty(material)){
            sql = "select * from " + TABLE_MATERIAL + " order by Material";
        }
        String[] selectionArg = null;
        List <Material> list = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.material, sql, selectionArg);
        return list;
    }

    public int getMaterialCount(){
        String sql = "select * from " + TABLE_MATERIAL + " limit 1 ";
        String[] selectionArg = null;
        List <Material> list = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.material, sql, selectionArg);
        int count = list.size();
        return count;
    }
}
