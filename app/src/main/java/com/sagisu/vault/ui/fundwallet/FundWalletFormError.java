package com.sagisu.vault.ui.fundwallet;

public class FundWalletFormError {
    private Integer amountError;
    private Integer accountError;
    private String toastMsg;

    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }

    public Integer getAmountError() {
        return amountError;
    }

    public void setAmountError(Integer amountError) {
        this.amountError = amountError;
    }

    public Integer getAccountError() {
        return accountError;
    }

    public void setAccountError(Integer accountError) {
        this.accountError = accountError;
    }
}
