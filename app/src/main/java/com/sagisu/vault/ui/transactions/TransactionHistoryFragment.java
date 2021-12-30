package com.sagisu.vault.ui.transactions;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sagisu.vault.R;
import com.sagisu.vault.TransactionDetailsActivity;
import com.sagisu.vault.databinding.TransactionHistoryFragmentBinding;
import com.sagisu.vault.models.Transaction;

import autodispose2.AutoDispose;
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider;

public class TransactionHistoryFragment extends Fragment implements View.OnClickListener, TransactionListAdapter.ITransactionClickListener {
    private TransactionHistoryViewModel mViewModel;
    private TransactionHistoryFragmentBinding binding;
   // private TransactionHistoryAdapter adapter;
   private TransactionListAdapter adapter;

    public static TransactionHistoryFragment newInstance() {
        return new TransactionHistoryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transaction_history_fragment, container, false);
        binding = DataBindingUtil.bind(rootView);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TransactionHistoryViewModel.class);
        mViewModel.getTransactions("");
        initRecyclerView();
    }

    private void initRecyclerView() {
        binding.transactionHistoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionListAdapter(getActivity(), this);
        /*adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getSource().getRefresh() instanceof LoadState.NotLoading && loadStates.getAppend().getEndOfPaginationReached() && adapter.getItemCount() < 1) {
                binding.transactionHistoryList.setVisibility(View.GONE);
                binding.loading.setVisibility(View.VISIBLE);
            } else {
                binding.transactionHistoryList.setVisibility(View.VISIBLE);
                binding.loading.setVisibility(View.GONE);
            }
            return null;
        });*/
        adapter.withLoadStateFooter(
                new TransactionLoadStateAdapter(this));
        binding.transactionHistoryList.setAdapter(adapter);

        mViewModel.getFlowable()
                // Using AutoDispose to handle subscription lifecycle.
                // See: https://github.com/uber/AutoDispose
                .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(pagingData -> adapter.submitData(getLifecycle(), pagingData));
    }

   /* private void initRecyclerView() {
        binding.transactionHistoryList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TransactionHistoryAdapter(getActivity(), this);

        mViewModel.getTransactions().observe(getViewLifecycleOwner(), pagedList -> {
            adapter.submitList(pagedList);
        });
        mViewModel.getNetworkState().observe(getViewLifecycleOwner(), networkState -> {
            adapter.setNetworkState(networkState);
        });

        binding.transactionHistoryList.setAdapter(adapter);
    }
*/
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.retry_button) {
            adapter.retry();
        }
    }

    @Override
    public void transactionClick(View view, Transaction transaction) {
        Intent intent = new Intent(getActivity(), TransactionDetailsActivity.class);
        intent.putExtra(TransactionDetailsActivity.BUNDLE_TRANSACTION,transaction);
        startActivity(intent);
    }
}