package com.sagisu.vault.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.ExternalAppLayoutBinding;
import com.sagisu.vault.databinding.MainActivityLayoutBinding;
import com.sagisu.vault.ui.home.HomeFragment;
import com.sagisu.vault.ui.home.JoinWaitListBottomDialogFragment;
import com.sagisu.vault.ui.home.TradeHomeFragment;
import com.sagisu.vault.ui.login.ExternalAppLoginRoueFragment;
import com.sagisu.vault.ui.login.VaultLoginActivity;
import com.sagisu.vault.ui.profile.ProfileFragment;
import com.sagisu.vault.ui.splashscreen.VaultSplashScreenActivity;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.SharedPref;

public class ExternalAppRouteFragment extends Fragment {
    private ExternalAppLayoutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.external_app_route_layout, container, false);
        binding = DataBindingUtil.bind(rootView);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUi();
    }

    private void loadUi() {
        if (new SharedPref().getToken() == null) {
            loadLogin();
        } else {
            loadHome();
        }
    }

    private void loadLogin() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.external_app_container, new ExternalAppLoginRoueFragment()).commit();

    }

    private void loadHome() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.external_app_container, new VaultMainFragment()).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove the Activity instance from the stack of AppManager
        AppManager.getAppManager().finishActivity(getActivity());
    }

}