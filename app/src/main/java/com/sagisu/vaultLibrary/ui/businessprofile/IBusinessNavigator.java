package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.fragment.app.Fragment;

public interface IBusinessNavigator {
    void loadFragment(Fragment fragment);
    void setActionBarTittle(String tittle);
    void loading(String text);
}
