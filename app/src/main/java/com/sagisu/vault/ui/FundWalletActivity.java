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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.ui.fundwallet.FundWalletFragment;
import com.sagisu.vault.ui.fundwallet.FundWalletViewModel;
import com.sagisu.vault.ui.home.HomeFragment;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

public class FundWalletActivity extends AppCompatActivity {

    private FundWalletViewModel mViewModel;

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
                            //fragment.setPublicToken(publicToken);
                            mViewModel.exchangeForAccessToken(publicToken, institutionName);
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // There are no request codes
                        Intent data = result.getData();
                        if (data != null) {
                            String errorMsg = data.getStringExtra(PlaidLinkActivity.BUNDLE_ERROR_MESSAGE);
                            Util.showSnackBar(errorMsg, FundWalletActivity.this);
                            //Toast.makeText(FundWalletActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.fund_wallet_activity);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        setActionBarTitle("Fund Wallet");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            mViewModel = new ViewModelProvider(this).get(FundWalletViewModel.class);
            mViewModel.checkUserStatus(new SharedPref(getApplicationContext()).getUser());
            mViewModel.setTotalWalletBalance(getIntent().getDoubleExtra(HomeFragment.BUNDLE_WALLET_BALANCE, 0));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FundWalletFragment.newInstance())
                    .commitNow();

            mViewModel.getLaunchPlaid().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean == null) return;
                    if (aBoolean) {
                        launchPlaid();
                        mViewModel.setLaunchPlaid(null);
                    }
                }
            });

            mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String txt) {
                    if (txt == null) loading(null, false);
                    else loading(txt, true);
                }
            });
        }
    }

    public void launchPlaid() {
        Intent plaidLinkIntent = new Intent(FundWalletActivity.this, PlaidLinkActivity.class);
        plaidActivityResultLauncher.launch(plaidLinkIntent);
    }

    public void loading(String text, boolean loading) {
        // findViewById(R.id.loading).setVisibility(loading ? View.VISIBLE : View.GONE);
        // findViewById(R.id.container).setVisibility(loading ? View.GONE : View.VISIBLE);
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading), findViewById(R.id.container), text, loading);

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