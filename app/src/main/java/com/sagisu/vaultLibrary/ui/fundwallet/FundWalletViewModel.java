package com.sagisu.vaultLibrary.ui.fundwallet;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.Account;
import com.sagisu.vaultLibrary.models.Payments;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.ui.home.Balances;
import com.sagisu.vaultLibrary.ui.login.fragments.User;
import com.sagisu.vaultLibrary.ui.transfer.BankDetailsResponse;

import java.util.List;

public class FundWalletViewModel extends ViewModel {
    private final MutableLiveData<Payments> bankTransferRequest = new MutableLiveData<>(new Payments());
    private final MutableLiveData<Boolean> launchPlaid = new MutableLiveData<>(null);
    private final MediatorLiveData<BankDetailsResponse> accessTokenReceived = new MediatorLiveData<>();
    private final MediatorLiveData<Transaction> bankTransferResponseData = new MediatorLiveData<>();
    private MutableLiveData<FundWalletFormError> formError = new MutableLiveData<>();
    private ObservableField<Boolean> validateFields = new ObservableField<>(true);
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> loadingAccountsObservable = new MutableLiveData<>(true);
    private final MediatorLiveData<List<Account>> accountsList = new MediatorLiveData<>();
    private MediatorLiveData<Double> totalWalletBalance = new MediatorLiveData<>();

    public ObservableField<Boolean> getValidateFields() {
        return validateFields;
    }

    public MutableLiveData<Payments> getBankTransferRequest() {
        return bankTransferRequest;
    }

    public MutableLiveData<Boolean> getLaunchPlaid() {
        return launchPlaid;
    }

    public void setLaunchPlaid(Boolean launchPlaid) {
        this.launchPlaid.setValue(launchPlaid);
    }

    public void init() {
        //getWalletBalance();
        fetchPaidAccounts();
    }

    public void checkUserStatus(User user) {
        if (user.getStatus().equals("Funding failed")) {
            bankTransferRequest.getValue().setRetry(true);
            bankTransferRequest.getValue().setAmount(user.getStatusMetadata().getAmount());
        } else {
            bankTransferRequest.getValue().setRetry(false);
        }
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

    public void fetchPaidAccounts() {
        setLoadingAccountsObservable(true);
        LiveData<VaultResult<List<Account>>> accountsLiveData = NetworkRepository.getInstance().getPlaidAccounts();
        accountsList.addSource(accountsLiveData, new Observer<VaultResult<List<Account>>>() {
            @Override
            public void onChanged(VaultResult<List<Account>> listResult) {
                setLoadingAccountsObservable(false);
                if (listResult instanceof VaultResult.Success) {
                    accountsList.setValue(((VaultResult.Success<List<Account>>) listResult).getData());
                } else if (listResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) listResult).getError();
                    FundWalletFormError formError1 = new FundWalletFormError();
                    formError1.setToastMsg(vaultApiError.message());
                    formError.setValue(formError1);
                }
            }
        });
    }

    public void proceedBtnClicked() {
        if (validated()) {
            //setLaunchPlaid(true);
            doTransfer();
        }

    }

    private boolean validated() {
        boolean flag = true;
        Payments bankTransferRequest = this.bankTransferRequest.getValue();
        FundWalletFormError formError = new FundWalletFormError();
        assert bankTransferRequest != null;
        if (bankTransferRequest.getAmount() == null || bankTransferRequest.getAmount() == 0) {
            flag = false;
            formError.setAmountError(R.string.required_field_error);
        }

        if (bankTransferRequest.getReceiver().getAccount_id() == null || bankTransferRequest.getReceiver().getAccount_id().isEmpty()) {
            flag = false;
            formError.setToastMsg("Please add or select account details");
        }

        this.formError.setValue(formError);
        return flag;
    }

    public void exchangeForAccessToken(String publicToken, String institutionName) {
        loadingObservable.setValue("Getting banking details");
        LiveData<VaultResult<BankDetailsResponse>> exchangeTokenResponseLiveData = NetworkRepository.getInstance().bankDetails(publicToken, institutionName);
        accessTokenReceived.addSource(exchangeTokenResponseLiveData, new Observer<VaultResult<BankDetailsResponse>>() {
            @Override
            public void onChanged(VaultResult<BankDetailsResponse> bankTransferResponseResult) {
                setLoadingObservable(null);
                if (bankTransferResponseResult instanceof VaultResult.Success) {
                    BankDetailsResponse bankDetailsResponse = ((VaultResult.Success<BankDetailsResponse>) bankTransferResponseResult).getData();
                    //bankTransferRequest.setAccessToken(bankDetailsResponse.getAccess_token());
                    insertAccountDetails(bankDetailsResponse.getAccounts().get(0));
                    fetchPaidAccounts();
                    //doTransfer();
                } else if (bankTransferResponseResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) bankTransferResponseResult).getError();
                    FundWalletFormError formError1 = new FundWalletFormError();
                    formError1.setToastMsg(vaultApiError.message());
                    formError.setValue(formError1);
                }

            }
        });

    }

    public void insertAccountDetails(Account account) {
        Payments bankTransferRequest = this.bankTransferRequest.getValue();
        assert bankTransferRequest != null;
        /*bankTransferRequest.setAccountId(account.getAccount_id());
        bankTransferRequest.setInstitutionName(account.getInstitutionName());*/ //TODO : delete this
        bankTransferRequest.setReceiver(account);
        bankTransferRequest.setToAccountName(account.getName());
    }

    public void doTransfer() {
        loadingObservable.setValue("Transferring amount");
        //Hit api to transfer amount
        LiveData<VaultResult<Transaction>> bankTransferResponseLiveData = NetworkRepository.getInstance().fundWallet(bankTransferRequest.getValue());
        bankTransferResponseData.addSource(bankTransferResponseLiveData, new Observer<VaultResult<Transaction>>() {
            @Override
            public void onChanged(VaultResult<Transaction> bankTransferResponseResult) {
                loadingObservable.setValue(null);
                if (bankTransferResponseResult instanceof VaultResult.Success)
                    bankTransferResponseData.setValue(((VaultResult.Success<Transaction>) bankTransferResponseResult).getData());
                else if (bankTransferResponseResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) bankTransferResponseResult).getError();
                    FundWalletFormError formError1 = new FundWalletFormError();
                    formError1.setToastMsg(vaultApiError.message());
                    formError.setValue(formError1);
                }
            }
        });

    }

    public MutableLiveData<FundWalletFormError> getFormError() {
        return formError;
    }

    public MediatorLiveData<BankDetailsResponse> getAccessTokenReceived() {
        return accessTokenReceived;
    }

    public void setLoadingObservable(String loadingObservable) {
        this.loadingObservable.setValue(loadingObservable);
    }

    public MediatorLiveData<List<Account>> getAccountsList() {
        return accountsList;
    }

    public void setLoadingAccountsObservable(Boolean loadingAccountsObservable) {
        this.loadingAccountsObservable.setValue(loadingAccountsObservable);
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public MutableLiveData<Boolean> getLoadingAccountsObservable() {
        return loadingAccountsObservable;
    }

    public MediatorLiveData<Double> getTotalWalletBalance() {
        return totalWalletBalance;
    }

    public void setTotalWalletBalance(Double totalWalletBalance) {
        this.totalWalletBalance.setValue(totalWalletBalance);
    }

    public MediatorLiveData<Transaction> getBankTransferResponseData() {
        return bankTransferResponseData;
    }
}