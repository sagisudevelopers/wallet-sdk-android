package com.sagisu.vaultLibrary.ui.login.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.VaultLoginPage2Binding;

public class VaultLoginPage2Fragment extends Fragment {

    private VaultLoginViewModel mViewModel;
    private VaultLoginPage2Binding binding;
    public static VaultLoginPage2Fragment newInstance() {
        return new VaultLoginPage2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vault_otp_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(VaultLoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {

                binding.loginOtpWrap.setError(loginFormError.getOtpError() == null ? null : getString(loginFormError.getOtpError()));
                binding.loginOtpWrap.setErrorEnabled(loginFormError.getOtpError() != null);

            }
        });
    }
}