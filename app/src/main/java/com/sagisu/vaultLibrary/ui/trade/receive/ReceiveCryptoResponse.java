package com.sagisu.vaultLibrary.ui.trade.receive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.sagisu.vaultLibrary.utils.Util;

public class ReceiveCryptoResponse {
    private String qrCodeUri;
    private String address;
    private String assetId;
    private boolean isNew;

    public String getQrCodeUri() {
        return qrCodeUri;
    }

    public void setQrCodeUri(String qrCodeUri) {
        this.qrCodeUri = qrCodeUri;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAssetId() {
        return assetId;
    }

    public boolean isNew() {
        return isNew;
    }

    public Bitmap getQrCodeBitmap(){
        return Util.getQrCodeFromBitmap(qrCodeUri);
    }
}
