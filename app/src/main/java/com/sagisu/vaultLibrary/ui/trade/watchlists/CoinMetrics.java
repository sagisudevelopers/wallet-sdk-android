package com.sagisu.vaultLibrary.ui.trade.watchlists;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;

public class CoinMetrics extends BaseObservable {
    MarketData market_data;
    MarketCap marketcap;
    Supply supply;
    private boolean supported;

    @Bindable
    public MarketData getMarket_data() {
        return market_data;
    }

    public void setMarket_data(MarketData market_data) {
        this.market_data = market_data;
    }

    @Bindable
    public MarketCap getMarketcap() {
        return marketcap;
    }

    public void setMarketcap(MarketCap marketcap) {
        this.marketcap = marketcap;
    }

    @Bindable
    public Supply getSupply() {
        return supply;
    }

    public void setSupply(Supply supply) {
        this.supply = supply;
    }

    @Bindable
    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public class OHCLV extends BaseObservable{
        Double high;
        Double low;

        @Bindable
        public Double getHigh() {
            return high;
        }

        public void setHigh(Double high) {
            this.high = high;
        }

        @Bindable
        public Double getLow() {
            return low;
        }

        public void setLow(Double low) {
            this.low = low;
        }
    }

    public static class MarketData extends BaseObservable implements Parcelable {
        double price_usd;
        double percent_change_usd_last_24_hours;
        OHCLV ohlcv_last_24_hour;

        @Bindable
        public OHCLV getOhlcv_last_24_hour() {
            return ohlcv_last_24_hour;
        }

        public void setOhlcv_last_24_hour(OHCLV ohlcv_last_24_hour) {
            this.ohlcv_last_24_hour = ohlcv_last_24_hour;
        }

        @Bindable
        public double getPrice_usd() {
            return price_usd;
        }

        public void setPrice_usd(double price_usd) {
            this.price_usd = price_usd;
        }

        @Bindable
        public double getPercent_change_usd_last_24_hours() {
            return percent_change_usd_last_24_hours;
        }

        public void setPercent_change_usd_last_24_hours(double percent_change_usd_last_24_hours) {
            this.percent_change_usd_last_24_hours = percent_change_usd_last_24_hours;
        }


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

    public class MarketCap extends BaseObservable{
        Integer rank;
        Double current_marketcap_usd;

        @Bindable
        public Integer getRank() {
            return rank;
        }


        public void setRank(Integer rank) {
            this.rank = rank;
        }

        @Bindable
        public Double getCurrent_marketcap_usd() {
            return current_marketcap_usd;
        }

        public void setCurrent_marketcap_usd(Double current_marketcap_usd) {
            this.current_marketcap_usd = current_marketcap_usd;
        }
    }

    public class Supply extends BaseObservable{
        Double circulating;

        @Bindable
        public Double getCirculating() {
            return circulating;
        }

        public void setCirculating(Double circulating) {
            this.circulating = circulating;
            notifyPropertyChanged(BR.circulating);
        }
    }
}
