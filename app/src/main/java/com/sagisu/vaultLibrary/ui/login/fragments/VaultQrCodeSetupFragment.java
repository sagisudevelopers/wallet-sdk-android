package com.sagisu.vaultLibrary.ui.login.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.VaultLoginPage3Binding;
import com.sagisu.vaultLibrary.databinding.VaultLoginQrCodeSetupBinding;

public class VaultQrCodeSetupFragment extends Fragment {

    private VaultLoginViewModel mViewModel;
    private VaultLoginQrCodeSetupBinding binding;

    public static VaultQrCodeSetupFragment newInstance() {
        return new VaultQrCodeSetupFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vault_login_qrcode_setup_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(VaultLoginViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        initRecoveryCodesList();

        mViewModel.getSecretCodeCopyAlert().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    showAlert();
                    mViewModel.setSecretCodeCopyAlert(false);
                }
            }
        });
    }

    private void initRecoveryCodesList() {
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.grid_item, R.id.grid_textview, mViewModel.getTotpData().getValue().getRecovery_codes());
        binding.recoveryCodeList.setAdapter(adapter);
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please save recovery codes in a safe place");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setMessage("Please make sure you have copied the recovery codes. Without these you cannot gain back access to the authenticator app if you loose it. You might permanently loose access to your account without these recovery codes");
        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mViewModel.setPage(VaultLoginViewModel.PageType.Totp);
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}