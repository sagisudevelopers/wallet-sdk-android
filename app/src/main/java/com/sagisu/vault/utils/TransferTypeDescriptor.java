package com.sagisu.vault.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TransferTypeDescriptor {
    public static final String TO_ACCOUNT = "walletToAccount";
    public static final String TO_CONTACT = "toContact";
    public static final String TO_SELF = "accountToAccount";
    public static final String FUND_WALLET = "fund";
    public static final String SEND_CRYPTO = "Send crypto";
    public static final String RECEIVE_CRYPTO = "Receive crypto";

    @StringDef({TO_ACCOUNT,TO_CONTACT,FUND_WALLET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransferTypes {}

}
