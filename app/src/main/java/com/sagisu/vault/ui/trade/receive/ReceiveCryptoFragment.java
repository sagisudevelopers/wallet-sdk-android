package com.sagisu.vault.ui.trade.receive;

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
import com.sagisu.vault.databinding.ReceiveCryptoFragmentBinding;
import com.sagisu.vault.utils.Globals;
import com.sagisu.vault.utils.SharedPref;

public class ReceiveCryptoFragment extends Fragment {

    private ReceiveCryptoViewModel mViewModel;
    private ReceiveCryptoFragmentBinding binding;

    public static ReceiveCryptoFragment newInstance() {
        return new ReceiveCryptoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.receive_crypto_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(ReceiveCryptoViewModel.class);
        mViewModel.init();
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getReceiveCryptoResponse().observe(getViewLifecycleOwner(), new Observer<ReceiveCryptoResponse>() {
            @Override
            public void onChanged(ReceiveCryptoResponse receiveCryptoResponse) {
                if (receiveCryptoResponse != null) {
                    binding.imgQrCode.setImageBitmap(receiveCryptoResponse.getQrCodeBitmap());
                    binding.receiveAddressTxt.setText(receiveCryptoResponse.getAddress());
                    new SharedPref(Globals.getContext()).setCryptoBalanceUpdated(!receiveCryptoResponse.isNew());
                }
            }
        });

    }

}