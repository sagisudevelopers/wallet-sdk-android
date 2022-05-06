package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AccountTypeDescriptor {
    public static final String DEBIT = "Debit";
    public static final String CREDIT = "Credit";

    @StringDef({DEBIT,CREDIT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AccountTypes {}

}
