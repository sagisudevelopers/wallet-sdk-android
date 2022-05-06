package com.sagisu.vaultLibrary.ui.trade.watchlists;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;

public class CoinProfile extends BaseObservable {
    String overview;

    @Bindable
    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
        notifyPropertyChanged(BR.overview);
    }
}
