package com.sagisu.vaultLibrary.ui.trade;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.ui.home.CoinListAdapter;
import com.sagisu.vaultLibrary.ui.transactions.TransactionLoadStateAdapter;
import com.sagisu.vaultLibrary.utils.ProgressShimmer;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.ArrayList;
import java.util.List;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class SelectCoinsFragment extends Fragment implements CryptoTokensListAdapter.ICoinSelectionListener, CoinListAdapter.ICoinSelectionListener, View.OnClickListener {

    public static final String BUNDLE_COINS_KEY = "Coins list";
    public static final String BUNDLE_TRADE_TYPE = "Trade type";
    public static final String KEY_LAYOUT = "Recycler position";

    private SelectCoinsViewModel mViewModel;
    private ICoinSelectionListener listener;
    private RecyclerView recyclerView;
    private AppCompatTextView tokenEmptyView;
    private CryptoTokensListAdapter cryptoTokensListAdapter;
    private CoinListAdapter coinListAdapter;
    Util.TradeTypes tradeType;
    View rootView;

    public static SelectCoinsFragment newInstance(Util.TradeTypes tradeType, ArrayList<Coins> coinsList) {
        SelectCoinsFragment fragment = new SelectCoinsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_TRADE_TYPE, tradeType);
        if (tradeType.equals(Util.TradeTypes.SEND))
            bundle.putParcelableArrayList(BUNDLE_COINS_KEY, coinsList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void coinClick(Coins coins) {
        if (coins.isSupported())
            listener.onCoinSelected(coins);
        else Util.showSnackBar("Token is not supported");
    }

    @Override
    public void onCoinSelected(Coins coins) {
        listener.onCoinSelected(coins);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.retry_button) {
            cryptoTokensListAdapter.retry();
        }
    }

    public interface ICoinSelectionListener {
        void onCoinSelected(Coins coins);

        void setActionBarTitle(String title);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ICoinSelectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ICoinSelectionListener ");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.setActionBarTitle("Select token");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.select_coins_fragment, container, false);
            recyclerView = rootView.findViewById(R.id.coins_list);
            tokenEmptyView = rootView.findViewById(R.id.token_empty_view);
            initViewModel(savedInstanceState);
        }
        return rootView;
    }

    private void initViewModel(Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(getActivity()).get(SelectCoinsViewModel.class);
        init();

        /*mViewModel.getCoinsList().observe(getViewLifecycleOwner(), new Observer<List<Coins>>() {
            @Override
            public void onChanged(List<Coins> coins) {
                initRecyclerView(coins);
            }
        });*/
    }

    private void init() {
        tradeType = (Util.TradeTypes) getArguments().getSerializable(BUNDLE_TRADE_TYPE);
        if (tradeType.equals(Util.TradeTypes.SEND)) {
            List<Coins> coinsList = getArguments().getParcelableArrayList(BUNDLE_COINS_KEY);
            if (coinsList == null) {
                mViewModel.getCryptoWalletBalance();
                mViewModel.getCoinsList().observe(getViewLifecycleOwner(), new Observer<List<Coins>>() {
                    @Override
                    public void onChanged(List<Coins> coinsList) {
                        if (coinsList == null || coinsList.isEmpty())
                            tokenEmptyView.setVisibility(View.VISIBLE);
                        else
                            initRecyclerViewCoinsList(coinsList);
                    }
                });
            } else if (coinsList.isEmpty()) {
                tokenEmptyView.setVisibility(View.VISIBLE);
            } else {
                mViewModel.setCoinsList(coinsList);
                initRecyclerViewCoinsList(coinsList);
            }
        } else {
            if (mViewModel.getFlowable() == null) {
                loading("Fetching tokens", true);
                mViewModel.getCryptoTokens("");
            }
            initRecyclerViewCryptoToken();
        }
    }

    private void initRecyclerViewCryptoToken() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cryptoTokensListAdapter = new CryptoTokensListAdapter(getActivity(), this);
        ConcatAdapter concatAdapter = cryptoTokensListAdapter.withLoadStateHeaderAndFooter(
                new TransactionLoadStateAdapter(this),
                new TransactionLoadStateAdapter(this));
        cryptoTokensListAdapter.addLoadStateListener(loadStates -> {
            if (loadStates.getPrepend() instanceof LoadState.NotLoading && loadStates.getPrepend().getEndOfPaginationReached()) {
                loading(null, false);
            } /*else {
                //binding.transactionHistoryList.setVisibility(View.VISIBLE);
                //binding.loading.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"Loading finished",Toast.LENGTH_LONG).show();
            }*/
            return null;
        });

        recyclerView.setAdapter(concatAdapter);

        mViewModel.getFlowable()
                // Using AutoDispose to handle subscription lifecycle.
                // See: https://github.com/uber/AutoDispose
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> cryptoTokensListAdapter.submitData(getLifecycle(), pagingData));


    }

    private void initRecyclerViewCoinsList(List<Coins> coinsList) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        coinListAdapter = new CoinListAdapter(getActivity(), coinsList, this);
        recyclerView.setAdapter(coinListAdapter);
    }

    public void loading(String text, boolean loading) {
        ProgressShimmer.shimmerLoading(rootView.findViewById(R.id.loading), recyclerView, text, loading);
    }

}