package com.sagisu.vaultLibrary.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.sagisu.vaultLibrary.ui.login.fragments.User;

public class BusinessRequest {
    String _id;
    Business business;
    User user;
    String requestType;
    String status;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static DiffUtil.ItemCallback<BusinessRequest> getDiffCallback() {
        return DIFF_CALLBACK;
    }

    public static void setDiffCallback(DiffUtil.ItemCallback<BusinessRequest> diffCallback) {
        DIFF_CALLBACK = diffCallback;
    }

    //User for pagination
    public static DiffUtil.ItemCallback<BusinessRequest> DIFF_CALLBACK = new DiffUtil.ItemCallback<BusinessRequest>() {
        @Override
        public boolean areItemsTheSame(@NonNull BusinessRequest oldItem, @NonNull BusinessRequest newItem) {
            return oldItem._id.equals(newItem._id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull BusinessRequest oldItem, @NonNull BusinessRequest newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        BusinessRequest article = (BusinessRequest) obj;
        return article._id.equals(this._id);
    }
}
