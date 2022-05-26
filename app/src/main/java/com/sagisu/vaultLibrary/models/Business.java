package com.sagisu.vaultLibrary.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;

import com.sagisu.vaultLibrary.utils.BusinessTypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class Business extends BaseObservable implements Parcelable {
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


    protected Business(Parcel in) {
        name = in.readString();
        _id = in.readString();
        einNumber = in.readString();
        ethAddress = in.readString();
        address = in.readString();
        corporationType = in.readString();
        type = in.readString();
        department = in.readString();
        defaultCrypto = in.readString();
        directors = in.createTypedArrayList(Director.CREATOR);
        vaultAccountId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(_id);
        dest.writeString(einNumber);
        dest.writeString(ethAddress);
        dest.writeString(address);
        dest.writeString(corporationType);
        dest.writeString(type);
        dest.writeString(department);
        dest.writeString(defaultCrypto);
        dest.writeTypedList(directors);
        dest.writeString(vaultAccountId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Business> CREATOR = new Creator<Business>() {
        @Override
        public Business createFromParcel(Parcel in) {
            return new Business(in);
        }

        @Override
        public Business[] newArray(int size) {
            return new Business[size];
        }
    };

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



    public static class Director extends BaseObservable implements Parcelable{
        private String name;
        private String phone;
        private String countryCode;

        public Director(){

        }
        protected Director(Parcel in) {
            name = in.readString();
            phone = in.readString();
            countryCode = in.readString();
        }

        public static final Creator<Director> CREATOR = new Creator<Director>() {
            @Override
            public Director createFromParcel(Parcel in) {
                return new Director(in);
            }

            @Override
            public Director[] newArray(int size) {
                return new Director[size];
            }
        };

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

        @Bindable
        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
            notifyPropertyChanged(BR.countryCode);
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(name);
            parcel.writeString(phone);
            parcel.writeString(countryCode);
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

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
