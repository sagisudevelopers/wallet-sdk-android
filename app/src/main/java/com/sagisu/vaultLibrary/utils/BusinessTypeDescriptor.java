package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BusinessTypeDescriptor {
    public static final String BUSINESS = "Business";
    public static final String DAO = "Dao";

    @StringDef({BUSINESS, DAO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BusinessTypes {
    }

}
