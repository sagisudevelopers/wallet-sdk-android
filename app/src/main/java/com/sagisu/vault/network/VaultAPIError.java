package com.sagisu.vault.network;


public class VaultAPIError {
    private int statusCode;
    private String message;
    private String code;

    public VaultAPIError() {
    }

    public VaultAPIError(String message, int code) {
        this.message = message;
        this.statusCode = code;
    }

    public int status() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
