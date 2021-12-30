package com.sagisu.vault.ui.login.fragments;

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

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.LoginPage1Binding;


public class LoginPage1Fragment extends Fragment {

    private LoginViewModel mViewModel;
    private LoginPage1Binding binding;

    public static LoginPage1Fragment newInstance() {
        return new LoginPage1Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_page1_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {

                binding.loginPhoneWrap.setError(loginFormError.getPhoneError() == null ? null : getString(loginFormError.getPhoneError()));
                binding.loginPhoneWrap.setErrorEnabled(loginFormError.getPhoneError() != null);

                binding.loginPasswordWrap.setError(loginFormError.getPasswordError() == null ? null : getString(loginFormError.getPasswordError()));
                binding.loginPasswordWrap.setErrorEnabled(loginFormError.getPasswordError() != null);

            }
        });
    }
}