package com.sagisu.vault.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class OtpTypeDescriptor {
    public static final String GENERATE = "GENERATE";
    public static final String VERIFY = "VERIFY";
    public static final String RESEND = "RESEND";

    @StringDef({GENERATE,VERIFY,RESEND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OtpTypes {}

}
