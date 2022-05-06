package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.utils.BusinessTypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class AddBusinessViewModel extends ViewModel {
    private MutableLiveData<Business> businessData;
    private MutableLiveData<List<Business.Director>> directorsList = new MutableLiveData<>(new ArrayList<>());
    private MediatorLiveData<String> postBusinessSuccess = new MediatorLiveData<>();
    private MutableLiveData<BusinessErrorBean> errorBean = new MutableLiveData<>(new BusinessErrorBean());
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> defaultBusiness = new MutableLiveData<>(true);

    public void init(@BusinessTypeDescriptor.BusinessTypes String businessType) {
        Business business = new Business();
        business.setType(businessType);
        businessData = new MutableLiveData<>(business);
    }

    public void setBusinessData(Business businessData) {
        this.businessData.setValue(businessData);
    }

    public MutableLiveData<Business> getBusinessData() {
        return businessData;
    }

    public void addDirectors(Business.Director director) {
        List<Business.Director> tmpList = directorsList.getValue();
        tmpList.add(director);
        directorsList.setValue(tmpList);
    }

    public void removeDirectors(Business.Director director) {
        List<Business.Director> tmpList = directorsList.getValue();
        tmpList.remove(director);
        directorsList.setValue(tmpList);
    }

    public MutableLiveData<List<Business.Director>> getDirectorsList() {
        return directorsList;
    }

    public void addButtonSubmit() {
        Business business = businessData.getValue();
        if (validate(business)) postBusiness(business);
    }

    private boolean validate(Business business) {
        BusinessErrorBean tmpErrorBean = new BusinessErrorBean();
        boolean flag = true;
        if (business.getName() == null || business.getName().isEmpty()) {
            tmpErrorBean.setNameError(R.string.required_field_error);
            flag = false;
        }

        if (business.getType().equals(BusinessTypeDescriptor.BUSINESS)) {
            business.setDirectors(directorsList.getValue());
            if (business.getEinNumber() == null || business.getEinNumber().isEmpty()) {
                tmpErrorBean.setEinError(R.string.required_field_error);
                flag = false;
            }

            if (business.getCorporationType() == null || business.getCorporationType().isEmpty()) {
                tmpErrorBean.setCorporationTypeError(R.string.required_field_error);
                flag = false;
            }

            if (business.getDepartment() == null || business.getDepartment().isEmpty()) {
                tmpErrorBean.setDepartmentError(R.string.required_field_error);
                flag = false;
            }
        }

        errorBean.setValue(tmpErrorBean);
        return flag;

    }

    private void postBusiness(Business business) {
        loadingObservable.setValue("Adding business");
        LiveData<VaultResult<Business>> response = NetworkRepository.getInstance().postBusiness(business, defaultBusiness.getValue());
        postBusinessSuccess.addSource(response, new Observer<VaultResult<Business>>() {
            @Override
            public void onChanged(VaultResult<Business> businessVaultResult) {
                loadingObservable.setValue(null);
                if (businessVaultResult instanceof VaultResult.Success) {
                    postBusinessSuccess.postValue(((VaultResult.Success<Business>) businessVaultResult).getMsg());
                } else if (businessVaultResult instanceof VaultResult.Error) {
                    VaultAPIError apiError = ((VaultResult.Error) businessVaultResult).getError();
                    BusinessErrorBean tmpErrorBean = new BusinessErrorBean();
                    tmpErrorBean.setToastError(apiError.message());
                    errorBean.setValue(tmpErrorBean);
                }
            }
        });
    }

    public MutableLiveData<BusinessErrorBean> getErrorBean() {
        return errorBean;
    }

    public MediatorLiveData<String> getPostBusinessSuccess() {
        return postBusinessSuccess;
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public MutableLiveData<Boolean> getDefaultBusiness() {
        return defaultBusiness;
    }

    public void setDefaultBusiness(Boolean defaultBusiness) {
        this.defaultBusiness.setValue(defaultBusiness);
    }
}