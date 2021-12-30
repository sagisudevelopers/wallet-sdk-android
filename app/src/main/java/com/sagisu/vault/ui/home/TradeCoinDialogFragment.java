package com.sagisu.vault.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sagisu.vault.R;
import com.sagisu.vault.databinding.TradeCoinSheetBinding;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.utils.ProgressShimmer;
import com.sagisu.vault.utils.Util;

public class TradeCoinDialogFragment extends BottomSheetDialogFragment {
    public static final String COINS_KEY = "coins";
    public static final String TRADE_TYPE_KEY = "trade_type";

    private TradeCoinSheetBinding binding;
    private ITradeCoinListener listener;
    View view;
    Coins coins;
    private Util.TradeTypes tradeType;
    private String currencyCode;

    public interface ITradeCoinListener {
        void OnTradeAction(String amount, String currencyCode);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View bottomSheet = (View) getView().getParent();
        bottomSheet.setBackgroundTintMode(PorterDuff.Mode.CLEAR);
        bottomSheet.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);
        bottomSheet.setElevation(0);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ITradeCoinListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ITradeCoinListener ");
        }
    }

    public static TradeCoinDialogFragment newInstance(Coins coins, Util.TradeTypes tradeType) {
        TradeCoinDialogFragment bottomSheetFragment = new TradeCoinDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(COINS_KEY, coins);
        bundle.putSerializable(TRADE_TYPE_KEY, tradeType);
        bottomSheetFragment.setArguments(bundle);
        return bottomSheetFragment;
    }

    public TradeCoinDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.trade_coin_sheet, container,
                false);
        binding = DataBindingUtil.bind(view);
        Bundle bundle = getArguments();
        coins = bundle.getParcelable(COINS_KEY);
        currencyCode = coins.getSymbol();
        binding.setCurrencyCode(currencyCode);
        tradeType = (Util.TradeTypes) bundle.getSerializable(TRADE_TYPE_KEY);
        binding.setModel(coins);
        switch (tradeType) {
            case BUY:
                binding.setBtnText("BUY");
                break;
            case SEND:
                binding.setBtnText("SEND");
                break;
            case RECEIVE:
                binding.setBtnText("RECEIVE");
                break;
        }

        binding.swapCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapCurrencyCode();
            }
        });
        binding.tradeCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = binding.sellCoinAmount.getEditText().getText().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currencyCode.equals("USD")) {
                    amount = String.valueOf(coins.convertUsdToTokenValue(amount));
                }

                if (Double.parseDouble(amount) == 0) {
                    Toast.makeText(getActivity(), "Please enter higher value, amount is equivalent to zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(amount) > coins.getBalance()) {
                    Toast.makeText(getActivity(), "Insufficient token balance", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.OnTradeAction(amount, coins.getSymbol());
                dismiss();
            }
        });
        return binding.getRoot();

    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(view.findViewById(R.id.waitlist_loading), view.findViewById(R.id.view_waitlist), text, loading);
    }

    public void swapCurrencyCode() {
        if (currencyCode.equals("USD")) currencyCode = coins.getSymbol();
        else if (currencyCode.equals(coins.getSymbol())) currencyCode = "USD";

        binding.setCurrencyCode(currencyCode);
    }
}
