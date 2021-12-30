package com.sagisu.vault.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.ui.home.HomeFragment;
import com.sagisu.vault.ui.transfer.TransferToAccountFragment;
import com.sagisu.vault.ui.transfer.TransferToAccountViewModel;
import com.sagisu.vault.ui.transfer.TransferToSelfFragment;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.TransferTypeDescriptor;
import com.sagisu.vault.utils.Util;

public class TransferToAccountActivity extends AppCompatActivity implements TransferToAccountFragment.ITransferToAccount, TransferToSelfFragment.ITransferToSelf {

    Fragment fragment;
    private TransferToAccountViewModel mViewModel;
    private String transferType;
    ActivityResultLauncher<Intent> plaidActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            String publicToken = data.getStringExtra(PlaidLinkActivity.BUNDLE_PUBLIC_TOKEN);
                            String institutionName = data.getStringExtra(PlaidLinkActivity.BUNDLE_INSTITUTION_NAME);

                            if (fragment instanceof TransferToAccountFragment)
                                ((TransferToAccountFragment) fragment).setPublicToken(publicToken, institutionName);
                            else if (fragment instanceof TransferToSelfFragment)
                                ((TransferToSelfFragment) fragment).setPublicToken(publicToken, institutionName);
                        }
                    }else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            String errorMsg = data.getStringExtra(PlaidLinkActivity.BUNDLE_ERROR_MESSAGE);
                            Util.showSnackBar(errorMsg,TransferToAccountActivity.this);
                            //Toast.makeText(TransferToAccountActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_to_account_activity);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        setActionBarTitle("Transfer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            loading("Fetching bank accounts", false);
            mViewModel = new ViewModelProvider(this).get(TransferToAccountViewModel.class);
            mViewModel.setTotalWalletBalance(getIntent().getDoubleExtra(HomeFragment.BUNDLE_WALLET_BALANCE, 0));
            transferType = getIntent().getStringExtra(HomeFragment.BUNDLE_TRANSFER_TYPE);
            mViewModel.setTransferType(transferType);
            if (transferType == null || transferType.equals(TransferTypeDescriptor.TO_ACCOUNT)) {
                fragment = TransferToAccountFragment.newInstance(this);
            } else {
                fragment = TransferToSelfFragment.newInstance(this);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow();

            mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String txt) {
                    if (txt == null) loading(null, false);
                    else loading(txt, true);
                }
            });

        }

    }

    @Override
    public void launchPlaid() {
        Intent plaidLinkIntent = new Intent(TransferToAccountActivity.this, PlaidLinkActivity.class);
        plaidActivityResultLauncher.launch(plaidLinkIntent);
    }

    @Override
    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading), findViewById(R.id.container), text, loading);
        //circularProgressIndicator.show();
        //findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }
}