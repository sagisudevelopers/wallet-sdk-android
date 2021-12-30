package com.sagisu.vault.ui.home;

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

import com.sagisu.vault.models.Coins;
import com.sagisu.vault.network.APIError;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.network.Result;
import com.sagisu.vault.repository.CryptoTokensPagingSource;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.login.fragments.LoginResponse;
import com.sagisu.vault.ui.login.fragments.User;
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

    public void init() {
        getCryptoTokens("");
        //getProfile();
        tradeTypes = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        joinWaitList = new MutableLiveData<>();
    }

    public void getCryptoWalletBalance() {
        shimmerBalanceView.setValue(true);
        LiveData<Result<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance();
        balances.addSource(balanceLiveData, new Observer<Result<Balances>>() {
            @Override
            public void onChanged(Result<Balances> balancesResult) {
                shimmerBalanceView.setValue(false);
                if (balancesResult instanceof Result.Success) {
                    balances.setValue(((Result.Success<Balances>) balancesResult).getData());
                } else if (balancesResult instanceof Result.Error) {
                    APIError apiError = ((Result.Error) balancesResult).getError();
                    toastMsg.setValue(apiError.message());
                }
            }
        });
    }

    public void getCryptoTokens(String query){
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Coins> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new CryptoTokensPagingSource(ApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }

    private void getProfile() {
        LiveData<Result<LoginResponse>> liveData = NetworkRepository.getInstance().getProfile();
        userData.addSource(liveData, new Observer<Result<LoginResponse>>() {
            @Override
            public void onChanged(Result<LoginResponse> agentResult) {

                if (agentResult instanceof Result.Success) {
                    User user = ((Result.Success<LoginResponse>) agentResult).getData().getUser();
                    userData.setValue(user);

                } else if (agentResult instanceof Result.Error) {
                    APIError apiError = ((Result.Error) agentResult).getError();
                    toastMsg.setValue(apiError.message());
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
}