package com.sagisu.vault.ui.transfer;

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

import com.sagisu.vault.R;
import com.sagisu.vault.TransactionDetailsActivity;
import com.sagisu.vault.databinding.TransferToAccountFragmentBinding;
import com.sagisu.vault.models.Account;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.utils.AccountTypeDescriptor;
import com.sagisu.vault.utils.Util;

import java.util.List;

public class TransferToAccountFragment extends Fragment {

    public interface ITransferToAccount {
        void launchPlaid();

        void loading(String text,boolean loading);
    }

    ITransferToAccount listener;

    private TransferToAccountViewModel mViewModel;
    private TransferToAccountFragmentBinding binding;
    private BankTransferRequest transferModel = new BankTransferRequest();

    public static TransferToAccountFragment newInstance(ITransferToAccount listener) {
        return new TransferToAccountFragment(listener);
    }

    public TransferToAccountFragment(ITransferToAccount listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_to_account_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        //setup from account dropdown with single item
        /*String[] toAccountList = new String[]{"Wallet"};
        assert binding != null;
        binding.dropdownBankTransferFrom.setAdapter(
                new ArrayAdapter<>(getActivity(),
                        R.layout.dropdown_menu_popup_item,
                        toAccountList));
*/
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(TransferToAccountViewModel.class);
        //binding.setTransferModel(mViewModel.getTransferModel().getValue());
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        //Initialize to account with list
        mViewModel.getAccountsList().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                CustomFooterAdapter adapter = new CustomFooterAdapter(getActivity(),
                        R.layout.dropdown_item_account,
                        R.id.dropdown_item_from_wallet,
                        accounts,
                        "Add Account");
                binding.dropdownBankTransferTo.setAdapter(adapter);


                /*adapter.setOnItemClickListener(new CustomFooterAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClicked(Account account) {
                        mViewModel.setAccountSelected(account);
                    }
                });*/

                adapter.setOnFooterClickListener(new CustomFooterAdapter.OnFooterClickListener() {
                    @Override
                    public void onFooterClicked() {
                        //Plaid link for the selection of bank accounts
                        listener.launchPlaid();
                    }
                });

                binding.dropdownBankTransferTo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                binding.transferAmount.setError(accountTransferFormError.getAmountError() != null ? getString(accountTransferFormError.getAmountError()) : null);
                binding.transferAmount.setErrorEnabled(accountTransferFormError.getAmountError() != null);

                binding.tlBankTransferTo.setError(accountTransferFormError.getDepositToAccountError() != null ? getString(accountTransferFormError.getDepositToAccountError()) : null);
                binding.tlBankTransferTo.setErrorEnabled(accountTransferFormError.getDepositToAccountError() != null);

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

        mViewModel.init();
    }

    public void setPublicToken(String publicToken, String institutionName) {
        mViewModel.exchangeForAccessToken(publicToken, institutionName);
    }

}