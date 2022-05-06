package com.sagisu.vaultLibrary.ui.login.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.SerializedName;
import com.sagisu.vaultLibrary.BR;
import com.sagisu.vaultLibrary.models.Business;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseObservable implements Parcelable, Cloneable {
    @SerializedName("id")
    private String id;
    @SerializedName("fullName")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    private String password;
    private String confirmPassword;
    private String fcmToken;
    private String status;
    private StatusMeta statusMetadata;
    private String defaultBusinessId;
    private String vaultAccountId;
    private List<Business> businessVaults = new ArrayList<>();

    public User() {

    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        password = in.readString();
        confirmPassword = in.readString();
        fcmToken = in.readString();
        status = in.readString();
        statusMetadata = in.readParcelable(StatusMeta.class.getClassLoader());
        defaultBusinessId = in.readString();
        vaultAccountId = in.readString();
        businessVaults = in.createTypedArrayList(Business.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    //Getter methods
    @Bindable
    public String getName() {
        return name;
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusMeta getStatusMetadata() {
        return statusMetadata;
    }

    public void setStatusMetadata(StatusMeta statusMetadata) {
        this.statusMetadata = statusMetadata;
    }

    public String getFullName() {
        if (firstName == null && lastName == null) return null;
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName.concat(" ").concat(lastName);
    }

    public void setFullName(String fullName) {
        String[] nameArray = fullName.split(" ");
        setFirstName(nameArray[0]);
        for (int i = 1; i < nameArray.length; i++) {
            lastName = lastName.concat(" ").concat(nameArray[i]);
        }
    }

    public String getDefaultBusinessId() {
        return defaultBusinessId;
    }

    public void setDefaultBusinessId(String defaultBusinessId) {
        this.defaultBusinessId = defaultBusinessId;
    }

    public String getVaultAccountId() {
        return vaultAccountId;
    }

    public void setVaultAccountId(String vaultAccountId) {
        this.vaultAccountId = vaultAccountId;
    }

    public List<Business> getBusinessVaults() {
        return businessVaults;
    }

    public void setBusinessVaults(List<Business> businessVaults) {
        this.businessVaults = businessVaults;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(phone);
        parcel.writeString(email);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(password);
        parcel.writeString(confirmPassword);
        parcel.writeString(fcmToken);
        parcel.writeString(status);
        parcel.writeParcelable(statusMetadata, i);
        parcel.writeString(defaultBusinessId);
        parcel.writeString(vaultAccountId);
        parcel.writeTypedList(businessVaults);
    }

    public class StatusMeta implements Parcelable {
        Integer amount;
        String transaction;

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getTransaction() {
            return transaction;
        }

        public void setTransaction(String transaction) {
            this.transaction = transaction;
        }

        protected StatusMeta(Parcel in) {
            if (in.readByte() == 0) {
                amount = null;
            } else {
                amount = in.readInt();
            }
            transaction = in.readString();
        }

        public final Creator<StatusMeta> CREATOR = new Creator<StatusMeta>() {
            @Override
            public StatusMeta createFromParcel(Parcel in) {
                return new StatusMeta(in);
            }

            @Override
            public StatusMeta[] newArray(int size) {
                return new StatusMeta[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            if (amount == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(amount);
            }
            parcel.writeString(transaction);
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
