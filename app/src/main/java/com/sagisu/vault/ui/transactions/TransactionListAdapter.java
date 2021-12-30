package com.sagisu.vault.ui.transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sagisu.vault.databinding.NetworkItemBinding;
import com.sagisu.vault.databinding.TransactionItemBinding;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.network.NetworkState;

public class TransactionListAdapter extends PagingDataAdapter<Transaction, RecyclerView.ViewHolder> {

    interface ITransactionClickListener{
        void transactionClick(View view, Transaction transaction);
    }
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private ITransactionClickListener listener;

    private Context context;
    private NetworkState networkState;

    public TransactionListAdapter(Context context, ITransactionClickListener listener) {
        super(Transaction.DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            NetworkItemBinding headerBinding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
            return viewHolder;

        } else {
            TransactionItemBinding itemBinding = TransactionItemBinding.inflate(layoutInflater, parent, false);
            CustomerItemViewHolder viewHolder = new CustomerItemViewHolder(itemBinding);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomerItemViewHolder) {
            ((CustomerItemViewHolder) holder).bindTo(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(networkState);
        }
    }


    private boolean hasExtraRow() {
        if (networkState != null && networkState != NetworkState.LOADED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    public class CustomerItemViewHolder extends RecyclerView.ViewHolder {

        private TransactionItemBinding binding;

        public CustomerItemViewHolder(TransactionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(Transaction transaction) {
           binding.setModel(transaction);

            binding.transactionRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.transactionClick(view,transaction);
                }
            });

        }
    }


    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private NetworkItemBinding binding;

        public NetworkStateItemViewHolder(NetworkItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(NetworkState networkState) {
            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(networkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }
}
