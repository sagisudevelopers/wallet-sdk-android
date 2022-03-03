package com.sagisu.vault.ui.businessprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.sagisu.vault.databinding.ListItemBinding;
import com.sagisu.vault.databinding.TransactionItemBinding;
import com.sagisu.vault.databinding.VaultNetworkItemBinding;
import com.sagisu.vault.models.Business;
import com.sagisu.vault.models.MyBusinessVault;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.network.VaultNetworkState;

import java.util.ArrayList;
import java.util.List;

public class AllBusinessListAdapter extends PagingDataAdapter<Business, RecyclerView.ViewHolder> {
    private static final int TYPE_PROGRESS = 0;
    private static final int TYPE_ITEM = 1;
    private IBusinessClickListener listener;
    private List<MyBusinessVault> myBusinessVaultList = new ArrayList<>();

    private Context context;
    private VaultNetworkState vaultNetworkState;

    public AllBusinessListAdapter(Context context, IBusinessClickListener listener) {
        super(Business.DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    public void setMyBusinessVaultList(List<MyBusinessVault> myBusinessVaultList) {
        this.myBusinessVaultList = myBusinessVaultList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_PROGRESS) {
            VaultNetworkItemBinding headerBinding = VaultNetworkItemBinding.inflate(layoutInflater, parent, false);
            NetworkStateItemViewHolder viewHolder = new NetworkStateItemViewHolder(headerBinding);
            return viewHolder;

        } else {
            ListItemBinding itemBinding = ListItemBinding.inflate(layoutInflater, parent, false);
            CustomerItemViewHolder viewHolder = new CustomerItemViewHolder(itemBinding);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomerItemViewHolder) {
            ((CustomerItemViewHolder) holder).bindTo(getItem(position));
        } else {
            ((NetworkStateItemViewHolder) holder).bindView(vaultNetworkState);
        }
    }


    private boolean hasExtraRow() {
        if (vaultNetworkState != null && vaultNetworkState != VaultNetworkState.LOADED) {
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

    public void setNetworkState(VaultNetworkState newVaultNetworkState) {
        VaultNetworkState previousState = this.vaultNetworkState;
        boolean previousExtraRow = hasExtraRow();
        this.vaultNetworkState = newVaultNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newVaultNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }


    public class CustomerItemViewHolder extends RecyclerView.ViewHolder {

        private ListItemBinding binding;

        public CustomerItemViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindTo(Business transaction) {
            binding.setModel(transaction);
            MyBusinessVault business = new MyBusinessVault(transaction);
            MyBusinessVault myBusiness = null;
            int myBusinessIndex = myBusinessVaultList.indexOf(business);
            if (myBusinessIndex != -1) myBusiness = myBusinessVaultList.get(myBusinessIndex);

            binding.setMyBusiness(myBusiness == null ? null : myBusiness);

            binding.joinBusiness.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.itemClick(view, transaction);
                }
            });

        }
    }


    public class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {

        private VaultNetworkItemBinding binding;

        public NetworkStateItemViewHolder(VaultNetworkItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(VaultNetworkState vaultNetworkState) {
            if (vaultNetworkState != null && vaultNetworkState.getStatus() == VaultNetworkState.Status.RUNNING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }

            if (vaultNetworkState != null && vaultNetworkState.getStatus() == VaultNetworkState.Status.FAILED) {
                binding.errorMsg.setVisibility(View.VISIBLE);
                binding.errorMsg.setText(vaultNetworkState.getMsg());
            } else {
                binding.errorMsg.setVisibility(View.GONE);
            }
        }
    }
}
