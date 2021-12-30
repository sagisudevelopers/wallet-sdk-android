package com.sagisu.vault.repository.services;


import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.sagisu.vault.network.ApiInterface;


public class ServicesDataFactory extends DataSource.Factory {

    private MutableLiveData<ServicesDataSource> mutableLiveData;
    private ServicesDataSource customerDataSource;
    private ApiInterface api;
    String filterKey,filterValue;
    MutableLiveData<String> filterText = new MutableLiveData<>();
    String productType;

    public ServicesDataFactory(ApiInterface api) {
        this.api = api;
        this.mutableLiveData = new MutableLiveData<ServicesDataSource>();
    }

    @Override
    public DataSource create() {
        customerDataSource = new ServicesDataSource(api);
        customerDataSource.setFilterKey(filterKey);
        customerDataSource.setFilterValue(filterValue);
        customerDataSource.setProductType(productType);
        mutableLiveData.postValue(customerDataSource);
        return customerDataSource;
    }


    public MutableLiveData<ServicesDataSource> getMutableLiveData() {
        return mutableLiveData;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void setFilterKey(String filterKey) {
        this.filterKey = filterKey;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void search(String filterValue){
       setFilterKey(filterValue);
    }
}
