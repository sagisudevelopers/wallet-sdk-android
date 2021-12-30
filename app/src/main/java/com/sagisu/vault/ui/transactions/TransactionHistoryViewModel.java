package com.sagisu.vault.ui.transactions;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.network.NetworkState;
import com.sagisu.vault.repository.TransactionPagingSource;
import com.sagisu.vault.repository.services.ServicesRepository;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class TransactionHistoryViewModel extends ViewModel {
    ServicesRepository productRepository;
    private Flowable<PagingData<Transaction>> flowable;

    public void getTransactions(String query){
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Transaction> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new TransactionPagingSource(ApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }

    public Flowable<PagingData<Transaction>> getFlowable() {
        return flowable;
    }

    /*public LiveData<PagedList<Transaction>> getTransactions() {
        productRepository = ServicesRepository.getInstance();
        return productRepository.getProducts();
    }
*/
    @NonNull
    public LiveData<NetworkState> getNetworkState() {
        return productRepository.getNetworkState();
    }
}