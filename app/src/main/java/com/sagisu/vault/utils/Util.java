package com.sagisu.vault.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.sagisu.vault.R;
import com.sagisu.vault.ui.login.LoginActivity;
import com.sagisu.vault.ui.login.fragments.User;

public class Util {
    public static final String phone_prefix = "+1";

    public enum TradeTypes {
        BUY,
        SEND,
        RECEIVE,
        SWAP
    }

    public static void showSnackBar(String msg, Activity activity) {
        if (null != activity && null != msg) {
            Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT)
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
                            new SharedPref(activity.getApplicationContext()).clearSharedPref();
                            Intent intent = new Intent(activity, LoginActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    }).show();
        }
    }

    public static boolean hasPermission(String functionality, Context context) {
        boolean flag = true;

        if (new SharedPref(context).getToken() == null)
            return false;

        User user = new SharedPref(context).getUser();


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
        showSnackBar("Address copied");
    }
}
