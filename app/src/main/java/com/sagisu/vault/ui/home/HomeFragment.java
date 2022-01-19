package com.sagisu.vault.ui.home;

import android.content.Intent;
import android.net.Uri;
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

import com.sagisu.vault.R;
import com.sagisu.vault.databinding.HomeFragmentBinding;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.ui.ContactsActivity;
import com.sagisu.vault.ui.FundWalletActivity;
import com.sagisu.vault.ui.ReceiveCryptoActivity;
import com.sagisu.vault.ui.TransferToAccountActivity;
import com.sagisu.vault.ui.kyc.VerifyIdIntroActivity;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.ui.trade.SelectCoinsFragment;
import com.sagisu.vault.ui.trade.send.SendActivity;
import com.sagisu.vault.ui.transactions.TransactionHistoryActivity;
import com.sagisu.vault.utils.SharedPref;
import com.sagisu.vault.utils.TransferTypeDescriptor;
import com.sagisu.vault.utils.Util;
import com.storyteller.Storyteller;
import com.storyteller.domain.ClientAd;
import com.storyteller.domain.ClientStory;
import com.storyteller.domain.UserActivity;
import com.storyteller.domain.UserActivityData;
import com.storyteller.domain.UserInput;
import com.storyteller.services.Error;
import com.storyteller.ui.row.StorytellerRowViewDelegate;

