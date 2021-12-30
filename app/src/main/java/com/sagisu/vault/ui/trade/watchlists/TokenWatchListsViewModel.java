package com.sagisu.vault.ui.trade.watchlists;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.models.ValidateAddressResponse;
import com.sagisu.vault.repository.NetworkRepository;
import com.sagisu.vault.utils.PrettyDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TokenWatchListsViewModel extends ViewModel {
    MutableLiveData<Coins> coinSelected = new MutableLiveData<>();
    MediatorLiveData<CoinProfile> coinProfile = new MediatorLiveData<>();
    MediatorLiveData<CoinMetrics> coinMetrics = new MediatorLiveData<>();
    MediatorLiveData<ChartMetrics> chartMetrics = new MediatorLiveData<>();
    MediatorLiveData<LineData> lineData = new MediatorLiveData<>();
    MutableLiveData<String> loadingObservable = new MutableLiveData<>();
    MutableLiveData<String> loadingChartObservable = new MutableLiveData<>();
    private MutableLiveData<Integer> range = new MutableLiveData<>(12);
    private MutableLiveData<Integer> interval = new MutableLiveData<>(1);

    public void init() {
        fetchCoinProfile();
        fetchCoinMetrics();
        // fetchChartData();
    }

    public void fetchCoinProfile() {
        loadingObservable.setValue("Fetching token profile");
        LiveData<CoinProfile> coinProfileLiveData = NetworkRepository.getInstance().getCoinProfile(coinSelected.getValue().getSymbol());
        coinProfile.addSource(coinProfileLiveData, new Observer<CoinProfile>() {
            @Override
            public void onChanged(CoinProfile coinProfile1) {
                loadingObservable.setValue(null);
                coinProfile.setValue(coinProfile1);
            }
        });
    }

    public void fetchCoinMetrics() {
        LiveData<CoinMetrics> coinProfileLiveData = NetworkRepository.getInstance().getCoinMetrics(coinSelected.getValue().getSymbol());
        coinMetrics.addSource(coinProfileLiveData, new Observer<CoinMetrics>() {
            @Override
            public void onChanged(CoinMetrics coinMetrics1) {
                coinMetrics.setValue(coinMetrics1);
            }
        });
    }

    public void fetchChartData(String start, String end, String interval) {
        loadingChartObservable.setValue("Fetching");
        LiveData<ChartMetrics> chartMetricsLiveData = NetworkRepository.getInstance().fetchChartData(coinSelected.getValue().getSymbol(), start, end, interval);
        lineData.addSource(chartMetricsLiveData, new Observer<ChartMetrics>() {
            @Override
            public void onChanged(ChartMetrics chartMetrics1) {
                loadingChartObservable.setValue(null);
                setLineDataSet(chartMetrics1);
            }
        });
    }

    private List<Entry> getDataSet(ChartMetrics chartMetrics1) {
        List<Entry> lineEntries = new ArrayList<>();
        for (Float[] d :
                chartMetrics1.values) {
            lineEntries.add(new Entry(d[0], d[4]));

        }
        return lineEntries;
    }

    private void setLineDataSet(ChartMetrics chartMetrics1) {
        List<Entry> lineEntries = getDataSet(chartMetrics1);
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Price");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setLineWidth(2);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleHoleRadius(3);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setHighlightEnabled(true);
        lineDataSet.setHighLightColor(Color.BLUE);
        lineDataSet.setValueTextSize(12);
        lineDataSet.setValueTextColor(Color.DKGRAY);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);

        LineData lineData = new LineData(lineDataSet);
        this.lineData.setValue(lineData);
    }

    public void onChipSelectionChanged(ChipGroup chipGroup, int i) {
        String start, end, format, interval;
        Calendar endCalender = Calendar.getInstance();
        Calendar startCalender = Calendar.getInstance();
        Chip chip = chipGroup.findViewById(i);
        String chipTxt = chip.getText().toString();
        switch (chipTxt) {
            case "1 Day":
                startCalender.add(Calendar.DATE, -1);
                format = PrettyDate.ISO_FORMAT;
                interval = "1h";
                break;
            case "3 Days":
                startCalender.add(Calendar.DATE, -3);
                format = PrettyDate.ONLY_DATE_FORMAT;
                interval = "1d";
                break;
            case "1 Week":
                startCalender.add(Calendar.DATE, -7);
                format = PrettyDate.ONLY_DATE_FORMAT;
                interval = "1d";
                break;
            case "1 Month":
                startCalender.add(Calendar.MONTH, -1);
                format = PrettyDate.ONLY_DATE_FORMAT;
                interval = "1d";
                break;
            case "1 Year":
                startCalender.add(Calendar.YEAR, -1);
                format = PrettyDate.ONLY_DATE_FORMAT;
                interval = "1w";
                break;
            default:
                startCalender.add(Calendar.YEAR, -40);
                format = PrettyDate.ONLY_DATE_FORMAT;
                interval = "1w";
                break;
        }
        start = PrettyDate.toString(startCalender.getTime(), format);
        end = PrettyDate.toString(endCalender.getTime(), format);
        fetchChartData(start, end, interval);
    }

    public MediatorLiveData<CoinProfile> getCoinProfile() {
        return coinProfile;
    }

    public MediatorLiveData<CoinMetrics> getCoinMetrics() {
        return coinMetrics;
    }

    public MediatorLiveData<LineData> getLineData() {
        return lineData;
    }

    public MutableLiveData<Coins> getCoinSelected() {
        return coinSelected;
    }

    public MutableLiveData<String> getLoadingChartObservable() {
        return loadingChartObservable;
    }
}