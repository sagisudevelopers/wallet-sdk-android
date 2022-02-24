package com.sagisu.vault.ui.trade;

import android.graphics.Shader;

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
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultApiClient;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.CryptoTokensPagingSource;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.ui.home.Balances;
import com.sagisu.vault.utils.SharedPref;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class SelectCoinsViewModel extends ViewModel {
    private MediatorLiveData<List<Coins>> coinsList = new MediatorLiveData<>();
    private Flowable<PagingData<Coins>> flowable;

    public void init() {
    }

    public void getCryptoWalletBalance() {
        String vaultAccountId = new SharedPref().getBusinessVaultSelected().getVaultAccountId();
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance(vaultAccountId);
        coinsList.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                if (balancesResult instanceof VaultResult.Success) {
                    coinsList.setValue(((VaultResult.Success<Balances>) balancesResult).getData().getCoinBalance());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
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
                () -> new CryptoTokensPagingSource(VaultApiClient.buildRetrofitService(), query));

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