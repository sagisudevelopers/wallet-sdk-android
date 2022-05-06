package com.sagisu.vaultLibrary.ui.contacts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ContactsViewModel extends ViewModel {
    private MutableLiveData<List<ContactsInfo>> contactInfoList = new MutableLiveData<>();

    public void setContactInfoList(List<ContactsInfo> contactInfoList) {
        this.contactInfoList.setValue(contactInfoList);
    }

    public MutableLiveData<List<ContactsInfo>> getContactInfoList() {
        return contactInfoList;
    }
}