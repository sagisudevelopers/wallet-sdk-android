package com.sagisu.vaultLibrary.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class Payments extends BaseObservable {
    private Account receiver = new Account();
    private Integer amount = 0;
    private User user;
    private String accountNumber;
    private String toAccountName;
    private String fromAccountName;
    private String transactionType;
    private Boolean retry = false;

    static class User {
        private String legalName;
        private String emailId;
    }

    public Account getReceiver() {
        return receiver;
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    @Bindable
    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getAmountString() {
        if (amount == null) return null;
        return Integer.toString(amount);
    }

    public void setAmountString(String amountString) {
        try {
            int val = Integer.parseInt(amountString);
            this.setAmount(val);
        } catch (NumberFormatException ex) {
            this.setAmount(null);//default value
        }
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public String getFromAccountName() {
        return fromAccountName;
    }

    public void setFromAccountName(String fromAccountName) {
        this.fromAccountName = fromAccountName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    @Bindable
    public Boolean getRetry() {
        return retry;
    }

    public void setRetry(Boolean retry) {
        this.retry = retry;
        notifyPropertyChanged(BR.retry);
    }
}
