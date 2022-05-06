package com.sagisu.vaultLibrary.ui.transactions;

import android.content.Intent;
import android.net.Uri;
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
import com.sagisu.vaultLibrary.databinding.TransactionDetailsFragmentBinding;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.utils.Util;

public class TransactionDetailsFragment extends Fragment {

    private TransactionDetailsViewModel mViewModel;
    private TransactionDetailsFragmentBinding binding;

    public static TransactionDetailsFragment newInstance() {
        return new TransactionDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transaction_details_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(TransactionDetailsViewModel.class);

        mViewModel.getTransactionData().observe(getViewLifecycleOwner(), new Observer<Transaction>() {
            @Override
            public void onChanged(Transaction transaction) {
                binding.setModel(transaction);
            }
        });

        mViewModel.getCopyToClipboard().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Util.setClipboard(s);
            }
        });

        mViewModel.getOpenBlockExplorer().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(s)));
            }
        });
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);
    }
}