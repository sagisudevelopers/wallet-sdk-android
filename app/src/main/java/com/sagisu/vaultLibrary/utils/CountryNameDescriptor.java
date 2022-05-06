package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CountryNameDescriptor {
    public static final String US = "US";
    public static final String IN = "INDIA";

    @StringDef({US,IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CountryNames{}

}
