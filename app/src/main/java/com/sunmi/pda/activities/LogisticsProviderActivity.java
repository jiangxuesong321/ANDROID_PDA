package com.sunmi.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.sunmi.pda.R;
import com.sunmi.pda.adapters.LogisticsProviderAdapter;
import com.sunmi.pda.adapters.MaterialAdapter;
import com.sunmi.pda.application.SunmiApplication;
import com.sunmi.pda.controllers.LogisticsProviderController;
import com.sunmi.pda.controllers.MaterialController;
import com.sunmi.pda.database.pojo.LogisticsProvider;
import com.sunmi.pda.database.pojo.Material;

import java.util.List;

public class LogisticsProviderActivity extends AppCompatActivity implements ActivityInitialization{
    private final static SunmiApplication app = SunmiApplication.getInstance();
    private static final LogisticsProviderController controller = app.getLogisticsProviderController();
    private List<LogisticsProvider> logisticsProviders;
    private LogisticsProviderAdapter adapter;
    private ListView lvLogisticsProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logistics_provider);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        showLogisticsProvider();
    }

    private void showLogisticsProvider() {
        adapter = new LogisticsProviderAdapter(getApplicationContext(), logisticsProviders);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvLogisticsProvider.setDividerHeight(1);
        this.lvLogisticsProvider.setAdapter(adapter);
        //this.lvPurchase.setOnItemClickListener(this);
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, LogisticsProviderActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }
    @Override
    public void initView() {
        lvLogisticsProvider = findViewById(R.id.lv_logistics_provider);

    }

    @Override
    public void initData() {
        logisticsProviders = controller.getLogisticsProvider();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}