package com.android.pda.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;


import com.android.pda.R;

import com.android.pda.adapters.UserAdapter;
import com.android.pda.application.AndroidApplication;

import com.android.pda.controllers.UserController;

import com.android.pda.database.pojo.User;

import java.util.List;

public class UserActivity extends AppCompatActivity implements ActivityInitialization{
    private final static AndroidApplication app = AndroidApplication.getInstance();
    private static final UserController controller = app.getUserController();
    private List<User> users;
    private UserAdapter adapter;
    private ListView lvUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        initData();
        showStorageLocation();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, UserActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        return intent;
    }

    private void showStorageLocation() {
        adapter = new UserAdapter(getApplicationContext(), users);
        //this.lvPurchase.setDivider(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.colorDivider)));
        this.lvUser.setDividerHeight(1);
        this.lvUser.setAdapter(adapter);
        //this.lvPurchase.setOnItemClickListener(this);
    }

    @Override
    public void initView() {
        lvUser = findViewById(R.id.lv_user);
    }

    @Override
    public void initData() {
        users = controller.getUsers();
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