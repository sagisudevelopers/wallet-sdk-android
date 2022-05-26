package com.sagisu.vaultLibrary.ui.trade.send;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.databinding.SendCryptoAddressFragmentBinding;

public class SendCryptoAddressFragment extends Fragment {

    private SendCoinsViewModel mViewModel;
    private SendCryptoAddressFragmentBinding binding;

    public static SendCryptoAddressFragment newInstance() {
        return new SendCryptoAddressFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.send_crypto_address_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(SendCoinsViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

    }

}