package com.sagisu.vault.ui.login.fragments;

public class LoginFormError {
    private Integer phoneError;
    private Integer passwordError;
    private Integer confirmPasswordError;
    private Integer otpError;
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

    public String getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg = toastMsg;
    }
}
