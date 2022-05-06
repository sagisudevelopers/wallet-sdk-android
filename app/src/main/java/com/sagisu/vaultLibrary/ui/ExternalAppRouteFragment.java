package com.sagisu.vaultLibrary.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.ExternalAppLayoutBinding;
import com.sagisu.vaultLibrary.databinding.MainActivityLayoutBinding;
import com.sagisu.vaultLibrary.ui.login.ExternalAppLoginRoueFragment;
import com.sagisu.vaultLibrary.utils.AppManager;
import com.sagisu.vaultLibrary.utils.SharedPref;

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