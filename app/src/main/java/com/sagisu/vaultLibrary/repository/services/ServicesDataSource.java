package com.sagisu.vaultLibrary.repository.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaultNetworkState;
import com.sagisu.vaultLibrary.ui.transactions.GetTransactionResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ServicesDataSource extends PageKeyedDataSource<Integer, Transaction> {

    private static final String TAG = ServicesDataSource.class.getSimpleName();

    private VaultApiInterface api;

    private MutableLiveData networkState;
    private MutableLiveData initialLoading;
    String filterValue;
    String filterKey;
    String productType;
    private String query;

    public ServicesDataSource(VaultApiInterface api) {
        this.api = api;
        networkState = new MutableLiveData();
        initialLoading = new MutableLiveData();
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    public MutableLiveData getInitialLoading() {
        return initialLoading;
    }

    public void setFilterValue(String filterValue) {
        if (filterValue != null)
            filterValue = filterValue.trim().replaceAll(" ", "||");
        this.filterValue = filterValue;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public void setProductType(String productType) {
        this.query = new StringBuilder("productType").append("||").append(productType).toString();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, Transaction> callback) {

        initialLoading.postValue(VaultNetworkState.LOADING);
        networkState.postValue(VaultNetworkState.LOADING);


        Call<GetTransactionResponse> call = api.getTransactionList(1);
        call.enqueue(new Callback<GetTransactionResponse>() {
            @Override
            public void onResponse(Call<GetTransactionResponse> call, Response<GetTransactionResponse> response) {
                if (response.isSuccessful()) {
                    //  Integer nextKey = (BaseConstants.Pagination.PAGE_SIZE >= response.getCount()) ? null : BaseConstants.Pagination.FIRST_PAGE_NO + 1;
                    Integer nextKey = null;
                    callback.onResult(response.body().getTransactions(), null, nextKey);
                    initialLoading.postValue(VaultNetworkState.LOADED);
                    networkState.postValue(VaultNetworkState.LOADED);
                } else {
                    initialLoading.postValue(new VaultNetworkState(VaultNetworkState.Status.FAILED, response.toString()));
                    networkState.postValue(new VaultNetworkState(VaultNetworkState.Status.FAILED, response.toString()));
                }
            }

            @Override
            public void onFailure(Call<GetTransactionResponse> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new VaultNetworkState(VaultNetworkState.Status.FAILED, errorMessage));
            }
        });

       /* NetworkRequest.getInstance().performAsyncRequest(api.getProducts(BaseConstants.Pagination.PAGE_SIZE, BaseConstants.Pagination.FIRST_PAGE_NO, filterKey, filterValue,query), (response) -> {
            if (response.isSuccess()) {
                Integer nextKey = (BaseConstants.Pagination.PAGE_SIZE >= response.getCount()) ? null : BaseConstants.Pagination.FIRST_PAGE_NO + 1;
                callback.onResult(response.getData(), null, nextKey);
                initialLoading.postValue(NetworkState.LOADED);
                networkState.postValue(NetworkState.LOADED);
            } else {
                initialLoading.postValue(new NetworkState(NetworkState.Status.FAILED, response.getMessage()));
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.getMessage()));
            }
        }, (error) -> {
            String errorMessage = error == null ? "unknown error" : error.getMessage();
            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

        });*/
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, Transaction> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, Transaction> callback) {

        Log.i(TAG, "Loading Rang " + params.key + " Count " + params.requestedLoadSize);

        networkState.postValue(VaultNetworkState.LOADING);

       /* NetworkRequest.getInstance().performAsyncRequest(api.getProducts(params.requestedLoadSize, params.key, filterKey, filterValue,query), (response) -> {
            if (response.isSuccess()) {
                Integer nextKey = ((params.key * params.requestedLoadSize) >= response.getCount()) ? null : params.key + 1;
                callback.onResult(response.getData(), nextKey);
                networkState.postValue(NetworkState.LOADED);
            } else
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.getMessage()));

        }, (error) ->

        {
            String errorMessage = error == null ? "unknown error" : error.getMessage();
            networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));

        });*/
    }
}
