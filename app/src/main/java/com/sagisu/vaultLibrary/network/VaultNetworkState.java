package com.sagisu.vaultLibrary.network;

public class VaultNetworkState {

    public enum Status{
        RUNNING,
        SUCCESS,
        SUCCESS_EMPTY,
        FAILED,
        SHOW_SNACKBAR
    }


    private final Status status;
    private final String msg;

    public static final VaultNetworkState LOADED;
    public static final VaultNetworkState LOADING;
    public static final VaultNetworkState SUCCESS_EMPTY;
    public static final VaultNetworkState SHOW_SNACKBAR;

    public VaultNetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED=new VaultNetworkState(Status.SUCCESS,"Success");
        LOADING=new VaultNetworkState(Status.RUNNING,"Running");
        SUCCESS_EMPTY=new VaultNetworkState(Status.SUCCESS_EMPTY,"Empty");
        SHOW_SNACKBAR=new VaultNetworkState(Status.SHOW_SNACKBAR,"Show snackbar");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
