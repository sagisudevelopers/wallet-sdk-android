package com.sagisu.vaultLibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.snackbar.Snackbar;
import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.ui.login.VaultLoginActivity;
import com.sagisu.vaultLibrary.ui.login.fragments.User;

public class Util {
    public static final String phone_prefix = "+91";

    public enum TradeTypes {
        BUY,
        SEND,
        RECEIVE,
        SWAP
    }

    public static void showSnackBar(String msg, Activity activity) {
        if (null != activity && null != msg) {
            Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setActionTextColor(activity.getResources().getColor(R.color.red_500))
                    .show();
        }
    }

    public static void showSnackBar(String msg) {
        Activity activity = AppManager.getAppManager().currentActivity();
        if (null != activity && null != msg) {
            Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setActionTextColor(activity.getResources().getColor(R.color.red_500))
                    .show();
        }
    }

    public static void showUnAuthorizedSnackBar() {
        Activity activity = AppManager.getAppManager().currentActivity();
        if (null != activity) {
            Snackbar.make(activity.findViewById(android.R.id.content), "UnAuthorized, Please login back to use the application", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(activity.getResources().getColor(R.color.red_500))
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new SharedPref().clearSharedPref();
                            Intent intent = new Intent(activity, VaultLoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }).show();
        }
    }

    public static boolean hasPermission(String functionality, Context context) {
        boolean flag = true;

        if (new SharedPref().getToken() == null)
            return false;

        User user = new SharedPref().getUser();


        return hasPermission(functionality, user.getStatus());
    }

    public static boolean hasPermission(String functionality, String userStatus) {
        boolean flag = true;
        /*ArrayList<String> allowed = new ArrayList<>();
        switch (userStatus) {
            case "Funding failed":
            case "UnVerified":
                allowed.add(TransferTypeDescriptor.FUND_WALLET);
                break;
            case "Verified":
                allowed.add(TransferTypeDescriptor.FUND_WALLET);
                allowed.add(TransferTypeDescriptor.TO_ACCOUNT);
                allowed.add(TransferTypeDescriptor.TO_CONTACT);
                allowed.add(TransferTypeDescriptor.TO_SELF);
            default:
                return true;
        }

        return allowed.contains(functionality);*/
        return flag;
    }

    public static void setClipboard(String text) {
        Activity activity = AppManager.getAppManager().currentActivity();
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        showSnackBar("Copied");
    }

    public static Bitmap getQrCodeFromBitmap(String qrCodeUri){
        String base64String = qrCodeUri;
        base64String = base64String.substring(base64String.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static void hideKeyboard(){
        try {
            Activity context = AppManager.getAppManager().currentActivity();
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
