package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.utils.BusinessTypeDescriptor;

public class AddDaoViewModel extends ViewModel {
    private MutableLiveData<Business> businessData = new MutableLiveData<>();

    public void init() {
        Business business = new Business();
        business.setType(BusinessTypeDescriptor.DAO);
        businessData.setValue(business);
    }

    public MutableLiveData<Business> getBusinessData() {
        return businessData;
    }

    public void setBusinessData(Business businessData) {
        this.businessData.setValue(businessData);
    }

    public void addDaoSubmit() {
        Business business = businessData.getValue();
        if (validated(business)) {
            postDao(business);
        }
    }

    private void postDao(Business business) {

    }

    private boolean validated(Business business) {
        boolean flag = true;
        if (business.getName() == null || business.getName().isEmpty()) {
            flag = false;
        }
        return flag;
    }
}