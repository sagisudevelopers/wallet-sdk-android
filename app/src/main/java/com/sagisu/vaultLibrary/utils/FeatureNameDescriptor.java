package com.sagisu.vaultLibrary.utils;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FeatureNameDescriptor {
    public static final String SEND_CRYPTO = "Send Crypto";
    public static final String BUY_CRYPTO = "Buy Crypto";
    public static final String SELL_CRYPTO = "Sell Crypto";
    public static final String RECEIVE_CRYPTO = "Receive Crypto";
    public static final String BUY_NFT = "Buy NFT";
    public static final String RECEIVE_NFT = "Receive NFT";
    public static final String BUY_STOCKS = "Buy Stocks";
    public static final String RECEIVE_STOCKS = "Receive Stocks";
    public static final String BUY_INSURANCE = "Buy Insurance";
    public static final String RECEIVE_INSURANCE = "Receive Insurance";
    public static final String TRADE = "Trade";
    public static final String PORTFOLIO = "Portfolio";
    public static final String TOKENIZE_WILL_TRUST = "Tokenize will and trust";
    public static final String SWAP_CRYPTO = "Swap Crypto";
    public static final String TRANSFER_TO_ACCOUNT = "Transfer to account";
    public static final String TRANSFER_TO_SELF = "Transfer to self";
    public static final String FUND_WALLET = "Fund wallet";
    public static final String TRANSFER_TO_CONTACT = "Transfer to contact";

    @StringDef({BUY_CRYPTO,RECEIVE_CRYPTO,BUY_NFT,RECEIVE_NFT,BUY_STOCKS,RECEIVE_STOCKS,BUY_INSURANCE,RECEIVE_INSURANCE,TRADE,PORTFOLIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FeatureNameTypes {}

}
