package com.sagisu.vault.ui.transfer;

public class AccountTransferFormError {
    private Integer amountError;
    private Integer depositToAccountError;
    private String toastError;

    public Integer getAmountError() {
        return amountError;
    }

    public void setAmountError(int amountError) {
        this.amountError = amountError;
    }

    public Integer getDepositToAccountError() {
        return depositToAccountError;
    }

    public void setDepositToAccountError(int depositToAccountError) {
        this.depositToAccountError = depositToAccountError;
    }

    public String getToastError() {
        return toastError;
    }

    public void setToastError(String toastError) {
        this.toastError = toastError;
    }
}
