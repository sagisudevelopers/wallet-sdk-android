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
import com.sagisu.vaultLibrary.databinding.VaultLoginTotpFragmentBinding;
import com.sagisu.vaultLibrary.databinding.VaultTotpRecoverFragmentBinding;

public class VaultTotpRecoverFragment extends Fragment {

    private VaultLoginViewModel mViewModel;
    private VaultTotpRecoverFragmentBinding binding;

    public static VaultTotpRecoverFragment newInstance() {
        return new VaultTotpRecoverFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vault_totp_recover_fragment, container, false);
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
                binding.loginTotpRecover.setError(loginFormError.getTotpRecoveryCodeError() == null ? null : getString(loginFormError.getTotpRecoveryCodeError()));
                binding.loginTotpRecover.setErrorEnabled(loginFormError.getTotpRecoveryCodeError() != null);

            }
        });
    }

}