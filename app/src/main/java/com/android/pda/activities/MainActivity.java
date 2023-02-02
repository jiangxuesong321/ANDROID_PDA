package com.android.pda.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.pda.R;
import com.android.pda.activities.view.NoticeDialog;
import com.android.pda.adapters.MenuItemAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.application.AppConstants;
import com.android.pda.controllers.LoginController;
import com.android.pda.controllers.OfflineController;
import com.android.pda.controllers.PrintController;
import com.android.pda.controllers.UserController;
import com.android.pda.database.pojo.Login;
import com.android.pda.database.pojo.Offline;
import com.android.pda.database.pojo.User;
import com.android.pda.log.LogUtils;
import com.android.pda.models.MenuList;
import com.android.pda.utils.FileUtil;
import com.android.pda.utils.Util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description MenuList 展示菜单主界面
 */

public class MainActivity extends AppCompatActivity implements ActivityInitialization, AdapterView.OnItemClickListener {
    private final static AndroidApplication app = AndroidApplication.getInstance();
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
                } else {
                    LogUtils.setLogDir(FileUtil.getSDPath() + "/Pda/Log");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 用户校验不存在则转到 LoginActivity 用户登录
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1234);
        } else {
            Login login = loginController.getLoginUser();
            if (login != null) {
                // 登陆状态，显示菜单页面
                List<String> funcList = Util.splitCode(loginController.getLoginUser().getZfunc());
                initData(funcList);

            } else {
                // 登出状态，显示登录页面
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


    /**
     * 配置 Adapter，绘制对应 FUNC 主菜单页面
     * @param funcList
     */
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

    /**
     * 菜单点击触发事件，跳转对应 Activity
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String funcId = menuList.getMenuItemList().get(position).getId();

        switch (funcId) {
            case AppConstants.FUNCTION_ID_BASE_DATA: //基础数据
                startActivity(MasterDataActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_SALES_INVOICE: //销售发货
                if (user == null) {
                    user = userController.getLoginUser();
                }
                if (user != null) {
                    if (StringUtils.equalsIgnoreCase(user.getGroup(), app.getString(R.string.text_company_name_c))) {
//                        startActivity(SalesInvoiceListHomeActivity.createIntent(getApplicationContext()));
                    } else {
                        // 组织 A 同 组织 D
//                        startActivity(SalesInvoiceHomeActivity.createIntent(getApplicationContext()));
                    }
                } else {
                    displayDialog(getString(R.string.text_update_user_data), AppConstants.REQUEST_STAY, 1);
                }
                break;
            case AppConstants.FUNCTION_ID_PURCHASE_ORDER: //采购收货
                startActivity(PoHomeActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_LEND: //借出发货
                startActivity(PrototypeBorrowHomeActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_PICKING_MATERIAL: //领料单发料
                startActivity(PickingHomeActivity.createIntent(getApplicationContext(), 4));
                break;
            case AppConstants.FUNCTION_ID_PICKING: //借机领用
                startActivity(PickingHomeActivity.createIntent(getApplicationContext(), 5));
                break;
            case AppConstants.FUNCTION_ID_STOCK_MOVE: //库存转移
                offlineStockTransfer = offlineController.getOfflineData(AppConstants.FUNCTION_ID_STOCK_MOVE);
                //for offline
                if (offlineStockTransfer != null) {
                    displayDialog(getString(R.string.text_offline_warning), AppConstants.REQUEST_OFFLINE_DATA, 2);
                } else {
                    startActivity(StockTransferActivity.createIntent(getApplicationContext(), null));
                }
                break;
            case AppConstants.FUNCTION_ID_TRANSFER_OUT: //调拨发出
                startActivity(TransferOrderHomeActivity.createIntent(getApplicationContext(), AppConstants.FUNCTION_ID_TRANSFER_OUT));
                break;
            case AppConstants.FUNCTION_ID_TRANSFER_IN: //调拨接收
                startActivity(TransferOrderHomeActivity.createIntent(getApplicationContext(), AppConstants.FUNCTION_ID_TRANSFER_IN));
                break;
            case AppConstants.FUNCTION_ID_SN_SEARCH: //序列号查询
                startActivity(SnSearchActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_SN_REPORT: //出入库报表
                startActivity(InOutboundReportActivity.createIntent(getApplicationContext()));
                break;
            case AppConstants.FUNCTION_ID_PRINT_MANUAL:
                startActivity(PrintDetailActivity.createIntent(getApplicationContext(), null, ""));
                break;
            case AppConstants.FUNCTION_ID_PRINT_SHIPPING_LABEL:  //发货标签打印
                startActivity(PrintHomeActivity.createIntent(getApplicationContext(), PrintController.SHIPPING_LABEL));
                break;
            case AppConstants.FUNCTION_ID_PRINT_RECEIVE_LABEL:   //收货标签打印
                startActivity(PrintHomeActivity.createIntent(getApplicationContext(), PrintController.RECEIVE_LABEL));
                break;
            case AppConstants.FUNCTION_ID_D66_TEST:
                startActivity(D66TestActivity.createIntent(getApplicationContext(), AppConstants.FUNCTION_ID_D66_TEST));
        }
    }

    /**
     * 主菜单界面退出登录
     * @param item
     * @return
     */
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

    /**
     * 构建菜单样式 & 对应用户 ID 展示
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        //menu.add(userController.getLoginUser().getUserId());
        MenuItem menuItemLayout = menu.findItem(R.id.action_menu_user);
        View view = menuItemLayout.getActionView();
        TextView textView = view.findViewById(R.id.tv_user);
        //System.out.println("Id---->" +userController.getLoginUser().getUserId());
        if (userController.getLoginUser() != null) {
            textView.setText(userController.getLoginUser().getUserId());
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 更新 / 退出 / 暂存 Dialog
     * @param message
     * @param action
     * @param buttonCount
     */
    private void displayDialog(String message, int action, int buttonCount) {
        NoticeDialog noticeDialog = new NoticeDialog(this, message, buttonCount);
        //for offline
        if (AppConstants.REQUEST_OFFLINE_DATA == action) {
            noticeDialog.setPositiveButtonText(getString(R.string.text_continue));
            noticeDialog.setNegativeButtonText(getString(R.string.text_discard_offline_data));
        }
        noticeDialog.setButtonCallback(new NoticeDialog.ButtonCallback() {
            @Override
            public void callOk() {
                //for offline
                if (AppConstants.REQUEST_OFFLINE_DATA == action) {
                    startActivity(StockTransferActivity.createIntent(app, offlineStockTransfer));
                }
                // 退出登录
                if (AppConstants.REQUEST_LOGOUT == action) {
                    loginController.deleteLoginUser();
                    startActivity(LoginActivity.createIntent(getApplicationContext()));
                    finish();
                }
            }

            @Override
            public void callClose() {
                //for offline
                if (AppConstants.REQUEST_OFFLINE_DATA == action) {
                    offlineController.clearOfflineData(AppConstants.FUNCTION_ID_STOCK_MOVE);
                    startActivity(StockTransferActivity.createIntent(app, null));
                }
            }
        });
        noticeDialog.create();

    }

}
