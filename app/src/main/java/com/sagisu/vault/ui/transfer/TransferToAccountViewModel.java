package com.sagisu.vault.ui.transfer;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vault.R;
import com.sagisu.vault.models.Account;
import com.sagisu.vault.models.Payments;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.home.Balances;
import com.sagisu.vault.utils.AccountTypeDescriptor;

import java.util.List;

public class TransferToAccountViewModel extends ViewModel {
    private final MediatorLiveData<List<Account>> accountsList = new MediatorLiveData<>();
    private MutableLiveData<Payments> transferModel = new MutableLiveData(new Payments());
    private final MediatorLiveData<Transaction> bankTransferResponseData = new MediatorLiveData<>();
    private final MutableLiveData<AccountTransferFormError> formError = new MutableLiveData<>();
    private String accountType = AccountTypeDescriptor.CREDIT;
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private ObservableField<Boolean> validateFields = new ObservableField<>(true);
    private MediatorLiveData<Double> totalWalletBalance = new MediatorLiveData<>();

    public void init() {
        //getWalletBalance();
        fetchPaidAccounts();
    }

    public void fetchPaidAccounts() {
        setLoadingObservable("Getting bank accounts");
        LiveData<VaultResult<List<Account>>> accountsLiveData = NetworkRepository.getInstance().getPlaidAccounts();
        accountsList.addSource(accountsLiveData, new Observer<VaultResult<List<Account>>>() {
            @Override
            public void onChanged(VaultResult<List<Account>> listResult) {
                setLoadingObservable(null);
                if (listResult instanceof VaultResult.Success) {
                    accountsList.setValue(((VaultResult.Success<List<Account>>) listResult).getData());
                } else if (listResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) listResult).getError();
                    AccountTransferFormError accountTransferFormError = new AccountTransferFormError();
                    accountTransferFormError.setToastError(vaultApiError.message());
                    formError.setValue(accountTransferFormError);
                }
            }
        });
    }

    public void getWalletBalance() {
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().walletBalance();
        totalWalletBalance.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                if (balancesResult instanceof VaultResult.Success) {
                    totalWalletBalance.setValue(((VaultResult.Success<Balances>) balancesResult).getData().getAvailable());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
                }
            }
        });
    }

    public MediatorLiveData<List<Account>> getAccountsList() {
        return accountsList;
    }

    public MutableLiveData<Payments> getTransferModel() {
        return transferModel;
    }

    public void setTransferModel(Payments transferModel) {
        this.transferModel.setValue(transferModel);
    }

    public void setTransferType(String transferType) {
        Payments transferRequest = transferModel.getValue();
       // transferRequest.setTransactionType(transferType);
        //transferModel.setValue(transferRequest);
    }

    public void doTransfer() {
        if (validated()) {
            setLoadingObservable("Transferring amount");
            //Hit api to transfer amount
            LiveData<VaultResult<Transaction>> bankTransferResponseLiveData = NetworkRepository.getInstance().createBankTransfer(transferModel.getValue());
            bankTransferResponseData.addSource(bankTransferResponseLiveData, new Observer<VaultResult<Transaction>>() {
                @Override
                public void onChanged(VaultResult<Transaction> bankTransferResponseResult) {
                    setLoadingObservable(null);
                    if (bankTransferResponseResult instanceof VaultResult.Success)
                        bankTransferResponseData.setValue(((VaultResult.Success<Transaction>) bankTransferResponseResult).getData());
                    else if (bankTransferResponseResult instanceof VaultResult.Error) {
                        VaultAPIError vaultApiError = ((VaultResult.Error) bankTransferResponseResult).getError();
                        AccountTransferFormError accountTransferFormError = new AccountTransferFormError();
                        accountTransferFormError.setToastError(vaultApiError.message());
                        formError.setValue(accountTransferFormError);
                    }
                }
            });
        }
    }

    public void exchangeForAccessToken(String publicToken, String institutionName) {
        setLoadingObservable("Getting bank details");
        LiveData<VaultResult<BankDetailsResponse>> exchangeTokenResponseLiveData = NetworkRepository.getInstance().bankDetails(publicToken, institutionName);
        bankTransferResponseData.addSource(exchangeTokenResponseLiveData, new Observer<VaultResult<BankDetailsResponse>>() {
            @Override
            public void onChanged(VaultResult<BankDetailsResponse> bankTransferResponseResult) {
                setLoadingObservable(null);
                if (bankTransferResponseResult instanceof VaultResult.Success) {
                    BankDetailsResponse bankDetailsResponse = ((VaultResult.Success<BankDetailsResponse>) bankTransferResponseResult).getData();
                    // transferModel.setAccessToken(bankDetailsResponse.getAccess_token());
                    setAccountSelected(bankDetailsResponse.accounts.get(0), accountType);
                    fetchPaidAccounts();
                }else if(bankTransferResponseResult instanceof VaultResult.Error){
                    VaultAPIError vaultApiError = ((VaultResult.Error) bankTransferResponseResult).getError();
                    AccountTransferFormError formError1 = new AccountTransferFormError();
                    formError1.setToastError(vaultApiError.message());
                    formError.setValue(formError1);
                }
            }
        });

    }

    public void setAccountSelected(Account account, String accountType) {
        Payments transferModel = getTransferModel().getValue();
        if (transferModel == null) return;
        switch (accountType) {
            case AccountTypeDescriptor.DEBIT:
                //transferModel.setSourceInstitutionName(account.getInstitutionName());
                transferModel.setAccountNumber(account.getAccount_id());
                transferModel.setFromAccountName(account.getName());
                break;
            case AccountTypeDescriptor.CREDIT:
                transferModel.setReceiver(account);
                transferModel.setToAccountName(account.getName());
                break;

        }
        setTransferModel(transferModel);
    }

    public boolean validated() {
        /* Do the necessary page validation
         * error model helps in setting up UI error
         */
        boolean flag = true;
        AccountTransferFormError tmpFormError = new AccountTransferFormError();
        Payments transferModel = getTransferModel().getValue();

        assert transferModel != null;
        if (transferModel.getAmount() == null || transferModel.getAmount() == 0) {
            flag = false;
            tmpFormError.setAmountError(R.string.required_field_error);
        }else if(transferModel.getAmount() > getTotalWalletBalance().getValue()){
            flag = false;
            tmpFormError.setAmountError(R.string.amount_higher_than_wallet_balance);
        }

        if (transferModel.getReceiver().getAccount_id() == null || transferModel.getReceiver().getAccount_id().isEmpty()) {
            flag = false;
            tmpFormError.setDepositToAccountError(R.string.required_field_error);
        } else if (transferModel.getReceiver().getAccount_id().equals(transferModel.getAccountNumber())) {
            flag = false;
            tmpFormError.setDepositToAccountError(R.string.select_different_account_error);
        }

        //TODO: Based on self or account do from account validation

        formError.setValue(tmpFormError);

        return flag;
    }

    public MutableLiveData<AccountTransferFormError> getFormError() {
        return formError;
    }

    public MediatorLiveData<Transaction> getBankTransferResponseData() {
        return bankTransferResponseData;
    }

    public void setAccountType(@AccountTypeDescriptor.AccountTypes String accountType) {
        this.accountType = accountType;
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public void setLoadingObservable(String loadingObservable) {
        this.loadingObservable.setValue(loadingObservable);
    }

    public ObservableField<Boolean> getValidateFields() {
        return validateFields;
    }

    public MediatorLiveData<Double> getTotalWalletBalance() {
        return totalWalletBalance;
    }

    public void setTotalWalletBalance(Double totalWalletBalance) {
        this.totalWalletBalance.setValue(totalWalletBalance);
    }
}