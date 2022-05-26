package com.sagisu.vaultLibrary.ui.transfer;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;
import com.sagisu.vaultLibrary.models.Account;

public class BankTransferRequest extends BaseObservable {
    private Account debitAccount = new Account();
    /*private String accessToken;
    private String accountId;
    private String institutionName;*/
    private Integer amount = 0;
    private User user;
    private String sourceAccessToken;
    private String sourceInstitutionName;
    private String sourceAccountId;
    private String toAccountName;
    private String fromAccountName;
    private String transferType;

    public String getSourceAccessToken() {
        return sourceAccessToken;
    }

    public void setSourceAccessToken(String sourceAccessToken) {
        this.sourceAccessToken = sourceAccessToken;
    }

    public String getSourceInstitutionName() {
        return sourceInstitutionName;
    }

    public void setSourceInstitutionName(String sourceInstitutionName) {
        this.sourceInstitutionName = sourceInstitutionName;
    }

    public String getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(String sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
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

    public Account getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(Account debitAccount) {
        this.debitAccount = debitAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    static class User {
        private String legalName;
        private String emailId;
    }

    @Bindable
    public void setToAccountName(String accountName) {
        this.toAccountName = accountName;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public String getFromAccountName() {
        return fromAccountName;
    }

    public void setFromAccountName(String fromAccountName) {
        this.fromAccountName = fromAccountName;
    }
}
