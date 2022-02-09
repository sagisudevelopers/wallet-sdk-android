package com.sagisu.vault.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.MainActivityLayoutBinding;
import com.sagisu.vault.ui.home.HomeFragment;
import com.sagisu.vault.ui.home.HomeViewModel;
import com.sagisu.vault.ui.home.JoinWaitListBottomDialogFragment;
import com.sagisu.vault.ui.home.TradeHomeFragment;
import com.sagisu.vault.ui.profile.ProfileFragment;
import com.sagisu.vault.utils.AppManager;

public class VaultMainFragment extends Fragment implements View.OnClickListener {

    private static final String TAB_HOME = "home";
    private static final String TAB_PROFILE = "profile";
    private static final String TAB_TRADE = "trade";

    private String tabClicked = "";
    private MainActivityLayoutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_vault_main, container, false);
        binding = DataBindingUtil.bind(rootView);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(getActivity());
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout_container, TradeHomeFragment.newInstance())
                    .commitNow();
        }*/
        binding.home.setOnClickListener(this);
        binding.profile.setOnClickListener(this);
        binding.trade.setOnClickListener(this);
        rootView.findViewById(R.id.trade).performClick();
        /*if (getArguments() == null) rootView.findViewById(R.id.trade).performClick();
        else {
            binding.home.performClick();
            onNewIntent(getArguments());
        }*/
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(getActivity());
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
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_layout_container, fragment)
                    .commit();
    }

    private void showJoinWaitlist(String featureName) {
        JoinWaitListBottomDialogFragment joinWaitListFragment =
                JoinWaitListBottomDialogFragment.newInstance(featureName);
        joinWaitListFragment.show(getActivity().getSupportFragmentManager(),
                "join_waitlist");
    }

    public void onNewIntent(Bundle intent) {
        /*Fragment fragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.main_layout_container);
        if (fragment instanceof HomeFragment) {
            HomeViewModel mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            mViewModel.setDeepLinkData(intent.getData());
            //((HomeFragment) fragment).handleDeepLink(intent.getData());
        }*/

    }
}