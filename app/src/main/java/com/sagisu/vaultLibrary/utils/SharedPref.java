package com.sagisu.vaultLibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.ui.login.fragments.User;

import java.lang.reflect.Type;


public class SharedPref {
    public final String MY_PREFS_NAME = "FLEET_PREF";
    public static final String CURRENT_VAULT_ID = "Current_vault_id";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref() {
        Context context = AppManager.getAppManager().currentActivity();
        sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setValueToSharedPref(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void setBooleanValueToSharedPref(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setToken(String value) {
        //editor.putString("vault_token", "Bearer ".concat(value));
        editor.putString("vault_token", value);
        editor.commit();
    }

    public void setBusinessVaultSelected(Business value) {
        String json = new Gson().toJson(value);
        editor.putString("business_vault_id", json);
        editor.putBoolean("cryptoBalanceUpdated", false);
        editor.commit();
    }

    public void setCryptoBalanceUpdated(Boolean value) {
        editor.putBoolean("cryptoBalanceUpdated", value);
        editor.commit();
    }

    public void setTransactionCursor(String value) {
        editor.putString("tCursor", value);
        editor.commit();
    }

    public void setUser(User user) {
        String json = new Gson().toJson(user);
        editor.putString("user", json);
        editor.commit();
    }

    public void setFullName(String fullName) {
        editor.putString("fullName", fullName);
        editor.commit();
    }


    public void setRefreshTokenToSharedPref(String value) {
        editor.putString("refreshToken", value);
        editor.commit();
    }

    public Business getBusinessVaultSelected() {
        String json = sharedPreferences.getString("business_vault_id", null);
        if (json == null)
            return null;
        Type type = new TypeToken<Business>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }

    public void clearSharedPref() {
        editor.clear();
        editor.commit();
    }

    public String getToken() {
        return sharedPreferences.getString("vault_token", null);
    }

    public String getTransactionCursor() {
        return sharedPreferences.getString("tCursor", null);
    }

    public Boolean isCryptoBalanceUpdated() {
        return sharedPreferences.getBoolean("cryptoBalanceUpdated", false);
    }

    public String getFullName() {
        return sharedPreferences.getString("fullName", null);
    }

    public User getUser() {
        String json = sharedPreferences.getString("user", null);
        if (json == null)
            return null;
        Type type = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(json, type);
    }


}
