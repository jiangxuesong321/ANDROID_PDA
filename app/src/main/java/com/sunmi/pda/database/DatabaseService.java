package com.sunmi.pda.database;


import com.delawareconsulting.libtools.database.PojoDBObjectHelper;
import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Material;
import com.sunmi.pda.database.pojo.StorageLocation;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class DatabaseService implements DatabaseConstants {
	// database helper
	private final DatabaseHelper dbHelper;
	private final static SunmiApplication app = SunmiApplication.getInstance();
    protected static final String TAG = DatabaseService.class.getSimpleName();


	/**
	 * Constructor
	 * 
	 * @param
	 */
	public DatabaseService() {
		this.dbHelper = new DatabaseHelper();
		this.databaseServiceStorageLocation = new DatabaseServiceStorageLocation();
		this.databaseServiceLogisticsProvider = new DatabaseServiceLogisticsProvider();
		this.databaseServiceMaterial = new DatabaseServiceMaterial();
		this.databaseServiceLogin = new DatabaseServiceLogin();
		this.databaseServiceUser = new DatabaseServiceUser();
		this.databaseServiceOffline = new DatabaseServiceOffline();
	}

	public DatabaseServiceStorageLocation databaseServiceStorageLocation;
	public DatabaseServiceLogisticsProvider databaseServiceLogisticsProvider;
	public DatabaseServiceMaterial databaseServiceMaterial;
	public DatabaseServiceLogin databaseServiceLogin;
	public DatabaseServiceUser databaseServiceUser;
	public DatabaseServiceOffline databaseServiceOffline;

	public DatabaseServiceStorageLocation getDatabaseServicePlant() {
		return databaseServiceStorageLocation;
	}

	public DatabaseServiceLogisticsProvider getDatabaseServiceLogisticsProvider() {
		return databaseServiceLogisticsProvider;
	}

	public DatabaseServiceLogin getDatabaseServiceLogin() {
		return databaseServiceLogin;
	}

	public DatabaseServiceUser getDatabaseServiceUser() {
		return databaseServiceUser;
	}

	public DatabaseServiceMaterial getDatabaseServiceMaterial() {
		return databaseServiceMaterial;
	}

	public DatabaseServiceOffline getDatabaseServiceOffline(){
		return databaseServiceOffline;
	}

	public void clearDatabase() {
		dbHelper.deleteAll();
	}

	public void createLogin(Login login) {
		PojoDBObjectHelper.createOrUpdate(dbHelper.login, login);
	}

	public void deleteLogin(){
		PojoDBObjectHelper.deleteAll(dbHelper.login);
	}

	public List<Login> getLoginWithSql(){
		String sql = "select * from " + TABLE_LOGIN;
		String[] selectionArg = null;
		List<Login> loginList = PojoDBObjectHelper.fetchByCustomQuery(dbHelper.login, sql, selectionArg);
		return loginList;
	}

}