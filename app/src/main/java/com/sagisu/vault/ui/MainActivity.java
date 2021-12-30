package com.sagisu.vault.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.ui.home.HomeFragment;
import com.sagisu.vault.ui.home.HomeViewModel;
import com.sagisu.vault.ui.home.JoinWaitListBottomDialogFragment;
import com.sagisu.vault.ui.home.TradeHomeFragment;
import com.sagisu.vault.ui.profile.ProfileFragment;
import com.sagisu.vault.utils.AppManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAB_HOME = "home";
    private static final String TAB_PROFILE = "profile";
    private static final String TAB_TRADE = "trade";

    private String tabClicked = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(this);
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout_container, TradeHomeFragment.newInstance())
                    .commitNow();
        }*/
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.profile).setOnClickListener(this);
        findViewById(R.id.trade).setOnClickListener(this);
        if (getIntent().getData() == null) findViewById(R.id.trade).performClick();
        else {
            findViewById(R.id.home).performClick();
            onNewIntent(getIntent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                ((AppCompatTextView) findViewById(R.id.home_txt)).setTextColor(getResources().getColor(R.color.colorPrimary));
                ((AppCompatTextView) findViewById(R.id.profile_txt)).setTextColor(getResources().getColor(R.color.tabInActive));

                ((AppCompatImageView) findViewById(R.id.home_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_home_active));
                ((AppCompatImageView) findViewById(R.id.profile_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_inactive));

                fragment = HomeFragment.newInstance();
            }
        } else if (id == R.id.profile) {
            if (tabClicked.equals(TAB_PROFILE)) return;
            else {
                tabClicked = TAB_PROFILE;
                ((AppCompatTextView) findViewById(R.id.home_txt)).setTextColor(getResources().getColor(R.color.tabInActive));
                ((AppCompatTextView) findViewById(R.id.profile_txt)).setTextColor(getResources().getColor(R.color.colorPrimary));


                ((AppCompatImageView) findViewById(R.id.home_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_home_inactive));
                ((AppCompatImageView) findViewById(R.id.profile_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_active));

                fragment = ProfileFragment.newInstance();
            }


        } else if (id == R.id.trade) {
            if (tabClicked.equals(TAB_TRADE)) return;
            //showJoinWaitlist(FeatureNameDescriptor.TRADE);
            tabClicked = TAB_TRADE;
            ((AppCompatTextView) findViewById(R.id.home_txt)).setTextColor(getResources().getColor(R.color.tabInActive));
            ((AppCompatTextView) findViewById(R.id.profile_txt)).setTextColor(getResources().getColor(R.color.tabInActive));

            ((AppCompatImageView) findViewById(R.id.home_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_home_inactive));
            ((AppCompatImageView) findViewById(R.id.profile_img)).setImageDrawable(getResources().getDrawable(R.drawable.ic_profile_inactive));

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