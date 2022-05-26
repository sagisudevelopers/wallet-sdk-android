package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.utils.SharedPref;

import java.util.List;

public class BusinessProfileViewModel extends ViewModel {
    MutableLiveData<Boolean> switchToBusinessProfile = new MutableLiveData<>(false);
    private MediatorLiveData<List<MyBusinessVault>> businessList = new MediatorLiveData<>();

    public void init() {
        switchToBusinessProfile.setValue(new SharedPref().getBusinessVaultSelected() != null);
        fetchMyBusiness();
    }

    public void fetchMyBusiness() {
        LiveData<List<MyBusinessVault>> listLiveData = NetworkRepository.getInstance().getMyBusiness();
        businessList.addSource(listLiveData, new Observer<List<MyBusinessVault>>() {
            @Override
            public void onChanged(List<MyBusinessVault> myBusinessVaults) {
                businessList.setValue(myBusinessVaults);
            }
        });
    }

    public MutableLiveData<Boolean> getSwitchToBusinessProfile() {
        return switchToBusinessProfile;
    }

    public void setSwitchToBusinessProfile(MutableLiveData<Boolean> switchToBusinessProfile) {
        this.switchToBusinessProfile = switchToBusinessProfile;
    }

    public MediatorLiveData<List<MyBusinessVault>> getBusinessList() {
        return businessList;
    }
}