package com.sagisu.vaultLibrary.ui.fundwallet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.TransactionDetailsActivity;
import com.sagisu.vaultLibrary.databinding.FundWalletFragmentBinding;
import com.sagisu.vaultLibrary.models.Account;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.ui.transfer.BankDetailsResponse;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.List;

public class FundWalletFragment extends Fragment {

    private FundWalletViewModel mViewModel;
    private FundWalletFragmentBinding binding;

    public static FundWalletFragment newInstance() {
        return new FundWalletFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fund_wallet_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(FundWalletViewModel.class);

        binding.setViewModel(mViewModel);
        mViewModel.init();

        //field validations
        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<FundWalletFormError>() {
            @Override
            public void onChanged(FundWalletFormError fundWalletFormError) {
                if (fundWalletFormError == null) return;

                binding.fundWalletAmount.setError(fundWalletFormError.getAmountError() == null ? null : getString(fundWalletFormError.getAmountError()));
                binding.fundWalletAmount.setErrorEnabled(fundWalletFormError.getAmountError() != null);


                if (fundWalletFormError.getToastMsg() != null)
                    Util.showSnackBar(fundWalletFormError.getToastMsg(),getActivity());
                    //Toast.makeText(getActivity(), fundWalletFormError.getToastMsg(), Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getAccessTokenReceived().observe(getViewLifecycleOwner(), new Observer<BankDetailsResponse>() {
            @Override
            public void onChanged(BankDetailsResponse bankDetailsResponse) {

            }
        });

        mViewModel.getAccountsList().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                displayAccounts(accounts);
            }
        });

        mViewModel.getLoadingAccountsObservable().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) return;
                loading(aBoolean);
            }
        });

        mViewModel.getBankTransferResponseData().observe(getViewLifecycleOwner(), new Observer<Transaction>() {
            @Override
            public void onChanged(Transaction transaction) {
                Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                intent.putExtra(TransactionDetailsActivity.BUNDLE_TRANSACTION, transaction);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }

    private void displayAccounts(List<Account> accountList) {
        binding.radioGroup.removeAllViews();
        int id = 0;
        for (Account account :
                accountList) {
            MaterialRadioButton mrb = new MaterialRadioButton(getActivity());
            mrb.setText(account.getName());
            mrb.setId(id);
            binding.radioGroup.addView(mrb);
            id++;
        }

        //set listener to radio button group
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
                //RadioButton radioBtn = (RadioButton) binding.getRoot().findViewById(checkedRadioButtonId);
                mViewModel.insertAccountDetails(accountList.get(checkedRadioButtonId));
            }
        });
    }

    public void loading(boolean loading) {
        binding.shimmerViewContainer.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.radioGroup.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);

        //binding.loadingAccounts.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}