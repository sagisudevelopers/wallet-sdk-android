package com.sagisu.vault.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.TradeHomeFragmentBinding;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.ui.BuyCoinsActivity;
import com.sagisu.vault.ui.ReceiveCryptoActivity;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.ui.trade.CryptoTokensListAdapter;
import com.sagisu.vault.ui.trade.SelectCoinsFragment;
import com.sagisu.vault.ui.trade.send.SendActivity;
import com.sagisu.vault.ui.trade.watchlists.TokenWatchlistsActivity;
import com.sagisu.vault.utils.AppManager;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class TradeHomeFragment extends Fragment implements CoinListAdapter.ICoinSelectionListener, CryptoTokensListAdapter.ICoinSelectionListener {

    public static final String BUNDLE_TRANSFER_TYPE = "bundle_transfer_type";
    public static final String BUNDLE_WALLET_BALANCE = "wallet_balance";
    private TradeHomeViewModel mViewModel;
    private TradeHomeFragmentBinding binding;
    private CoinListAdapter adapter;
    private CryptoTokensListAdapter tokenAdapter;
    private String status;

    public static TradeHomeFragment newInstance() {
        return new TradeHomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_coin_wallet_home, container, false);
        binding = DataBindingUtil.bind(rootView);
        String userId = UUID.randomUUID().toString();

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(TradeHomeViewModel.class);
        mViewModel.init();
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        mViewModel.getBalances().observe(getViewLifecycleOwner(), new Observer<Balances>() {
            @Override
            public void onChanged(Balances balances) {
                //new SharedPref().setCryptoBalanceUpdated(true);
                initRecyclerView(balances.getCoinBalance());
            }
        });

        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mViewModel.setUserName(user.getName());
                }
            }
        });

        mViewModel.getTradeTypes().observe(getViewLifecycleOwner(), new Observer<Util.TradeTypes>() {
            @Override
            public void onChanged(Util.TradeTypes tradeTypes) {
                if (tradeTypes == null) return;
                Intent intent;

                switch (tradeTypes) {
                    case BUY:
                        intent = new Intent(getActivity(), BuyCoinsActivity.class);
                        break;
                    case SEND:
                        intent = new Intent(getActivity(), SendActivity.class);
                        ArrayList<Coins> coinsArrayList = null;
                        if (mViewModel.getBalances().getValue() != null)
                            coinsArrayList = (ArrayList<Coins>) mViewModel.getBalances().getValue().getCoinBalance();
                        intent.putParcelableArrayListExtra(SelectCoinsFragment.BUNDLE_COINS_KEY, coinsArrayList);
                        break;
                    case RECEIVE:
                        intent = new Intent(getActivity(), ReceiveCryptoActivity.class);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + tradeTypes);
                }
                intent.putExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE, tradeTypes);
                startActivity(intent);
            }
        });

        mViewModel.getToastMsg().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    mViewModel.setToastMsg(null);
                    Util.showSnackBar(s, getActivity());
                    //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getShimmerBalanceView().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                shimmerBalanceView(aBoolean);
            }
        });

        /* Observe if buy or swap clicked
         * Open receive dialog fragment
         */
        mViewModel.getJoinWaitList().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showJoinWaitlist(s);
            }
        });

        initPopularCurrenciesView();
    }

    private void showJoinWaitlist(String featureName) {
        JoinWaitListBottomDialogFragment joinWaitListFragment =
                JoinWaitListBottomDialogFragment.newInstance(featureName);
        joinWaitListFragment.show(getActivity().getSupportFragmentManager(),
                "join_waitlist");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mViewModel.getBalances().getValue() == null || !new SharedPref().isCryptoBalanceUpdated()) {
            mViewModel.getCryptoWalletBalance();
        }
    }

    private void initRecyclerView(List<Coins> coinsList) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CoinListAdapter(getActivity(), coinsList, this);
        binding.recyclerView.setAdapter(adapter);
    }

    private void initPopularCurrenciesView() {
        binding.tokenListAdapter.setLayoutManager(new LinearLayoutManager(getActivity()));
        tokenAdapter = new CryptoTokensListAdapter(getActivity(), this::coinClick);
        binding.tokenListAdapter.setAdapter(tokenAdapter);

        mViewModel.getFlowable()
                // Using AutoDispose to handle subscription lifecycle.
                // See: https://github.com/uber/AutoDispose
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> tokenAdapter.submitData(getLifecycle(), pagingData), error -> error.printStackTrace());
    }

    private void shimmerBalanceView(boolean visibility) {
        binding.shimmerViewContainer.setVisibility(visibility ? View.VISIBLE : View.GONE);
        binding.homeWalletBalance.setVisibility(visibility ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public void onCoinSelected(Coins coins) {
        /*if (mViewModel.getBalances().getValue() != null && mViewModel.getBalances().getValue().getCoinBalance() != null) {
            Integer i = mViewModel.getBalances().getValue().getCoinBalance().indexOf(coins);
            if (i >= 0) {
                Coins tmpCoins = mViewModel.getBalances().getValue().getCoinBalance().get(i);
                coins.setBalance(tmpCoins.getBalance());
                coins.setCurrentValue(tmpCoins.getUsdBalance());
            }
        }
        */
        Intent intent = new Intent(getActivity(), TokenWatchlistsActivity.class);
        intent.putExtra(TokenWatchlistsActivity.BUNDLE_COINS, coins);
        startActivity(intent);
    }

    @Override
    public void coinClick(Coins coins) {
        if (mViewModel.getBalances().getValue() != null && mViewModel.getBalances().getValue().getCoinBalance() != null) {
            Integer i = mViewModel.getBalances().getValue().getCoinBalance().indexOf(coins);
            if (i >= 0) {
                Coins tmpCoins = mViewModel.getBalances().getValue().getCoinBalance().get(i);
                coins.setBalance(tmpCoins.getBalance());
                coins.setCurrentValue(tmpCoins.getUsdBalance());
            }
        }
        /*for (Coins tmpCoins :
                mViewModel.getBalances().getValue().getCoinBalance()) {
            if (coins.getName().equals(tmpCoins.getName())) {
                coins.setBalance(tmpCoins.getBalance());
                coins.setCurrentValue(tmpCoins.getCurrentValue());
            }
        }*/
        Intent intent = new Intent(getActivity(), TokenWatchlistsActivity.class);
        intent.putExtra(TokenWatchlistsActivity.BUNDLE_COINS, coins);
        startActivity(intent);
    }
}