package com.sunmi.pda.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.sunmi.pda.R;
import com.sunmi.pda.application.SunmiApplication;

import org.apache.commons.lang3.StringUtils;


public class AppUtil {

	private static final String PROPERTY_APP_SERVICE_HOST = "serviceHost";

	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER = "PurchaseOrder";
	public static final String PROPERTY_LAST_INPUT_PROTOTYPE_BORROW = "PrototypeBorrow";
	public static final String PROPERTY_LAST_INPUT_SALES_INVOICE = "SalesInvoice";
	public static final String PROPERTY_LAST_INPUT_PICKING = "Picking";//材料领用
	public static final String PROPERTY_LAST_INPUT_PICKING_MATERIAL = "PickingMaterial";//借机领用
	public static final String PROPERTY_LAST_INPUT_TRANSFER_ORDER = "TransferOrder";
	public static final String PROPERTY_LAST_INPUT_TRANSFER_ORDER_RECEIVE = "TransferOrderReceive";

	public static final String PROPERTY_LAST_CHANGE_DATE_MATERIAL = "LastChangeDateMaterial";
	public static final String PROPERTY_LAST_CHANGE_DATE_LOCATION = "LastChangeDateLocation";
	public static final String PROPERTY_LAST_CHANGE_DATE_USER = "LastChangeDateUser";
	public static final String PROPERTY_LAST_CHANGE_DATE_LOGISTICS = "LastChangeDateLogistics";

	public static final String PROPERTY_LAST_INPUT_SHIPPING_LABEL = "ShippingLabel";
	public static final String RECEIVE_LABEL_INPUT_RECEIVE_LABEL = "ReceiveLabel";

	public static final String PROPERTY_LAST_INPUT_PRINT_IP = "PrintIp";

	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER_RETURN = "PurchaseOrderReturn";

	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_OUT = "PurchaseOrderSubOut";
	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER_SUBCONTRACT_IN = "PurchaseOrderSubIn";

	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER_GI = "PurchaseOrder_Gi";
	public static final String PROPERTY_LAST_INPUT_PURCHASE_ORDER_PGR = "PurchaseOrder_Pgr";


	private static final String PREFERENCES = "com.sunmi.pda";
	private static final SunmiApplication app = SunmiApplication.getInstance();
	
	public static void saveServiceHost(Context context, String host){
		SharedPreferences.Editor editor = getVersionPreferences(context).edit();
		editor.putString(PROPERTY_APP_SERVICE_HOST, host);
		editor.commit();
	}

	public static void saveLastChangeDate(Context context, String key, String value){
		SharedPreferences.Editor editor = getVersionPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getLastChangeDate(Context context, String key){
		String oldVersionString = getVersionPreferences(context).getString(key, "");
		return oldVersionString;
	}
	
	public static String getServiceHost(Context context){
		String url = context.getString(R.string.sap_url_host_q);
		String oldVersionString = "";
		if(StringUtils.equalsIgnoreCase("P", context.getString(R.string.default_environment))){
			oldVersionString = getVersionPreferences(context).getString(PROPERTY_APP_SERVICE_HOST,  app.getString(R.string.sap_url_host));
		}else{
			oldVersionString = getVersionPreferences(context).getString(PROPERTY_APP_SERVICE_HOST,  app.getString(R.string.sap_url_host_q));
		}
		return oldVersionString;
	}

	public static void saveLastInput(Context context, String key, String value){
		SharedPreferences.Editor editor = getVersionPreferences(context).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getLastInput(Context context, String key){
		String oldVersionString = getVersionPreferences(context).getString(key, "");
		return oldVersionString;
	}
	
	private static SharedPreferences getVersionPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Coult not get package name: " + e);
        }
    }


	/**
	 * [获取应用程序版本名称信息]
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static synchronized String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}
}
