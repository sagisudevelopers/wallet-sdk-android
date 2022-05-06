package com.sagisu.vaultLibrary.ui.trade.buy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;

public class BuyCoinsFragment extends Fragment {

    private BuyCoinsViewModel mViewModel;

    public static BuyCoinsFragment newInstance() {
        return new BuyCoinsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.buy_coins_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BuyCoinsViewModel.class);
        // TODO: Use the ViewModel
    }

}