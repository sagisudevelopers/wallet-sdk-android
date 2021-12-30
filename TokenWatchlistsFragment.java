package com.sagisu.vault.ui.trade.watchlists;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sagisu.vault.ui.trade.watchlists.R;

public class TokenWatchlistsFragment extends Fragment {

    private TokenWatchListsViewModel mViewModel;

    public static TokenWatchlistsFragment newInstance() {
        return new TokenWatchlistsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.token_watchlists_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TokenWatchListsViewModel.class);
        // TODO: Use the ViewModel
    }

}