package com.sagisu.vaultLibrary.utils;

import android.app.Activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.sagisu.vaultLibrary.network.VaulrErrorHandlingAdapter;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.network.VaultServerResponse;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.ui.login.fragments.LoginResponse;

import retrofit2.Response;

public class VaultSdk {

    public interface VaultSdkListener {
        void onSuccess();

        void onError(String message);
    }

    static void onSuccess(LoginResponse response, VaultSdkListener listener) {
        new SharedPref().setToken(response.getToken());
        new SharedPref().setUser(response.getUser());
        new SharedPref().setValueToSharedPref(SharedPref.CURRENT_VAULT_ID, response.getUser().getVaultAccountId());
        listener.onSuccess();
    }

    public static void initialize(Activity activity, String apiKey,String apiSecret, VaultSdkListener listener) {
        /*
         * Api call to check if the apiKey is valid
         * Get the token/session details of the user
         */
        AppManager.getAppManager().addActivity(activity);
        VaultApiInterface api = VaultApiClient.buildRetrofitService();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<LoginResponse>> call = api.validateApiKey(apiKey,apiSecret);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<LoginResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<LoginResponse>> response) {
                AppManager.getAppManager().finishActivity();
                if (response.isSuccessful()) {
                    if (response.body().isSuccess())
                        onSuccess(response.body().getData(), listener);
                    else
                        listener.onError(response.body().getMessage());
                } else {
                    listener.onError(response.errorBody().toString());
                }
            }

            @Override
            public void clientError(Response<?> response) {
                AppManager.getAppManager().finishActivity();
                listener.onError(response.errorBody().toString());
            }
        });
    }
}
