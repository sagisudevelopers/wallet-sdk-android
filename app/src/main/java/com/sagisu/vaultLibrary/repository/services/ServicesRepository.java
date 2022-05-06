package com.sagisu.vaultLibrary.repository.services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaultNetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServicesRepository {

    private static ServicesRepository customerRepository;
    VaultApiInterface api;
    private Executor executor;
    private LiveData<VaultNetworkState> networkState;
    ServicesDataFactory customerDataFactory;
    LiveData<PagedList<Transaction>> articleLiveData;
    private MediatorLiveData<VaultNetworkState> networkStateMediatorLiveData = new MediatorLiveData<>();


    public static ServicesRepository getInstance() {
        if (customerRepository == null) {
            customerRepository = new ServicesRepository();
        }
        return customerRepository;
    }

    private ServicesRepository() {
        api = VaultApiClient.buildRetrofitService();
    }


    public LiveData<PagedList<Transaction>> getProducts() {
        executor = Executors.newFixedThreadPool(5);

        customerDataFactory = new ServicesDataFactory(api);
        networkState = Transformations.switchMap(customerDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setInitialLoadSizeHint(20)
                        .setPageSize(1).build();

        articleLiveData = (new LivePagedListBuilder(customerDataFactory, pagedListConfig))
                .setFetchExecutor(executor)
                .build();

        return articleLiveData;
    }

    public LiveData<VaultNetworkState> getNetworkState() {
        return networkState;
    }

    public void searchProduct(String filterValue) {
        if (customerDataFactory == null || articleLiveData.getValue() == null) return;
        customerDataFactory.search(filterValue);
        articleLiveData.getValue().getDataSource().invalidate();
    }
}
