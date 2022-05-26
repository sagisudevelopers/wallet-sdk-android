package com.sagisu.vaultLibrary.ui.transactions;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultNetworkState;
import com.sagisu.vaultLibrary.repository.TransactionPagingSource;
import com.sagisu.vaultLibrary.repository.services.ServicesRepository;

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
                () -> new TransactionPagingSource(VaultApiClient.buildRetrofitService(), query));

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
    public LiveData<VaultNetworkState> getNetworkState() {
        return productRepository.getNetworkState();
    }
}