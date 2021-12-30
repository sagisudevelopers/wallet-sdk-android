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
import androidx.lifecycle.ViewModelProviders;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.LoginPage3Binding;

public class LoginPage3Fragment extends Fragment {

    private LoginViewModel mViewModel;
    private LoginPage3Binding binding;

    public static LoginPage3Fragment newInstance() {
        return new LoginPage3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_password_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(LoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {

                binding.loginPassword.setError(loginFormError.getPasswordError() == null ? null : getString(loginFormError.getPasswordError()));
                binding.loginPassword.setErrorEnabled(loginFormError.getPasswordError() != null);

                binding.loginConfirmPassword.setError(loginFormError.getConfirmPasswordError() == null ? null : getString(loginFormError.getConfirmPasswordError()));
                binding.loginConfirmPassword.setErrorEnabled(loginFormError.getConfirmPasswordError() != null);

            }
        });
    }
}