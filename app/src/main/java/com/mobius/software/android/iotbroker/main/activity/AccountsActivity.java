package com.mobius.software.android.iotbroker.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mobius.software.android.iotbroker.main.adapters.AccountAdapter;
import com.mobius.software.android.iotbroker.main.adapters.ButtonListener;
import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.base.DaoObject;
import com.mobius.software.android.iotbroker.main.dal.Accounts;
import com.mobius.software.android.iotbroker.main.dal.AccountsDao;
import com.mobius.software.android.iotbroker.main.dal.DaoType;
import com.mobius.software.iotbroker.androidclient.R;

import java.io.Serializable;
import java.util.List;

public class AccountsActivity extends Activity implements ButtonListener, AdapterView.OnItemClickListener, View.OnClickListener {

    static final Integer ACCOUNTS_ACTIVITY_CODE = 1291;
    static final Integer ACCOUNTS_ACTIVITY_SELECT_ITEM_RESULT_CODE = 12910;
    static final Integer ACCOUNTS_ACTIVITY_DELETE_ITEM_RESULT_CODE = 12911;

    static final String ACCOUNTS_ACTIVITY_ITEM_POSITION_PARAMETER = "ACCOUNTS_ACTIVITY_ITEM_POSITION_PARAMETER";

    private AccountAdapter adapter;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        Serializable accounts = getIntent().getSerializableExtra(ApplicationSettings.PARAM_ACCOUNTS);

        this.adapter = new AccountAdapter(this);
        this.adapter.setListener(this);
        this.adapter.setAccounts(accounts);

        this.loginButton = findViewById(R.id.btn_login);
        this.loginButton.setOnClickListener(this);

        ListView listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setAdapter(this.adapter);
    }

    @Override
    public void buttonPressed(int position) {
        this.adapter.removeItem(position);
        Intent intent = new Intent();
        intent.putExtra(ACCOUNTS_ACTIVITY_ITEM_POSITION_PARAMETER, position);
        setResult(ACCOUNTS_ACTIVITY_DELETE_ITEM_RESULT_CODE, intent);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.putExtra(ACCOUNTS_ACTIVITY_ITEM_POSITION_PARAMETER, i);
        setResult(ACCOUNTS_ACTIVITY_SELECT_ITEM_RESULT_CODE, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(this.loginButton)) {
            finish();
        }
    }

}
