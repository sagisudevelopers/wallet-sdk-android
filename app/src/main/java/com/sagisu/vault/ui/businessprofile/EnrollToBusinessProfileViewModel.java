package com.sagisu.vault.ui.businessprofile;

import androidx.annotation.NonNull;
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
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.network.VaultAPIError;
import com.sagisu.vault.network.VaultApiClient;
import com.sagisu.vault.network.VaultNetworkState;
import com.sagisu.vault.network.VaultResult;
import com.sagisu.vault.repository.BusinessPagingSource;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.repository.TransactionPagingSource;
import com.sagisu.vault.repository.services.ServicesRepository;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.MutableStateFlow;

public class EnrollToBusinessProfileViewModel extends ViewModel {
    private Flowable<PagingData<Business>> flowable;
    private Flow<PagingData<Business>> flow;
    private MutableStateFlow<String> query;
    private MediatorLiveData<String> joinBusinessSuccess = new MediatorLiveData<>();
    private MutableLiveData<BusinessErrorBean> errorBean = new MutableLiveData<>(new BusinessErrorBean());
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);

    public void getAllBusiness(String query) {
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Business> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new BusinessPagingSource(VaultApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }


    public void joinBusiness(String businessId) {
        loadingObservable.setValue("Sending join request");
        LiveData<VaultResult<Business>> response = NetworkRepository.getInstance().joinBusiness(businessId);
        joinBusinessSuccess.addSource(response, new Observer<VaultResult<Business>>() {
            @Override
            public void onChanged(VaultResult<Business> businessVaultResult) {
                loadingObservable.setValue(null);
                if (businessVaultResult instanceof VaultResult.Success) {
                    joinBusinessSuccess.postValue(((VaultResult.Success<Business>) businessVaultResult).getMsg());
                } else if (businessVaultResult instanceof VaultResult.Error) {
                    VaultAPIError apiError = ((VaultResult.Error) businessVaultResult).getError();
                    BusinessErrorBean tmpErrorBean = new BusinessErrorBean();
                    tmpErrorBean.setToastError(apiError.message());
                    errorBean.setValue(tmpErrorBean);
                }
            }
        });
    }

    public Flowable<PagingData<Business>> getFlowable() {
        return flowable;
    }

    public MediatorLiveData<String> getJoinBusinessSuccess() {
        return joinBusinessSuccess;
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }
}