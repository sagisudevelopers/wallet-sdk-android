package com.sagisu.vault.ui.trade.receive;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
        String base64String = qrCodeUri;
        base64String = base64String.substring(base64String.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
