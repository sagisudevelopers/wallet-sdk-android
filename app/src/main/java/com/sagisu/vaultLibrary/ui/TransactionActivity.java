package com.sagisu.vaultLibrary.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.ui.transactions.TransactionHistoryFragment;
import com.sagisu.vaultLibrary.utils.AppManager;


public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_activity);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TransactionHistoryFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }
}