import java.util.List;
import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class HomeFragment extends Fragment implements StorytellerRowViewDelegate {

    public static final String BUNDLE_TRANSFER_TYPE = "bundle_transfer_type";
    public static final String BUNDLE_WALLET_BALANCE = "wallet_balance";
    private HomeViewModel mViewModel;
    private TradeHomeViewModel tradeHomeViewModel;
    private HomeFragmentBinding binding;
    private String status;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        String userId = UUID.randomUUID().toString();
        //Recent transaction click event
        binding.recentTransactionWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        binding.homeVerifyIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), VerifyIdIntroActivity.class);
                startActivity(intent);
            }
        });

        binding.channelRowView.setDelegate(this);

        Storyteller.Companion.initialize(ApiClient.STORYTELLER_API_KEY, false, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Storyteller.Companion.setUserDetails(new UserInput(userId));
                binding.channelRowView.reloadData(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        if (getActivity() != null && getActivity().getIntent() != null)
                            handleDeepLink(getActivity().getIntent().getData());
                        return null;
                    }
                }, new Function1<Error, Unit>() {
                    @Override
                    public Unit invoke(Error error) {
                        return null;
                    }
                });
                return null;
            }
        }, new Function1<Error, Unit>() {
            @Override
            public Unit invoke(Error error) {
                return null;
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        tradeHomeViewModel = new ViewModelProvider(getActivity()).get(TradeHomeViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(this);

        //Load specific view based on transfer type clicked
        mViewModel.getTransferType().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String transferType) {
                Intent intent;
                if (!Util.hasPermission(transferType, status)) {
                    Util.showSnackBar("Permission denied", getActivity());
                    return;
                }
                switch (transferType) {
                    case TransferTypeDescriptor
                            .TO_CONTACT:
                        intent = new Intent(getActivity(), ContactsActivity.class);
                        break;
                    case TransferTypeDescriptor
                            .TO_ACCOUNT:
                    case TransferTypeDescriptor
                            .TO_SELF:
                        intent = new Intent(getActivity(), TransferToAccountActivity.class);
                        intent.putExtra(BUNDLE_WALLET_BALANCE, mViewModel.getTotalWalletBalance().getValue());
                        intent.putExtra(HomeFragment.BUNDLE_TRANSFER_TYPE, transferType);
                        break;
                    case TransferTypeDescriptor
                            .FUND_WALLET:
                        intent = new Intent(getActivity(), FundWalletActivity.class);
                        intent.putExtra(BUNDLE_WALLET_BALANCE, mViewModel.getTotalWalletBalance().getValue());
                        break;
                    case TransferTypeDescriptor
                            .SEND_CRYPTO:
                        intent = new Intent(getActivity(), SendActivity.class);
                        intent.putExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE, Util.TradeTypes.SEND);
                        break;
                    case TransferTypeDescriptor
                            .RECEIVE_CRYPTO:
                        intent = new Intent(getActivity(), ReceiveCryptoActivity.class);
                        intent.putExtra(SelectCoinsFragment.BUNDLE_TRADE_TYPE, Util.TradeTypes.RECEIVE);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + transferType);
                }
                startActivity(intent);
            }
        });

        /* Observe assetToBuy field to see if buy option is clicked
         * Open buy dialog fragment
         */
        mViewModel.getAssetToBuy().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //showJoinWaitlist(s);
            }
        });

        /* Observe assetToBuy field to see if buy option is clicked
         * Open receive dialog fragment
         */
        mViewModel.getAssetToReceive().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //showJoinWaitlist();
            }
        });

        /* Observe if buy or receive clicked
         * Open receive dialog fragment
         */
        mViewModel.getJoinWaitList().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showJoinWaitlist(s);
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

        mViewModel.getDeepLinkData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                handleDeepLink(uri);
            }
        });

        mViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User s) {
                status = s.getStatus();
                new SharedPref().setUser(s);
            }
        });


        tradeHomeViewModel.getBalances().observe(getViewLifecycleOwner(), new Observer<Balances>() {
            @Override
            public void onChanged(Balances balances) {
                new SharedPref().setCryptoBalanceUpdated(true);
                mViewModel.setTotalWalletBalance(balances.getCoinsTotal());
            }
        });
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
        if (mViewModel != null) {
            //mViewModel.init();
            //initRecyclerView();
        }
        if (tradeHomeViewModel != null)
            if (tradeHomeViewModel.getBalances().getValue() == null || !new SharedPref().isCryptoBalanceUpdated()) {
                tradeHomeViewModel.getCryptoWalletBalance();
            }
    }

    private void initRecyclerView() {
      /*  binding.homeRecentTransactionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BookingsListAdapter(getActivity(), this::onClick);

        mViewModel.getRecentTransactions().observe(getViewLifecycleOwner(), pagedList -> {
            adapter.submitList(pagedList);
        });
        mViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            adapter.setNetworkState(networkState);
        });

        binding.homeRecentTransactionList.setAdapter(adapter);*/
    }

    public void handleDeepLink(Uri data) {
        if (data == null) return;

        boolean isHttpsScheme = data.getScheme().equals("https");
        Integer storyIdSegment;
        if (isHttpsScheme) storyIdSegment = 1;else storyIdSegment = 0;
        binding.channelRowView.openStory(data.getPathSegments().get(storyIdSegment), true, new Function1<Error, Unit>() {
            @Override
            public Unit invoke(Error error) {
                error.printStackTrace();
                return null;
            }
        });
    }

    private void shimmerBalanceView(boolean visibility) {
        binding.shimmerViewContainer.setVisibility(visibility ? View.VISIBLE : View.GONE);
        binding.homeWalletBalance.setVisibility(visibility ? View.INVISIBLE : View.VISIBLE);

    }

    @Override
    public void onStoryDataLoadStarted() {

    }

    @Override
    public void onStoryDataLoadComplete(boolean b, @Nullable Error error, int i) {
        //Toast.makeText(getActivity(), String.valueOf(i), Toast.LENGTH_SHORT).show();
        /*if (i == 0) {
            binding.channelRowView.setVisibility(View.GONE);
        } else {
            binding.channelRowView.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onStoryDismissed() {

    }

    @Override
    public void onUserActivityOccurred(@NonNull UserActivity.EventType eventType, @NonNull UserActivityData userActivityData) {

    }

    @Override
    public void getAdsForRow(@NonNull List<ClientStory> list, @NonNull Function1<? super List<ClientAd>, Unit> function1) {

    }

    @Override
    public void userSwipedUpToApp(@NonNull String s) {

    }

    @Override
    public void tileBecameVisible(int i) {

    }

}