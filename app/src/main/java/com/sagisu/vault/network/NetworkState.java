package com.sagisu.vault.network;

public class NetworkState {

    public enum Status{
        RUNNING,
        SUCCESS,
        SUCCESS_EMPTY,
        FAILED,
        SHOW_SNACKBAR
    }


    private final Status status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;
    public static final NetworkState SUCCESS_EMPTY;
    public static final NetworkState SHOW_SNACKBAR;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED=new NetworkState(Status.SUCCESS,"Success");
        LOADING=new NetworkState(Status.RUNNING,"Running");
        SUCCESS_EMPTY=new NetworkState(Status.SUCCESS_EMPTY,"Empty");
        SHOW_SNACKBAR=new NetworkState(Status.SHOW_SNACKBAR,"Show snackbar");
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
