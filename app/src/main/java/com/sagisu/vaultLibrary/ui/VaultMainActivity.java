package com.sagisu.vaultLibrary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.MainActivityLayoutBinding;
import com.sagisu.vaultLibrary.ui.home.HomeFragment;
import com.sagisu.vaultLibrary.ui.home.HomeViewModel;
import com.sagisu.vaultLibrary.ui.home.JoinWaitListBottomDialogFragment;
import com.sagisu.vaultLibrary.ui.home.TradeHomeFragment;
import com.sagisu.vaultLibrary.ui.profile.ProfileFragment;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.SharedPref;

public class VaultMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAB_HOME = "home";
    private static final String TAB_PROFILE = "profile";
    private static final String TAB_TRADE = "trade";

    private String tabClicked = "";
    private MainActivityLayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vault_main);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout_container, TradeHomeFragment.newInstance())
                    .commitNow();
        }*/
        binding.home.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
        binding.trade.setOnClickListener(this);
        if (getIntent().getData() == null) findViewById(R.id.trade).performClick();
        else {
            binding.home.performClick();
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //CLear the business selection and set to null
        new SharedPref().setBusinessVaultSelected(null);
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        int id = view.getId();
        if (id == R.id.home) {
            if (tabClicked.equals(TAB_HOME)) return;
            else {
                tabClicked = TAB_HOME;
                binding.homeTxt.setTextColor(getResources().getColor(R.color.vaultColorPrimary));
                binding.profileTxt.setTextColor(getResources().getColor(R.color.tabInActive));

                binding.homeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_active));
                binding.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_inactive));

                fragment = HomeFragment.newInstance();
            }
        } else if (id == R.id.profile) {
            if (tabClicked.equals(TAB_PROFILE)) return;
            else {
                tabClicked = TAB_PROFILE;
                binding.homeTxt.setTextColor(getResources().getColor(R.color.tabInActive));
                binding.profileTxt.setTextColor(getResources().getColor(R.color.vaultColorPrimary));


                binding.homeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_inactive));
                binding.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_active));

                fragment = ProfileFragment.newInstance();
            }


        } else if (id == R.id.trade) {
            if (tabClicked.equals(TAB_TRADE)) return;
            //showJoinWaitlist(FeatureNameDescriptor.TRADE);
            tabClicked = TAB_TRADE;
            binding.homeTxt.setTextColor(getResources().getColor(R.color.tabInActive));
            binding.profileTxt.setTextColor(getResources().getColor(R.color.tabInActive));

            binding.homeImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_inactive));
            binding.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_inactive));

            fragment = TradeHomeFragment.newInstance();
        }
        if (fragment != null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout_container, fragment)
                    .commitNow();
    }

    private void showJoinWaitlist(String featureName) {
        JoinWaitListBottomDialogFragment joinWaitListFragment =
                JoinWaitListBottomDialogFragment.newInstance(featureName);
        joinWaitListFragment.show(getSupportFragmentManager(),
                "join_waitlist");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_layout_container);
        if (fragment instanceof HomeFragment) {
            HomeViewModel mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            mViewModel.setDeepLinkData(intent.getData());
            //((HomeFragment) fragment).handleDeepLink(intent.getData());
        }

    }
}