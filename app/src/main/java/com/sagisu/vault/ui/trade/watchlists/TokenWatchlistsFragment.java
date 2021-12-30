package com.sagisu.vault.ui.trade.watchlists;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.TokenWatchlistsFragmentBinding;
import com.sagisu.vault.ui.BuyCoinsActivity;
import com.sagisu.vault.ui.ReceiveCryptoActivity;
import com.sagisu.vault.ui.home.JoinWaitListBottomDialogFragment;
import com.sagisu.vault.ui.trade.SelectCoinsFragment;
import com.sagisu.vault.ui.trade.send.SendActivity;
import com.sagisu.vault.utils.FeatureNameDescriptor;
import com.sagisu.vault.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TokenWatchlistsFragment extends Fragment {

    private TokenWatchListsViewModel mViewModel;
    private TokenWatchlistsFragmentBinding binding;

    public static TokenWatchlistsFragment newInstance() {
        return new TokenWatchlistsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.token_watchlists_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);

        binding.materialButton9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJoinWaitlist(FeatureNameDescriptor.BUY_CRYPTO);
            }
        });
        binding.materialButton8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showJoinWaitlist(FeatureNameDescriptor.SELL_CRYPTO);
            }
        });

        binding.tokenWatchlistsReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mViewModel.coinSelected.getValue().isSupported()) {
                    Util.showSnackBar("Token is not supported");
                    return;
                }
                Intent intent = new Intent(getActivity(), ReceiveCryptoActivity.class);
                intent.putExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE, Util.TradeTypes.RECEIVE);
                intent.putExtra(ReceiveCryptoActivity.BUNDLE_COIN_SELECTED, mViewModel.coinSelected.getValue());
                startActivity(intent);
            }
        });

        binding.tokenWatchlistsSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewModel.coinSelected.getValue().getBalance() == 0) {
                    Util.showSnackBar("Insufficient token to send");
                    return;
                }
                Intent intent = new Intent(getActivity(), SendActivity.class);
                intent.putExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE, Util.TradeTypes.SEND);
                intent.putExtra(ReceiveCryptoActivity.BUNDLE_COIN_SELECTED, mViewModel.coinSelected.getValue());
                startActivity(intent);
            }
        });

        binding.tokenWatchlistsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(TokenWatchListsViewModel.class);
        mViewModel.init();

        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        binding.chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                mViewModel.onChipSelectionChanged(chipGroup, i);
            }
        });

        TooltipCompat.setTooltipText(binding.showDecimalPoints, String.valueOf(mViewModel.getCoinSelected().getValue().getBalance()));
        binding.showDecimalPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.showDecimalPoints.performLongClick();
            }
        });


        mViewModel.getLineData().observe(getViewLifecycleOwner(), new Observer<LineData>() {
            @Override
            public void onChanged(LineData lineData) {
                LineChart lineChart = binding.priceChart;
                lineChart.getDescription().setTextSize(12);
                lineChart.getDescription().setEnabled(false);
                lineChart.animateY(1000);
                lineChart.setData(lineData);

                // Setup X Axis
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setTextSize(8);
                xAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_700));
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        Date date = new Date((long) value);
                        //Specify the format you'd like
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy", Locale.ENGLISH);
                        return sdf.format(date);
                    }
                });
                 /*xAxis.setGranularityEnabled(true);
                xAxis.setGranularity(1.0f);
                xAxis.setXOffset(1f);
                xAxis.setLabelCount(25);
                xAxis.setAxisMinimum(0);
                xAxis.setAxisMaximum(24);*/

                // Setup Y Axis
                YAxis yAxis = lineChart.getAxisLeft();
                yAxis.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_700));
                /*yAxis.setAxisMinimum(0);
                yAxis.setAxisMaximum(3);
                yAxis.setGranularity(1f);*/

                /*ArrayList<String> yAxisLabel = new ArrayList<>();
                yAxisLabel.add(" ");
                yAxisLabel.add("Rest");
                yAxisLabel.add("Work");
                yAxisLabel.add("2-up");*/

                lineChart.getAxisLeft().setCenterAxisLabels(true);
               /* lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        if(value == -1 || value >= yAxisLabel.size()) return "";
                        return yAxisLabel.get((int) value);
                    }
                });*/

                lineChart.getAxisRight().setEnabled(false);
                CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker);
                lineChart.setMarkerView(mv);
                lineChart.invalidate();

            }
        });

        mViewModel.getLoadingChartObservable().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                shimmerChartView(s != null);
            }
        });
        binding.chipMonth.setChecked(true);
    }

    private void showJoinWaitlist(String featureName) {
        JoinWaitListBottomDialogFragment joinWaitListFragment =
                JoinWaitListBottomDialogFragment.newInstance(featureName);
        joinWaitListFragment.show(getActivity().getSupportFragmentManager(),
                "join_waitlist");
    }

    private void shimmerChartView(boolean visibility) {
        binding.shimmerViewContainer.setVisibility(visibility ? View.VISIBLE : View.GONE);
        binding.priceChart.setVisibility(visibility ? View.INVISIBLE : View.VISIBLE);

    }

}