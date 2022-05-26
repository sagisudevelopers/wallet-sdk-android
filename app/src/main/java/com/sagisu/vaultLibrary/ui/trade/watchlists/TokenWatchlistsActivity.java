package com.sagisu.vaultLibrary.ui.trade.watchlists;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.utils.AppManager;

public class TokenWatchlistsActivity extends AppCompatActivity {

    public static final String BUNDLE_COINS = "coins_selected";
    TokenWatchListsViewModel mViewModel;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.token_watchlists_activity);
        AppManager.getAppManager().addActivity(this);
        mViewModel = new ViewModelProvider(this).get(TokenWatchListsViewModel.class);
        extractBundleData();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TokenWatchlistsFragment.newInstance())
                    .commitNow();
        }
    }

    private void extractBundleData() {
        if (getIntent() != null) {
            Coins coinSymbol = getIntent().getParcelableExtra(BUNDLE_COINS);
            mViewModel.coinSelected.setValue(coinSymbol);
        }
    }
}