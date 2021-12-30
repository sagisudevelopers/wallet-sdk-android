package com.sagisu.vault.ui.trade;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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
import com.sagisu.vault.ui.home.Balances;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class SelectCoinsViewModel extends ViewModel {
    private MediatorLiveData<List<Coins>> coinsList = new MediatorLiveData<>();
    private Flowable<PagingData<Coins>> flowable;

    public void init() {
        /*Coins coins = new Coins();
        coins.setName("BitCoin");
        coins.setBalance(50);
        coins.setUsdValue(5000);
        coins.setSymbol("BTC");
        coins.setPriceChangePercentage("-0.7");
        Coins coins1 = new Coins();
        coins1.setName("Etherium");
        coins.setSymbol("ETH");
        coins.setPriceChangePercentage("0.5");
        coins1.setBalance(50);
        coins1.setUsdValue(8000);
        List<Coins> coinsList = new ArrayList<>();
        coinsList.add(coins);
        coinsList.add(coins1);
        this.coinsList.setValue(coinsList);*/
        //getCryptoTokens("");
    }

    public void getCryptoWalletBalance() {
        LiveData<Result<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance();
        coinsList.addSource(balanceLiveData, new Observer<Result<Balances>>() {
            @Override
            public void onChanged(Result<Balances> balancesResult) {
                if (balancesResult instanceof Result.Success) {
                    coinsList.setValue(((Result.Success<Balances>) balancesResult).getData().getCoinBalance());
                } else if (balancesResult instanceof Result.Error) {
                    APIError apiError = ((Result.Error) balancesResult).getError();
                    //toastMsg.setValue(apiError.message());
                }
            }
        });
    }

    public void getCryptoTokens(String query) {
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Coins> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new CryptoTokensPagingSource(ApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }

    public MediatorLiveData<List<Coins>> getCoinsList() {
        return coinsList;
    }

    public void setCoinsList(List<Coins> coinsList) {
        this.coinsList.setValue(coinsList);
    }

    public Flowable<PagingData<Coins>> getFlowable() {
        return flowable;
    }
}