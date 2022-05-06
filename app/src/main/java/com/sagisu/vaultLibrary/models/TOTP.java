package com.sagisu.vaultLibrary.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.sagisu.vaultLibrary.BR;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class TOTP extends BaseObservable {
    String qr_code;
    String secret;
    List<String> recovery_codes = new ArrayList<>();

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public List<String> getRecovery_codes() {
        return recovery_codes;
    }

    public void setRecovery_codes(List<String> recovery_codes) {
        this.recovery_codes = recovery_codes;
    }

    public Bitmap getQrCodeBitmap(){
        return Util.getQrCodeFromBitmap(qr_code);
    }
}

