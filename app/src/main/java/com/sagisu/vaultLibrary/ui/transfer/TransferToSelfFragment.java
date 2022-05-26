package com.sagisu.vaultLibrary.ui.transfer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.TransactionDetailsActivity;
import com.sagisu.vaultLibrary.databinding.TransferToSelfFragmentBinding;
import com.sagisu.vaultLibrary.models.Account;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.utils.AccountTypeDescriptor;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.List;

public class TransferToSelfFragment extends Fragment {

    public interface ITransferToSelf {
        void launchPlaid();
    }

    ITransferToSelf listener;

    private TransferToAccountViewModel mViewModel;
    private TransferToSelfFragmentBinding binding;
    private BankTransferRequest transferModel = new BankTransferRequest();

    public static TransferToSelfFragment newInstance(ITransferToSelf listener) {
        return new TransferToSelfFragment(listener);
    }

    public TransferToSelfFragment(ITransferToSelf listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tranfer_to_self_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(TransferToAccountViewModel.class);
        //binding.setTransferModel(mViewModel.getTransferModel());
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        //Initialize to account with list
        mViewModel.getAccountsList().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {

                CustomFooterAdapter fromAdapter = new CustomFooterAdapter(getActivity(),
                        R.layout.dropdown_item_account,
                        R.id.dropdown_item_from_wallet,
                        accounts,
                        "Add Account");
                binding.dropdownSelfBankTransferFrom.setAdapter(fromAdapter);
                fromAdapter.setOnFooterClickListener(new CustomFooterAdapter.OnFooterClickListener() {
                    @Override
                    public void onFooterClicked() {
                        //Plaid link for the selection of bank accounts
                        mViewModel.setAccountType(AccountTypeDescriptor.DEBIT);
                        listener.launchPlaid();
                    }
                });
                binding.dropdownSelfBankTransferFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mViewModel.setAccountSelected((Account) adapterView.getAdapter().getItem(i), AccountTypeDescriptor.DEBIT);
                    }
                });


                CustomFooterAdapter toAdapter = new CustomFooterAdapter(getActivity(),
                        R.layout.dropdown_item_account,
                        R.id.dropdown_item_from_wallet,
                        accounts,
                        "Add Account");
                binding.dropdownSelfBankTransferTo.setAdapter(toAdapter);
                toAdapter.setOnFooterClickListener(new CustomFooterAdapter.OnFooterClickListener() {
                    @Override
                    public void onFooterClicked() {
                        //Plaid link for the selection of bank accounts
                        mViewModel.setAccountType(AccountTypeDescriptor.CREDIT);
                        listener.launchPlaid();
                    }
                });
                binding.dropdownSelfBankTransferTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mViewModel.setAccountSelected((Account) adapterView.getAdapter().getItem(i), AccountTypeDescriptor.CREDIT);
                    }
                });

            }
        });

        //Field error handling
        mViewModel.getFormError().observe(getViewLifecycleOwner(), new Observer<AccountTransferFormError>() {
            @Override
            public void onChanged(AccountTransferFormError accountTransferFormError) {

                binding.selfTransferAmount.setError(accountTransferFormError.getAmountError() != null ? getString(accountTransferFormError.getAmountError()) : null);
                binding.selfTransferAmount.setErrorEnabled(accountTransferFormError.getAmountError() != null);

                binding.tlSelfBankTransferTo.setError(accountTransferFormError.getDepositToAccountError() != null ? getString(accountTransferFormError.getDepositToAccountError()) : null);
                binding.tlSelfBankTransferTo.setErrorEnabled(accountTransferFormError.getDepositToAccountError() != null);

                if (accountTransferFormError.getToastError() != null)
                    Util.showSnackBar(accountTransferFormError.getToastError(),getActivity());
                    //Toast.makeText(getActivity(), accountTransferFormError.getToastError(), Toast.LENGTH_SHORT).show();
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

        mViewModel.fetchPaidAccounts();
    }

    public void setPublicToken(String publicToken, String institutionName) {
        mViewModel.exchangeForAccessToken(publicToken, institutionName);
    }

}