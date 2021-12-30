package com.sagisu.vault.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MessageBean extends BaseObservable{
    private String account;
    private String message;
    private long timestamp;
    private int background;
    private boolean beSelf;
    private String paymentId;

    public MessageBean(String account, String message, long timestamp, String payment, boolean beSelf) {
        this.account = account;
        this.message = message;
        this.timestamp = timestamp;
        this.beSelf = beSelf;
        this.paymentId = payment;
    }

    @Bindable
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Bindable
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Bindable
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isBeSelf() {
        return beSelf;
    }

    public void setBeSelf(boolean beSelf) {
        this.beSelf = beSelf;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }
}
