package com.sagisu.vaultLibrary.ui.OTP;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkRequest;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.network.VaultServerResponse;
import com.sagisu.vaultLibrary.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class OtpView {

    private IOtp listener;
    private Context context;
    private BottomSheetDialog mBottomSheetDialog;
    private Otp otpObj;
    private VaultApiInterface api;
    private NetworkRequest networkRequest;

    public OtpView(Context context, IOtp listener, Otp otp) {
        this.context = context;
        this.listener = listener;
        this.otpObj = otp;
        api = VaultApiClient.buildRetrofitService();
        //this.networkRequest = NetworkRequest.getInstance();
        //init();
    }

    private void init() {
        generateOtp();
    }

    public void generateOtp() {
        listener.loading("Generating OTP", true);
        Call<VaultServerResponse<JsonObject>> call = api.generateOtp(otpObj);
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    listener.generateOtpSuccess();
                } else {
                    VaultAPIError vaultApiError = new VaultResult.Error(response.errorBody()).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                listener.loading(null, false);
                if (t instanceof HttpException) {
                    VaultAPIError vaultApiError = new VaultResult.Error((HttpException) t).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity)context) ;
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

   /* private void showOtpPopup() {
        OtpViewBinding binding = OtpViewBinding.inflate(LayoutInflater.from(context));
        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setCanceledOnTouchOutside(false);
        mBottomSheetDialog.setContentView(binding.getRoot());
        mBottomSheetDialog.show();

        binding.otpNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 0) {
                    binding.otpWrap.setError(null);
                    binding.otpWrap.setErrorEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.otpConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = binding.otpNumber.getText().toString().trim();
                if (otp == null || otp.isEmpty()) {
                    binding.otpWrap.setError(context.getString(R.string.error_field_empty));
                    binding.otpWrap.setErrorEnabled(true);
                } else
                    verifyOtp(otp);
            }
        });

        binding.resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                binding.otpNumber.setText("");
                resendOtp();
            }
        });


    }*/

    public void resendOtp() {
        listener.loading("Resending OTP", true);
        Call<VaultServerResponse<JsonObject>> call = api.resendOtp(otpObj);
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    Util.showSnackBar( response.message(),(Activity) context);
                    //Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    VaultAPIError vaultApiError = new VaultResult.Error(response.errorBody()).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                listener.loading(null, false);
                if (t instanceof HttpException) {
                    VaultAPIError vaultApiError = new VaultResult.Error((HttpException) t).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyOtp(String otpNumber) {
        listener.loading("Verifying OTP", true);
        Call<VaultServerResponse<JsonObject>> call = api.validateOtp(otpObj.getPhone(),Util.phone_prefix,null, otpNumber, otpObj.getEvent(), otpObj.getActorType());
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    // mBottomSheetDialog.dismiss();
                    listener.verifiedNumber("");
                } else {
                    VaultAPIError vaultApiError = new VaultResult.Error(response.errorBody()).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                listener.loading(null, false);
                t.printStackTrace();
                if (t instanceof HttpException) {
                    VaultAPIError vaultApiError = new VaultResult.Error((HttpException) t).getError();
                    Util.showSnackBar( vaultApiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
