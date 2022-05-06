package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BusinessRequestTypeDescriptor {
    public static final String JOIN_USER = "Join User";
    public static final String VERIFY_BUSINESS = "Verify Business";

    @StringDef({JOIN_USER,VERIFY_BUSINESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BusinessRequestTypes {}

}
