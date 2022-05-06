package com.sagisu.vaultLibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DiffUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Coins extends BaseObservable implements Parcelable {
    private String id;
    String name;
    private double balance;
    private double currentValue;
    private String logo;
    private String symbol;
    private Metrics metrics;
    private boolean supported;

    public Coins() {

    }

    protected Coins(Parcel in) {
        id = in.readString();
        name = in.readString();
        balance = in.readDouble();
        currentValue = in.readDouble();
        logo = in.readString();
        symbol = in.readString();
        metrics = in.readParcelable(Metrics.class.getClassLoader());
        supported = in.readByte() != 0;
    }

    public static final Creator<Coins> CREATOR = new Creator<Coins>() {
        @Override
        public Coins createFromParcel(Parcel in) {
            return new Coins(in);
        }

        @Override
        public Coins[] newArray(int size) {
            return new Coins[size];
        }
    };

    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public double getBalance() {
        return balance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @Bindable
    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public void setCurrentValue(Integer currentValue) {
        this.currentValue = currentValue;
    }

    @Bindable
    public double getUsdValue() {
        if (metrics == null || metrics.market_data == null) return 0;
        return metrics.market_data.price_usd;
    }

    @Bindable
    public double getUsdBalance() {
        if (metrics == null || metrics.market_data == null) return 0;
        return Math.round(balance * metrics.market_data.price_usd);
    }

    public String getId() {
        return id;
    }

    @Bindable
    public String getLogo() {
        return "https://messari.io/asset-images/" + id + "/64.png?v=2";
    }

    @Bindable
    public String getLogo128dp() {
        return "https://messari.io/asset-images/" + id + "/128.png?v=2";
    }

    @Bindable
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Bindable
    public double getPriceChangePercentage() {
        if (metrics == null || metrics.market_data == null) return 0;
        return metrics.market_data.percent_change_usd_last_24_hours;
    }

    public double convertUsdToTokenValue(String usdAmount) {
        Double convertedAmount = Double.parseDouble(usdAmount) / getUsdValue();
        BigDecimal bd = new BigDecimal(convertedAmount).setScale(6, RoundingMode.HALF_UP);
        double amount = bd.doubleValue();
        return amount;
    }

    @Bindable
    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    //User for pagination
    public static DiffUtil.ItemCallback<Coins> DIFF_CALLBACK = new DiffUtil.ItemCallback<Coins>() {
        @Override
        public boolean areItemsTheSame(@NonNull Coins oldItem, @NonNull Coins newItem) {
            return oldItem.symbol.equals(newItem.symbol);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Coins oldItem, @NonNull Coins newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Coins article = (Coins) obj;
        return article.symbol.equals(this.symbol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeDouble(balance);
        parcel.writeDouble(currentValue);
        parcel.writeString(logo);
        parcel.writeString(symbol);
        parcel.writeParcelable(metrics, i);
        parcel.writeByte((byte) (supported ? 1 : 0));
    }

    static class Metrics implements Parcelable {
        MarketData market_data;

        protected Metrics(Parcel in) {
            market_data = in.readParcelable(MarketData.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(market_data, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Metrics> CREATOR = new Creator<Metrics>() {
            @Override
            public Metrics createFromParcel(Parcel in) {
                return new Metrics(in);
            }

            @Override
            public Metrics[] newArray(int size) {
                return new Metrics[size];
            }
        };

        public MarketData getMarket_data() {
            return market_data;
        }


    }

    static class MarketData implements Parcelable {
        double price_usd;
        double percent_change_usd_last_24_hours;

        protected MarketData(Parcel in) {
            price_usd = in.readDouble();
            percent_change_usd_last_24_hours = in.readDouble();
        }

        public static final Creator<MarketData> CREATOR = new Creator<MarketData>() {
            @Override
            public MarketData createFromParcel(Parcel in) {
                return new MarketData(in);
            }

            @Override
            public MarketData[] newArray(int size) {
                return new MarketData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeDouble(price_usd);
            parcel.writeDouble(percent_change_usd_last_24_hours);
        }
    }
}