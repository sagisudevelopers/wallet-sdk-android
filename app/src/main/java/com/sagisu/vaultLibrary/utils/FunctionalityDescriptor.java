package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FunctionalityDescriptor {
    public static final String FUND_WALLET = "fund wallet";
    public static final String ACCOUNT_TRANSFER = "account transfer";
    public static final String CONTACT_TRANSFER = "contact transfer";

    @StringDef({FUND_WALLET,ACCOUNT_TRANSFER,CONTACT_TRANSFER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Functionality {}

}
