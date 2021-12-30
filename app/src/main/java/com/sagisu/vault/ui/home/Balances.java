package com.sagisu.vault.ui.home;

import android.os.Parcel;
import android.os.Parcelable;

import com.sagisu.vault.models.Coins;

import java.util.List;

public class Balances implements Parcelable {
    private double available;
    private double coinsTotal;
    private List<Coins> coinBalance;
    private double totalBalance;

    protected Balances(Parcel in) {
        available = in.readDouble();
        coinsTotal = in.readDouble();
        coinBalance = in.createTypedArrayList(Coins.CREATOR);
        totalBalance = in.readDouble();
    }

    public static final Creator<Balances> CREATOR = new Creator<Balances>() {
        @Override
        public Balances createFromParcel(Parcel in) {
            return new Balances(in);
        }

        @Override
        public Balances[] newArray(int size) {
            return new Balances[size];
        }
    };

    public double getAvailable() {
        return available;
    }

    public void setAvailable(double available) {
        this.available = available;
    }

    public double getCoinsTotal() {
        return coinsTotal;
    }

    public List<Coins> getCoinBalance() {
        return coinBalance;
    }

    public void setCoinsTotal(double coinsTotal) {
        this.coinsTotal = coinsTotal;
    }

    public void setCoinBalance(List<Coins> coinBalance) {
        this.coinBalance = coinBalance;
    }

    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(available);
        parcel.writeDouble(coinsTotal);
        parcel.writeTypedList(coinBalance);
        parcel.writeDouble(totalBalance);
    }
}
