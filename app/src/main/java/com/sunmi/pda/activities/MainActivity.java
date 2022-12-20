package com.sunmi.pda.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import com.sunmi.pda.R;
import com.sunmi.pda.activities.view.NoticeDialog;
import com.sunmi.pda.adapters.MenuItemAdapter;

import com.sunmi.pda.application.AppConstants;
import com.sunmi.pda.application.SunmiApplication;

import com.sunmi.pda.controllers.LoginController;
import com.sunmi.pda.controllers.OfflineController;
import com.sunmi.pda.controllers.PrintController;
import com.sunmi.pda.controllers.UserController;
import com.sunmi.pda.database.pojo.Login;
import com.sunmi.pda.database.pojo.Offline;
import com.sunmi.pda.database.pojo.User;
import com.sunmi.pda.log.LogUtils;
import com.sunmi.pda.models.MenuList;
import com.sunmi.pda.utils.FileUtil;
import com.sunmi.pda.utils.PrintUtil;
import com.sunmi.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ActivityInitialization, AdapterView.OnItemClickListener{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final LoginController loginController = app.getLoginController();
    private static final UserController userController = app.getUserController();
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1234;
    private User user;
    //for offline
    private static final OfflineController offlineController = app.getOfflineController();
    Offline offlineStockTransfer;

    private GridView gvMenu;
    private MenuList menuList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }else{
                    LogUtils.setLogDir(FileUtil.getSDPath() + "/Sunmi/Log");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1234);
        }else{
            Login login = loginController.getLoginUser();
            if(login != null){
                //登陆状态，显示菜单页面。
                List<String> funcList = Util.splitCode(loginController.getLoginUser().getZfunc());
                initData(funcList);

            }else{
                //登出状态，显示登录页面。
                startActivity(LoginActivity.createIntent(getApplicationContext()));
                finish();
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void initView() {
        gvMenu = findViewById(R.id.gv_menu);
    }

    @Override
    public void initData() {
        user = userController.getLoginUser();
    }


    public void initData(List<String> funcList) {
        menuList = userController.getUserMenuList(getApplicationContext());
        MenuItemAdapter adapter = new MenuItemAdapter(this, menuList.getMenuItemList());
        gvMenu.setAdapter(adapter);
        gvMenu.setOnItemClickListener(this);
    }

    @Override
    public void initService() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initIntent() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String funcId = menuList.getMenuItemList().get(position).getId();

        switch (funcId){
            case AppConstants.FUNCTION_ID_BASE_DATA : //基础数据
                startActivity(MasterDataActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_SALES_INVOICE : //销售发货
                if (user == null) {
                    user = userController.getLoginUser();
                }
                if (user != null){
                    if (StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_chuantian) )){
                        startActivity(SalesInvoiceListHomeActivity.createIntent(getApplicationContext()));
                    } else {
                        // 商米 同 沃有
                        startActivity(SalesInvoiceHomeActivity.createIntent(getApplicationContext()));
                    }
                } else {
                    displayDialog(getString(R.string.text_update_user_data), AppConstants.REQUEST_STAY, 1);
                }
                break;
            case AppConstants.FUNCTION_ID_PURCHASE_ORDER : //采购收货
                startActivity(PoHomeActivity.createIntent(getApplicationContext()));
                break;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        displayDialog(getString(R.string.text_confirm_logout), AppConstants.REQUEST_LOGOUT, 2);

        return super.onOptionsItemSelected(item);

    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        //menu.add(userController.getLoginUser().getUserId());
        MenuItem menuItemLayout = menu.findItem(R.id.action_menu_user);
        View view  = menuItemLayout.getActionView();
        TextView textView = view.findViewById(R.id.tv_user);
        //System.out.println("Id---->" +userController.getLoginUser().getUserId());
        if(userController.getLoginUser() != null){
            textView.setText(userController.getLoginUser().getUserId());
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void displayDialog(String message, int action, int buttonCount){
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        //for offline
        if(AppConstants.REQUEST_OFFLINE_DATA == action){
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    startActivity(StockTransferActivity.createIntent(app, offlineStockTransfer));
                }
                if(AppConstants.REQUEST_LOGOUT == action) {
                    loginController.deleteLoginUser();
                    startActivity(LoginActivity.createIntent(getApplicationContext()));
                    finish();
                }
            }

            @Override
            public void callClose() {
                //for offline
                if(AppConstants.REQUEST_OFFLINE_DATA == action){
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_STOCK_MOVE);
                    startActivity(StockTransferActivity.createIntent(app, null));
                }
            }
        });
        noticeDialog.create();

    }

}
