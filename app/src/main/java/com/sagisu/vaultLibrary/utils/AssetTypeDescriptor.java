package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AssetTypeDescriptor {
    public static final String CRYPTO = "crypto";
    public static final String NFT = "nft";
    public static final String STOCKS = "stocks";
    public static final String INSURANCE = "insurance";

    @StringDef({CRYPTO,NFT,STOCKS,INSURANCE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AssetTypes {}

}
