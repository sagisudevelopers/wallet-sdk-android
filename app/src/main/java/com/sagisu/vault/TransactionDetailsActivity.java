package com.sagisu.vault;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.ui.transactions.TransactionDetailsFragment;
import com.sagisu.vault.ui.transactions.TransactionDetailsViewModel;
import com.sagisu.vault.utils.AppManager;

public class TransactionDetailsActivity extends AppCompatActivity {

    public static final String BUNDLE_TRANSACTION  = "bundle_transaction";
    private TransactionDetailsViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_details_activity);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        if (savedInstanceState == null) {
            setActionBarTitle("Payment details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mViewModel = new ViewModelProvider(this).get(TransactionDetailsViewModel.class);
            Transaction transaction = getIntent().getParcelableExtra(BUNDLE_TRANSACTION);
            mViewModel.setTransactionData(transaction);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TransactionDetailsFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}