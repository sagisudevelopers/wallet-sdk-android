package com.sagisu.vaultLibrary.ui.OTP;

import androidx.annotation.Nullable;

public interface IOtp {
    void verifiedNumber(@Nullable String token);
    void generateOtpSuccess();
    void loading(String text,boolean isLoading);
}
