package com.sagisu.vaultLibrary.ui.login.fragments;

import com.sagisu.vaultLibrary.models.TOTP;

public class LoginResponse {
    private String token;
    private User user;
    private TOTP totp;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TOTP getTotp() {
        return totp;
    }

    public void setTotp(TOTP totp) {
        this.totp = totp;
    }
}
