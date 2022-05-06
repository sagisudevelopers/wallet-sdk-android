package com.sagisu.vaultLibrary.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;
import com.sagisu.vaultLibrary.ui.home.TradeHomeViewModel;

public class Profile extends BaseObservable {
    String userName;
    String vaultId;
    TradeHomeViewModel.ProfileEnum profile;

    public Profile(){

    }
    public Profile(String userName,
                   String vaultId,
                   TradeHomeViewModel.ProfileEnum profile) {
        this.userName = userName;
        this.vaultId = vaultId;
        this.profile = profile;
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public String getVaultId() {
        return vaultId;
    }

    public void setVaultId(String vaultId) {
        this.vaultId = vaultId;
        notifyPropertyChanged(BR.vaultId);
    }

    public TradeHomeViewModel.ProfileEnum getProfile() {
        return profile;
    }

    public void setProfile(TradeHomeViewModel.ProfileEnum profile) {
        this.profile = profile;
    }
}
