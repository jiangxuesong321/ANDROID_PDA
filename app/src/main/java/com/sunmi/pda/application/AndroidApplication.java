package com.sunmi.pda.application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;

import com.sunmi.pda.R;
import com.sunmi.pda.broadcastreceivers.AlarmsBroadcastReceiver;
import com.sunmi.pda.controllers.BatchStockController;
import com.sunmi.pda.controllers.InOutboundReportController;
import com.sunmi.pda.controllers.LendBackController;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.NoValueController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.OrderInvoiceOthersController;
import com.sunmi.pda.controllers.PickingController;
import com.sunmi.pda.controllers.PrintController;
import com.sunmi.pda.controllers.PrototypeBorrowController;
import com.sunmi.pda.controllers.PurchaseOrderGiController;
import com.sunmi.pda.controllers.PurchaseOrderGrController;
import com.sunmi.pda.controllers.PurchaseOrderSubContractController;
import com.sunmi.pda.controllers.SalesInvoiceController;
import com.sunmi.pda.controllers.ScanController;
import com.sunmi.pda.controllers.SerialInfoController;
import com.sunmi.pda.controllers.StockTransferController;
import com.sunmi.pda.controllers.StorageLocationController;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.controllers.PurchaseOrderController;
import com.sunmi.pda.controllers.TransferOrderController;
import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.controllers.MaterialVoucherController;
import com.sunmi.pda.database.DatabaseService;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.OdataService;
import com.sunmi.pda.utils.AppUtil;
import com.sunmi.pda.utils.DateUtils;
import com.sunmi.pda.utils.FileUtil;

import java.lang.reflect.Field;
import java.util.Calendar;

public class AndroidApplication extends Application {

    private static AndroidApplication instance;
    private DatabaseService databaseService;
    private OdataService odataService;
    private static StorageLocationController storageLocationController;
    private static LogisticsProviderController logisticsProviderController;
    private static MaterialController materialController;
    private static PurchaseOrderController poController;
    private static BatchStockController batchStockController;
    private static LoginController loginController;
    private static SalesInvoiceController salesInvoiceController;
    private static ScanController scanController;
    private static UserController userController;
    private static PrototypeBorrowController prototypeBorrowController;
    private static PickingController pickingController;
    private static OfflineController offlineController;
    private static LendBackController lendBackController;
    private static InOutboundReportController inOutboundReportController;
    private static PrintController printController;
    private static NoValueController noValueController;
    private static PurchaseOrderSubContractController purchaseOrderSubContractController;
    private static PurchaseOrderGiController purchaseOrderGiController;
    private static PurchaseOrderGrController purchaseOrderGrController;
    public final static int REQUESTCODE_MATERIAL_SYNC = 7700;
    private boolean alarmSet = false;

    public static PurchaseOrderGrController getPurchaseOrderGrController() {
        if (purchaseOrderGrController == null) {
            purchaseOrderGrController = new PurchaseOrderGrController();
        }
        return purchaseOrderGrController;
    }

    public static PurchaseOrderGiController getPurchaseOrderGiController() {
        if (purchaseOrderGiController == null) {
            purchaseOrderGiController = new PurchaseOrderGiController();
        }
        return purchaseOrderGiController;
    }

    public static PurchaseOrderSubContractController getPurchaseOrderSubContractController() {
        if (purchaseOrderSubContractController == null) {
            purchaseOrderSubContractController = new PurchaseOrderSubContractController();
        }
        return purchaseOrderSubContractController;
    }

    public static PrintController getPrintController() {
        if (printController == null) {
            printController = new PrintController();
        }
        return printController;
    }

    public static InOutboundReportController getInOutboundReportController() {
        if (inOutboundReportController == null) {
            inOutboundReportController = new InOutboundReportController();
        }
        return inOutboundReportController;
    }

    public static LendBackController getLendBackController() {
        if (lendBackController == null) {
            lendBackController = new LendBackController();
        }
        return lendBackController;
    }

    public static OfflineController getOfflineController() {
        if (offlineController == null) {
            offlineController = new OfflineController();
        }
        return offlineController;
    }
    private static MaterialVoucherController materialVoucherController;
    private static StockTransferController stockTransferController;
    private static SerialInfoController serialInfoController;


    public static PickingController getPickingController(){
        if (pickingController == null) {
            pickingController = new PickingController();
        }
        return pickingController;
    }
    private static TransferOrderController transferOrderController;

    public static PrototypeBorrowController getPrototypeBorrowController(){
        if (prototypeBorrowController == null) {
            prototypeBorrowController = new PrototypeBorrowController();
        }
        return prototypeBorrowController;
    }


    public static SerialInfoController getSerialInfoController(){
        if (serialInfoController == null) {
            serialInfoController = new SerialInfoController();
        }
        return serialInfoController;
    }

    public static StockTransferController getStockTransferController(){
        if (stockTransferController == null) {
            stockTransferController = new StockTransferController();
        }
        return stockTransferController;
    }

    public static MaterialVoucherController getMaterialVoucherController(){
        if (materialVoucherController == null) {
            materialVoucherController = new MaterialVoucherController();
        }
        return materialVoucherController;
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

    public PurchaseOrderController getPurchaseOrderController() {
        if (poController == null) {
            poController = new PurchaseOrderController();
        }
        return poController;
    }

    public BatchStockController getBatchStockController() {
        if (batchStockController == null) {
            batchStockController = new BatchStockController();
        }
        return batchStockController;
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

    public SalesInvoiceController getSalesInvoiceController() {
        if (salesInvoiceController == null) {
            salesInvoiceController = new SalesInvoiceController();
        }
        return salesInvoiceController;
    }

    public TransferOrderController getTransferOrderController() {
        if (transferOrderController == null) {
            transferOrderController = new TransferOrderController();
        }
        return transferOrderController;
    }

    public NoValueController getNoValueController() {
        if (noValueController == null) {
            noValueController = new NoValueController();
        }
        return noValueController;
    }
    private static OrderInvoiceOthersController orderInvoiceOthersController;
    public OrderInvoiceOthersController getOrderInvoiceOthersController() {
        if (orderInvoiceOthersController == null) {
            orderInvoiceOthersController = new OrderInvoiceOthersController();
        }
        return orderInvoiceOthersController;
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
        LogUtils.setLogDir(FileUtil.getSDPath() + "/Sunmi/Log");
    }

    public void setAlarm() {
        // setup of Alarm for sync MasterData
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.set(Calendar.SECOND, 00);
        cal.set(Calendar.MILLISECOND, 00);

        LogUtils.d("SunmiApplication", "Alarm set at: " + DateUtils.dateToString(cal.getTime(), DateUtils.FormatFullDate));

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
