package com.sagisu.vault.ui.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.VaultSplashScreenActivityBinding;
import com.sagisu.vault.ui.VaultMainActivity;
import com.sagisu.vault.ui.login.VaultLoginActivity;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.SharedPref;


public class VaultSplashScreenActivity extends AppCompatActivity {

    private VaultSplashScreenActivityBinding binding;
    private VaultSplashScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vault_splash_screen);
        viewModel = new ViewModelProvider(this).get(VaultSplashScreenViewModel.class);
        viewModel.init();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getLoadNextScreen().observe(this, loadNextScreen -> {
            if (loadNextScreen) {
                loadNextScreen();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    private void loadNextScreen() {
        if (new SharedPref().getToken() == null) {
            loadLoginActivity();
        } else {
            loadMainActivity();
        }
    }

    private void loadLoginActivity() {
        Intent intent = new Intent(VaultSplashScreenActivity.this, VaultLoginActivity.class);
        intent.putExtra(Intent.EXTRA_INTENT, getIntent());
        startActivity(intent);
        finish();
    }

    private void loadMainActivity() {
        Intent intent = new Intent(VaultSplashScreenActivity.this, VaultMainActivity.class);
        startActivity(intent);
        finish();
    }
}
