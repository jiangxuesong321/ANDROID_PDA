package com.android.pda.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;

import com.android.pda.R;
import com.android.pda.broadcastreceivers.AlarmsBroadcastReceiver;
import com.android.pda.controllers.LogisticsProviderController;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.POStorageController;
import com.android.pda.controllers.ProductionStorageController;
import com.android.pda.controllers.ScanController;
import com.android.pda.controllers.StorageLocationController;
import com.android.pda.controllers.MaterialController;
import com.android.pda.controllers.LoginController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.DatabaseService;
import com.android.pda.log.LogUtils;
import com.android.pda.models.OdataService;
import com.android.pda.utils.AppUtil;
import com.android.pda.utils.DateUtils;
import com.android.pda.utils.FileUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

public class AndroidApplication extends Application {

    private static AndroidApplication instance;
    private DatabaseService databaseService;
    private OdataService odataService;
    private static StorageLocationController storageLocationController;
    private static LogisticsProviderController logisticsProviderController;
    private static MaterialController materialController;
    private static LoginController loginController;
    private static ScanController scanController;
    private static UserController userController;
    private static OfflineController offlineController;
    private static POStorageController poStorageController;
    private static ProductionStorageController productionStorageController;
    public final static int REQUESTCODE_MATERIAL_SYNC = 7700;
    private boolean alarmSet = false;

    public static OfflineController getOfflineController() {
        if (offlineController == null) {
            offlineController = new OfflineController();
        }
        return offlineController;
    }

    public static ScanController getScanController(){
        if (scanController == null) {
            scanController = new ScanController();
        }
        return scanController;
    }

    public static LogisticsProviderController getLogisticsProviderController() {
        if (logisticsProviderController == null) {
            logisticsProviderController = new LogisticsProviderController();
        }
        return logisticsProviderController;
    }

    public StorageLocationController getStorageLocationController() {
        if (storageLocationController == null) {
            storageLocationController = new StorageLocationController();
        }
        return storageLocationController;
    }

    public MaterialController getMaterialController() {
        if (materialController == null) {
            materialController = new MaterialController();
        }
        return materialController;
    }

    public LoginController getLoginController() {
        if (loginController == null) {
            loginController = new LoginController();
        }
        return loginController;
    }

    public UserController getUserController() {
        if (userController == null) {
            userController = new UserController();
        }
        return userController;
    }

    public POStorageController getPoStorageController() {
        if (poStorageController == null) {
            poStorageController = new POStorageController();
        }
        return poStorageController;
    }

    public ProductionStorageController getProductionStorageController() {
        if (productionStorageController == null) {
            productionStorageController = new ProductionStorageController();
        }
        return productionStorageController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (!alarmSet) {
            setAlarm();
        }
        //CrashHandler.getInstance().initCrashHandler();
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.setLogDir(FileUtil.getSDPath() + "/Pda/Log");
    }

    /**
     * 定时广播更新主数据
     */
    public void setAlarm() {
        // setup of Alarm for sync MasterData
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        LogUtils.d("Application", "Alarm set at: " + DateUtils.dateToString(cal.getTime(), DateUtils.FormatFullDate));

        Intent intent = new Intent(AndroidApplication.getInstance(), AlarmsBroadcastReceiver.class);
        int RequestCode = REQUESTCODE_MATERIAL_SYNC;
        intent.putExtra("RequestCode", RequestCode);
        PendingIntent sender = PendingIntent.getBroadcast(AndroidApplication.getInstance(), RequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) AndroidApplication.getInstance().getSystemService(Context.ALARM_SERVICE);
        //am.cancel(sender);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_HALF_DAY, sender);

        alarmSet = true;
    }

    public static AndroidApplication getInstance() {
        return instance;
    }

    public OdataService getOdataService() {
        if(odataService == null){
            odataService = new OdataService(AppUtil.getServiceHost(instance),
                    instance.getString(R.string.sap_username), instance.getString(R.string.sap_password));
        }else{
            odataService.setHost(AppUtil.getServiceHost(instance));
        }
        return odataService;
    }

    public DatabaseService getDBService() {
        if (databaseService == null) {
            databaseService = new DatabaseService();
        }
        return databaseService;
    }
}
