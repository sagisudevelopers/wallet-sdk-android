package com.sagisu.vaultLibrary.ui.login.fragments;

public class LoginFormError {
    private Integer phoneError;
    private Integer passwordError;
    private Integer confirmPasswordError;
    private Integer otpError;
    private Integer totpError;
    private Integer totpRecoveryCodeError;
    private String toastMsg;

    public Integer getPhoneError() {
        return phoneError;
    }

    public void setPhoneError(Integer phoneError) {
        this.phoneError = phoneError;
    }

    public Integer getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(Integer passwordError) {
        this.passwordError = passwordError;
    }

    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public void setConfirmPasswordError(Integer confirmPasswordError) {
        this.confirmPasswordError = confirmPasswordError;
    }

    public Integer getOtpError() {
        return otpError;
    }

    public void setOtpError(Integer otpError) {
        this.otpError = otpError;
    }

    public Integer getTotpError() {
        return totpError;
    }

    public void setTotpError(Integer totpError) {
        this.totpError = totpError;
    }

    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }

    public Integer getTotpRecoveryCodeError() {
        return totpRecoveryCodeError;
    }

    public void setTotpRecoveryCodeError(Integer totpRecoveryCodeError) {
        this.totpRecoveryCodeError = totpRecoveryCodeError;
    }
}
