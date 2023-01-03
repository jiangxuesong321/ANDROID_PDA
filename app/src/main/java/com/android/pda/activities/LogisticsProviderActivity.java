package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.pda.R;
import com.android.pda.adapters.LogisticsProviderAdapter;
import com.android.pda.application.AndroidApplication;
import com.android.pda.controllers.LogisticsProviderController;
import com.android.pda.database.pojo.LogisticsProvider;

import java.util.List;

public class LogisticsProviderActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
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