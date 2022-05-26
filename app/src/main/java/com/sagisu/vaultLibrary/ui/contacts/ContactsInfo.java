package com.sagisu.vaultLibrary.ui.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class ContactsInfo extends BaseObservable implements Parcelable {
    private String contactId;
    private String displayName;
    private String phoneNumber;

    public ContactsInfo(){

    }
    protected ContactsInfo(Parcel in) {
        contactId = in.readString();
        displayName = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<ContactsInfo> CREATOR = new Creator<ContactsInfo>() {
        @Override
        public ContactsInfo createFromParcel(Parcel in) {
            return new ContactsInfo(in);
        }

        @Override
        public ContactsInfo[] newArray(int size) {
            return new ContactsInfo[size];
        }
    };

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    @Bindable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(contactId);
        parcel.writeString(displayName);
        parcel.writeString(phoneNumber);
    }
}
