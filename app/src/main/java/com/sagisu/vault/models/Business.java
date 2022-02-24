package com.sagisu.vault.models;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;

import com.sagisu.vault.utils.BusinessTypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class Business extends BaseObservable {
    private String name;
    private String _id;
    private String einNumber;
    private String ethAddress;
    private String address;
    private String corporationType;
    private String type;
    private String department;
    private String defaultCrypto;
    private List<Director> directors = new ArrayList<>();
    private String vaultAccountId;

    public Business() {
    }

    public Business(String name) {
        this.name = name;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Bindable
    public String getEinNumber() {
        return einNumber;
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
        notifyPropertyChanged(BR.ethAddress);
    }

    public void setEinNumber(String einNumber) {
        this.einNumber = einNumber;
        notifyPropertyChanged(BR.einNumber);
    }

    @Bindable
    public String getCorporationType() {
        return corporationType;
    }

    public void setCorporationType(String corporationType) {
        this.corporationType = corporationType;
        notifyPropertyChanged(BR.corporationType);
    }

    @Bindable
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
        notifyPropertyChanged(BR.department);
    }

    @Bindable
    public String getDefaultCrypto() {
        return defaultCrypto;
    }

    public void setDefaultCrypto(String defaultCrypto) {
        this.defaultCrypto = defaultCrypto;
        notifyPropertyChanged(BR.defaultCrypto);
    }

    public List<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(List<Director> directors) {
        this.directors = directors;
    }

    public void setType(@BusinessTypeDescriptor.BusinessTypes String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static class Director extends BaseObservable {
        private String name;
        private String phone;

        @Bindable
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
            notifyPropertyChanged(BR.name);
        }

        @Bindable
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
            notifyPropertyChanged(BR.phone);
        }
    }

    public String getVaultAccountId() {
        return vaultAccountId;
    }

    public void setVaultAccountId(String vaultAccountId) {
        this.vaultAccountId = vaultAccountId;
    }

    public static DiffUtil.ItemCallback<Business> getDiffCallback() {
        return DIFF_CALLBACK;
    }

    public static void setDiffCallback(DiffUtil.ItemCallback<Business> diffCallback) {
        DIFF_CALLBACK = diffCallback;
    }

    //User for pagination
    public static DiffUtil.ItemCallback<Business> DIFF_CALLBACK = new DiffUtil.ItemCallback<Business>() {
        @Override
        public boolean areItemsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return oldItem._id.equals(newItem._id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Business oldItem, @NonNull Business newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Business article = (Business) obj;
        return article._id.equals(this._id);
    }
}
