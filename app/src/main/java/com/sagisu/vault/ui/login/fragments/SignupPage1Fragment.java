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
import com.sagisu.vault.databinding.SignupPage1Binding;


public class SignupPage1Fragment extends Fragment {

    private LoginViewModel mViewModel;
    private SignupPage1Binding binding;

    public static SignupPage1Fragment newInstance() {
        return new SignupPage1Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signup_page1_fragment, container, false);
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

            }
        });
    }
}