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
import androidx.lifecycle.ViewModelProviders;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.VaultLoginQrCodeSetupBinding;
import com.sagisu.vaultLibrary.databinding.VaultLoginTotpFragmentBinding;

public class VaultTotpFragment extends Fragment {

    private VaultLoginViewModel mViewModel;
    private VaultLoginTotpFragmentBinding binding;

    public static VaultTotpFragment newInstance() {
        return new VaultTotpFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vault_login_totp_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(VaultLoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<LoginFormError>() {
            @Override
            public void onChanged(LoginFormError loginFormError) {
                binding.loginTotp.setError(loginFormError.getTotpError() == null ? null : getString(loginFormError.getTotpError()));
                binding.loginTotp.setErrorEnabled(loginFormError.getTotpError() != null);

            }
        });
    }

}