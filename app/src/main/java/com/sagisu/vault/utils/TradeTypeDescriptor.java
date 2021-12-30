package com.sagisu.vault.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TradeTypeDescriptor {
    public static final String BUY = "Buy";
    public static final String SEND = "Send";
    public static final String RECEIVE = "Receive";
    public static final String SWAP = "Swap";

    @StringDef({BUY,SEND,RECEIVE,SWAP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TradeTypes {}

}
