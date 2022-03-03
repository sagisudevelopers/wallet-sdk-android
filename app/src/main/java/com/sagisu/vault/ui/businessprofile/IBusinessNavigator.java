package com.sagisu.vault.ui.businessprofile;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;

import com.sagisu.vault.models.MyBusinessVault;

import java.util.List;

public interface IBusinessNavigator {
    void loadFragment(Fragment fragment);
    void setActionBarTittle(String tittle);
    void loading(String text);
}
