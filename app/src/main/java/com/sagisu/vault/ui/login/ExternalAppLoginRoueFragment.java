package com.sagisu.vault.ui.login;

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
import com.sagisu.vault.databinding.ExternalAppLoginRouteLayoutBinding;
import com.sagisu.vault.ui.VaultMainFragment;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.SharedPref;

public class ExternalAppLoginRoueFragment extends Fragment {
    private ExternalAppLoginRouteLayoutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.external_app_login_layout, container, false);
        binding = DataBindingUtil.bind(rootView);
        //Add an Activity instance to the stack of AppManager
        AppManager.getAppManager().addActivity(getActivity());

        binding.continueLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadLogin();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadUi();
    }

    private void loadUi() {
        if (new SharedPref().getToken() == null) {
            loadLogin();
        } else {
            loadHome();
        }
    }

    private void loadLogin() {
        Intent intent = new Intent(getActivity(), VaultLoginActivity.class);
        startActivity(intent);
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