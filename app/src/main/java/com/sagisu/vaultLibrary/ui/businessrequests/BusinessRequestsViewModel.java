package com.sagisu.vaultLibrary.ui.businessrequests;

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

import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.repository.BusinessRequestsPagingSource;
import com.sagisu.vaultLibrary.repository.NetworkRepository;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class BusinessRequestsViewModel extends ViewModel {
    private Flowable<PagingData<BusinessRequest>> businessRequestList;
    private MediatorLiveData<BusinessRequest> businessRequest = new MediatorLiveData<>();
    private MediatorLiveData<String> response = new MediatorLiveData<>();
    private MutableLiveData<String> businessId = new MutableLiveData<>();

    public void init() {
        //fetchMyBusiness();
    }

    public void fetchAllBusinessRequests() {
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, BusinessRequest> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new BusinessRequestsPagingSource(VaultApiClient.buildRetrofitService(), businessId.getValue()));

        businessRequestList = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(businessRequestList, viewModelScope);
    }

    public LiveData<BusinessRequest> fetchBusinessRequests(String requestId) {
        return NetworkRepository.getInstance().getBusinessRequest(requestId);
        /*businessRequest.addSource(listLiveData, new Observer<List<BusinessRequest>>() {
            @Override
            public void onChanged(List<BusinessRequest> businessRequests) {
                businessRequest.setValue(businessRequests.get(0));
            }
        });*/

    }

    public void approve(String requestId) {
        LiveData<String> liveData = NetworkRepository.getInstance().approveBusinessRequest(requestId);
        response.addSource(liveData, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                response.setValue(s);
            }
        });
    }

    public void reject(String requestId) {
        LiveData<String> liveData = NetworkRepository.getInstance().rejectBusinessRequest(requestId);
        response.addSource(liveData, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                response.setValue(s);
            }
        });
    }

    public MediatorLiveData<String> getResponse() {
        return response;
    }

    public Flowable<PagingData<BusinessRequest>> getBusinessRequestList() {
        return businessRequestList;
    }

    public MutableLiveData<String> getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId.setValue(businessId);
    }

    public void setBusinessRequest(BusinessRequest businessRequest) {
        this.businessRequest.setValue(businessRequest);
    }
}