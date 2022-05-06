package com.sagisu.vaultLibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;

public class FeeInfo extends BaseObservable implements Parcelable {
    String networkFee;
    String serviceFee;

    protected FeeInfo(Parcel in) {
        networkFee = in.readString();
        serviceFee = in.readString();
    }

    public static final Creator<FeeInfo> CREATOR = new Creator<FeeInfo>() {
        @Override
        public FeeInfo createFromParcel(Parcel in) {
            return new FeeInfo(in);
        }

        @Override
        public FeeInfo[] newArray(int size) {
            return new FeeInfo[size];
        }
    };

    @Bindable
    public String getNetworkFee() {
        return networkFee;
    }

    @Bindable
    public String getServiceFee() {
        return serviceFee;
    }

    public void setNetworkFee(String networkFee) {
        this.networkFee = networkFee;
        notifyPropertyChanged(BR.networkFee);
    }

    public void setServiceFee(String serviceFee) {
        this.serviceFee = serviceFee;
        notifyPropertyChanged(BR.serviceFee);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(networkFee);
        parcel.writeString(serviceFee);
    }
}

