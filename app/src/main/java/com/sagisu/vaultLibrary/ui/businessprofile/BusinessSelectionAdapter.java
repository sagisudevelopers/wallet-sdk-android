package com.sagisu.vaultLibrary.ui.businessprofile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.sagisu.vaultLibrary.databinding.ItemBusinessProfileSelection;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.utils.SharedPref;

import java.util.List;

public class BusinessSelectionAdapter extends RecyclerView.Adapter<BusinessSelectionAdapter.MyViewHolder> {

    private List<MyBusinessVault> businessList;
    private LayoutInflater inflater;
    private IBusinessClickListener listener;
    private BusinessProfileViewModel viewModel;
    private static boolean isBusinessProfile;

    private static MaterialRadioButton lastChecked = null;


    public void setBusinessProfile(boolean businessProfile) {
        isBusinessProfile = businessProfile;
    }

    public BusinessSelectionAdapter(Context context, List<MyBusinessVault> messageBeanList, IBusinessClickListener listener) {
        inflater = ((Activity) context).getLayoutInflater();
        this.businessList = messageBeanList;
        this.listener = listener;
    }

    @Override
    public BusinessSelectionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemBusinessProfileSelection itemBinding = ItemBusinessProfileSelection.inflate(layoutInflater, parent, false);
        BusinessSelectionAdapter.MyViewHolder viewHolder = new BusinessSelectionAdapter.MyViewHolder(itemBinding, listener, viewModel);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BusinessSelectionAdapter.MyViewHolder holder, int position) {
        MyBusinessVault bean = businessList.get(position);
        holder.bindTo(bean);
    }

    @Override
    public int getItemCount() {
        return businessList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemBusinessProfileSelection binding;
        private IBusinessClickListener listener;
        private Business prevVaultSelection;

        MyViewHolder(ItemBusinessProfileSelection binding, IBusinessClickListener listener, BusinessProfileViewModel viewModel) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            prevVaultSelection = new SharedPref().getBusinessVaultSelected();
        }

        public void bindTo(MyBusinessVault messageBean) {
            binding.setModel(messageBean.getBusiness());
            binding.setStatus(messageBean.getStatus());
            binding.setBusinessProfile(isBusinessProfile);
           /* binding.setMessageBean(messageBean);
            binding.messageRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageClicked(messageBean);
                }
            });*/
            if (prevVaultSelection != null && prevVaultSelection.equals(messageBean.getBusiness())) {
                lastChecked = binding.businessRb;
                binding.businessRb.setChecked(true);
            }
            binding.businessRb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialRadioButton checked_rb = (MaterialRadioButton) view;
                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                    }
                    lastChecked = checked_rb;
                    listener.itemClick(view, messageBean.getBusiness());
                }
            });
        }

    }
}


