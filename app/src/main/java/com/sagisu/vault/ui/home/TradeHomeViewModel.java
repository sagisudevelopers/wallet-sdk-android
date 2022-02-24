package com.sagisu.vault.ui.home;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.sagisu.vault.models.Business;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultApiClient;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.CryptoTokensPagingSource;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.login.fragments.LoginResponse;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class TradeHomeViewModel extends ViewModel {
    private MediatorLiveData<Balances> balances = new MediatorLiveData<>();
    private MutableLiveData<Util.TradeTypes> tradeTypes = new MutableLiveData<>();
    private MutableLiveData<String> assetToBuy = new MutableLiveData<>();
    private MutableLiveData<String> assetToReceive = new MutableLiveData<>();
    private MutableLiveData<String> joinWaitList = new MutableLiveData<>();
    private MutableLiveData<String> toastMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> shimmerBalanceView = new MutableLiveData<>(true);
    private MediatorLiveData<User> userData = new MediatorLiveData<>();
    private Flowable<PagingData<Coins>> flowable;
    private Business businessSelected;
    private ObservableField<String> userName = new ObservableField<>("");

    public void init() {
        getCryptoTokens("");
        //getProfile();
        tradeTypes = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        joinWaitList = new MutableLiveData<>();
        businessSelected = new SharedPref().getBusinessVaultSelected();
        if (businessSelected == null) {
            User user = new SharedPref().getUser();
            if (user == null) {
                getProfile();
            } else userData.setValue(user);

        } else userName.set(businessSelected.getName());
    }

    public void getCryptoWalletBalance() {
        shimmerBalanceView.setValue(true);
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance(businessSelected == null ? null : businessSelected.getVaultAccountId());
        balances.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                shimmerBalanceView.setValue(false);
                if (balancesResult instanceof VaultResult.Success) {
                    new SharedPref().setCryptoBalanceUpdated(true);
                    balances.setValue(((VaultResult.Success<Balances>) balancesResult).getData());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void getCryptoTokens(String query) {
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Coins> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new CryptoTokensPagingSource(VaultApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }

    private void getProfile() {
        LiveData<VaultResult<LoginResponse>> liveData = NetworkRepository.getInstance().getProfile();
        userData.addSource(liveData, new Observer<VaultResult<LoginResponse>>() {
            @Override
            public void onChanged(VaultResult<LoginResponse> agentResult) {

                if (agentResult instanceof VaultResult.Success) {
                    User user = ((VaultResult.Success<LoginResponse>) agentResult).getData().getUser();
                    new SharedPref().setUser(user);
                    userData.setValue(user);

                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void setTradeTypes(Util.TradeTypes tradeTypes) {
        this.tradeTypes.setValue(tradeTypes);
    }

    public MutableLiveData<Util.TradeTypes> getTradeTypes() {
        return tradeTypes;
    }

    public void setAssetToBuy(String assetToBuy) {
        this.assetToBuy.setValue(assetToBuy);
    }

    public MutableLiveData<String> getAssetToBuy() {
        return assetToBuy;
    }

    public MutableLiveData<String> getAssetToReceive() {
        return assetToReceive;
    }

    public void setAssetToReceive(String assetToReceive) {
        this.assetToReceive.setValue(assetToReceive);
    }

    public MutableLiveData<String> getJoinWaitList() {
        return joinWaitList;
    }

    public void setJoinWaitList(String joinWaitList) {
        this.joinWaitList.setValue(joinWaitList);
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg.setValue(toastMsg);
    }

    public MutableLiveData<Boolean> getShimmerBalanceView() {
        return shimmerBalanceView;
    }

    public MediatorLiveData<User> getUserData() {
        return userData;
    }

    public MediatorLiveData<Balances> getBalances() {
        return balances;
    }

    public Flowable<PagingData<Coins>> getFlowable() {
        return flowable;
    }

    public ObservableField<String> getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public Business getBusinessSelected() {
        return businessSelected;
    }
}