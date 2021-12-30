package com.sagisu.vault.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DiffUtil;

import com.sagisu.vault.ui.login.fragments.User;

public class Transaction extends BaseObservable implements Parcelable {
    private String id;
    private double amount;
    private String currencyCode;
    private long datetime;
    private User sender;
    private User receiver;
    private String debitAccountNumber;
    private String debitAccountMask;
    private String creditAccountNumber;
    private String creditAccountMask;
    private String status;
    private String type;
    private String txHash;
    private FeeInfo feeInfo;

    protected Transaction(Parcel in) {
        id = in.readString();
        amount = in.readDouble();
        currencyCode = in.readString();
        datetime = in.readLong();
        sender = in.readParcelable(User.class.getClassLoader());
        receiver = in.readParcelable(User.class.getClassLoader());
        debitAccountNumber = in.readString();
        debitAccountMask = in.readString();
        creditAccountNumber = in.readString();
        creditAccountMask = in.readString();
        status = in.readString();
        type = in.readString();
        txHash = in.readString();
        feeInfo = in.readParcelable(FeeInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(amount);
        dest.writeString(currencyCode);
        dest.writeLong(datetime);
        dest.writeParcelable(sender, flags);
        dest.writeParcelable(receiver, flags);
        dest.writeString(debitAccountNumber);
        dest.writeString(debitAccountMask);
        dest.writeString(creditAccountNumber);
        dest.writeString(creditAccountMask);
        dest.writeString(status);
        dest.writeString(type);
        dest.writeString(txHash);
        dest.writeParcelable(feeInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    @Bindable
    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getDebitAccountNumber() {
        return debitAccountNumber;
    }

    public void setDebitAccountNumber(String debitAccountNumber) {
        this.debitAccountNumber = debitAccountNumber;
    }

    public String getDebitAccountMask() {
        return debitAccountMask;
    }

    public void setDebitAccountMask(String debitAccountMask) {
        this.debitAccountMask = debitAccountMask;
    }

    public String getCreditAccountNumber() {
        return creditAccountNumber;
    }

    public void setCreditAccountNumber(String creditAccountNumber) {
        this.creditAccountNumber = creditAccountNumber;
    }

    public String getCreditAccountMask() {
        return creditAccountMask;
    }

    public void setCreditAccountMask(String creditAccountMask) {
        this.creditAccountMask = creditAccountMask;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    @Bindable
    public FeeInfo getFeeInfo() {
        return feeInfo;
    }

    public static DiffUtil.ItemCallback<Transaction> getDiffCallback() {
        return DIFF_CALLBACK;
    }

    public static void setDiffCallback(DiffUtil.ItemCallback<Transaction> diffCallback) {
        DIFF_CALLBACK = diffCallback;
    }

    //User for pagination
    public static DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.equals(newItem);
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Transaction article = (Transaction) obj;
        return article.id.equals(this.id);
    }
}
