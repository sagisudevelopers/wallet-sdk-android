package com.sagisu.vault.ui.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.SplashScreenActivityBinding;
import com.sagisu.vault.ui.MainActivity;
import com.sagisu.vault.ui.login.LoginActivity;
import com.sagisu.vault.utils.SharedPref;


public class SplashScreenActivity extends AppCompatActivity {

    private SplashScreenActivityBinding binding;
    private SplashScreenViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);
        viewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);
        viewModel.init();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.getLoadNextScreen().observe(this, loadNextScreen -> {
            if (loadNextScreen) {
                loadNextScreen();
            }
        });
    }

    private void loadNextScreen() {
        if (new SharedPref(getApplicationContext()).getToken() == null) {
            loadLoginActivity();
        } else {
            loadMainActivity();
        }
    }

    private void loadLoginActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadMainActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
