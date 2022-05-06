package com.sagisu.vaultLibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.ui.trade.SelectCoinsFragment;
import com.sagisu.vaultLibrary.ui.trade.receive.ReceiveCryptoFragment;
import com.sagisu.vaultLibrary.ui.trade.receive.ReceiveCryptoViewModel;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;
import com.sagisu.vaultLibrary.utils.Util;

public class ReceiveCryptoActivity extends AppCompatActivity implements SelectCoinsFragment.ICoinSelectionListener {
    public static final String BUNDLE_COIN_SELECTED = "bundle_coin_selected";
    private ReceiveCryptoViewModel mViewModel;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        loading(null, false);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            mViewModel.goBack(false);
        } else mViewModel.goBack(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Coins coins = intent.getParcelableExtra(BUNDLE_COIN_SELECTED);
        if (coins != null) {
            mViewModel.setNeedCoinSelection(false);
            onCoinSelected(coins);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_crypto_activity);
        AppManager.getAppManager().addActivity(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mViewModel = new ViewModelProvider(this).get(ReceiveCryptoViewModel.class);
        onNewIntent(getIntent());
        mViewModel.getAddPage().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, SelectCoinsFragment.newInstance((Util.TradeTypes) getIntent().getSerializableExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE), null), "SelectToken")
                                .commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.container, ReceiveCryptoFragment.newInstance())
                                .commit();
                        setActionBarTitle("Receive crypto");

                        break;
                }
            }
        });
        /*mViewModel.getQrCodeUrl().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap s) {
                if (s != null) {
                    mViewModel.nextPage();
                }
            }
        });*/

        mViewModel.getLoadingObservable().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String txt) {
                if (txt == null) loading(null, false);
                else loading(txt, true);
            }
        });
    }

    @Override
    public void onCoinSelected(Coins coins) {
        mViewModel.setCoins(coins);
        mViewModel.nextPage();
        //mViewModel.receiveCrypto();
    }

    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(findViewById(R.id.loading_receive), findViewById(R.id.container), text, loading);
    }
}