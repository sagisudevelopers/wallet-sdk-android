package com.sagisu.vault.ui.OTP;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkRequest;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.sagisu.vault.network.APIError;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.network.ApiInterface;
import com.sagisu.vault.network.Result;
import com.sagisu.vault.network.ServerResponse;
import com.sagisu.vault.utils.Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class OtpView {

    private IOtp listener;
    private Context context;
    private BottomSheetDialog mBottomSheetDialog;
    private Otp otpObj;
    private ApiInterface api;
    private NetworkRequest networkRequest;

    public OtpView(Context context, IOtp listener, Otp otp) {
        this.context = context;
        this.listener = listener;
        this.otpObj = otp;
        api = ApiClient.buildRetrofitService();
        //this.networkRequest = NetworkRequest.getInstance();
        //init();
    }

    private void init() {
        generateOtp();
    }

    public void generateOtp() {
        listener.loading("Generating OTP", true);
        otpObj.setPhone(Util.phone_prefix+otpObj.getPhone());
        Call<ServerResponse<JsonObject>> call = api.generateOtp(otpObj);
        call.enqueue(new Callback<ServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ServerResponse<JsonObject>> call, Response<ServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    listener.generateOtpSuccess();
                } else {
                    APIError apiError = new Result.Error(response.errorBody()).getError();
                    Util.showSnackBar( apiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                listener.loading(null, false);
                if (t instanceof HttpException) {
                    APIError apiError = new Result.Error((HttpException) t).getError();
                    Util.showSnackBar( apiError.message(),(Activity)context) ;
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
        Call<ServerResponse<JsonObject>> call = api.resendOtp(otpObj);
        call.enqueue(new Callback<ServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ServerResponse<JsonObject>> call, Response<ServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    Util.showSnackBar( response.message(),(Activity) context);
                    //Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                } else {
                    APIError apiError = new Result.Error(response.errorBody()).getError();
                    Util.showSnackBar( apiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<JsonObject>> call, Throwable t) {
                listener.loading(null, false);
                if (t instanceof HttpException) {
                    APIError apiError = new Result.Error((HttpException) t).getError();
                    Util.showSnackBar( apiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verifyOtp(String otpNumber) {
        listener.loading("Verifying OTP", true);
        Call<ServerResponse<JsonObject>> call = api.validateOtp(otpObj.getPhone(),null, otpNumber, otpObj.getEvent(), otpObj.getActorType());
        call.enqueue(new Callback<ServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ServerResponse<JsonObject>> call, Response<ServerResponse<JsonObject>> response) {
                listener.loading(null, false);
                if (response.isSuccessful()) {
                    // showOtpPopup();
                    // mBottomSheetDialog.dismiss();
                    listener.verifiedNumber("");
                } else {
                    APIError apiError = new Result.Error(response.errorBody()).getError();
                    Util.showSnackBar( apiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<JsonObject>> call, Throwable t) {
                listener.loading(null, false);
                t.printStackTrace();
                if (t instanceof HttpException) {
                    APIError apiError = new Result.Error((HttpException) t).getError();
                    Util.showSnackBar( apiError.message(),(Activity) context);
                    //Toast.makeText(context, apiError.message(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
