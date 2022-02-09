package com.sagisu.vault.network;

import com.google.gson.JsonObject;
import com.sagisu.vault.models.Coins;
import com.sagisu.vault.models.Payments;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.models.ValidateAddressResponse;
import com.sagisu.vault.models.WaitList;
import com.sagisu.vault.ui.OTP.Otp;
import com.sagisu.vault.ui.home.Balances;
import com.sagisu.vault.ui.kyc.KycBean;
import com.sagisu.vault.ui.kyc.KycScanResultBean;
import com.sagisu.vault.ui.login.fragments.LoginResponse;
import com.sagisu.vault.ui.login.fragments.User;
import com.sagisu.vault.ui.trade.receive.ReceiveCryptoResponse;
import com.sagisu.vault.ui.trade.send.SendCryptoResponse;
import com.sagisu.vault.ui.trade.watchlists.ChartMetrics;
import com.sagisu.vault.ui.trade.watchlists.CoinMetrics;
import com.sagisu.vault.ui.trade.watchlists.CoinProfile;
import com.sagisu.vault.ui.transactions.GetTransactionResponse;
import com.sagisu.vault.ui.transfer.BankDetailsResponse;
import com.sagisu.vault.ui.transfer.LinkTokenResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Anusha on 06-02-2018.
 */


public interface VaultApiInterface {

    @POST("auth/login")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> login(@Body User user);

    @POST("auth/signUp")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> signUp(@Body User user);

    @POST("auth/forgotPassword")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> forgotPassword(@Body User user);

    @GET("auth/me")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> getProfile(@Query("phone") String phone);

    @GET("auth")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> getProfile();

    @POST("auth/kyc")
    Call<VaultServerResponse<KycBean>> postKyc(@Body KycBean kycBean);

    @GET("auth/kycScanDetails")
    Call<VaultServerResponse<KycScanResultBean>> getKycScanDetails();

    @FormUrlEncoded
    @POST("auth/kycScanId")
    Call<VaultServerResponse<JsonObject>> postKycScanId(@Field("scanRefernceId") String scanRefernceId);

    @POST("token")
    VaulrErrorHandlingAdapter.MyCall<LinkTokenResponse> getPlaidLinkToken();

    @GET("accounts")
    VaulrErrorHandlingAdapter.MyCall<List<BankDetailsResponse>> getAccounts();

    @POST("wallet/transfer")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> createBankTransfer(@Body Payments bankTransferRequest);

    @POST("wallet/fund")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> fundWallet(@Body Payments payments);

    @FormUrlEncoded
    @POST("wallet/contact_transfer")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> contactTransfer(@Field("contactNumber") String contactNumber,
                                                                                       @Field("contactName") String contactName,
                                                                                       @Field("amount") Integer amount);

    @POST("token/bank_details")
    VaulrErrorHandlingAdapter.MyCall<BankDetailsResponse> bankDetails(@Query("publicToken") String publicToken,
                                                                      @Query("institutionName") String institutionName);

    @GET("accounts/balance")
    VaulrErrorHandlingAdapter.MyCall<Balances> walletBalance();

    @GET("crypto/balance")
    VaulrErrorHandlingAdapter.MyCall<Balances> cryptoWalletBalance();

    @GET("accounts/transactions")
    Single<GetTransactionResponse> getTransactions(@Query("pageNo") Integer pageNo/*,@Query("cursor") String cursor*/);

    @GET("accounts/transactions")
    Call<GetTransactionResponse> getTransactionList(@Query("pageNo") Integer pageNo);


    @GET("otps/validate")
    Call<VaultServerResponse<JsonObject>> validateOtp(@Query(value = "phone", encoded = true) String phone,
                                                      @Query(value = "email", encoded = true) String email,
                                                      @Query(value = "otpNumber", encoded = true) String otpNumber,
                                                      @Query(value = "event", encoded = true) String event,
                                                      @Query(value = "actorType", encoded = true) String actorType);


    @POST("otps/generate")
    Call<VaultServerResponse<JsonObject>> resendOtp(@Body Otp body);

    @POST("otps/generate")
    Call<VaultServerResponse<JsonObject>> generateOtp(@Body Otp body);

    @FormUrlEncoded
    @POST("waitlist")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<WaitList>> joinWaitLists(@Field("featureName") String featureName);

    @GET("accounts/transaction/{transactionId}")
    VaulrErrorHandlingAdapter.MyCall<Transaction> getTransaction(@Path("transactionId") String transactionId);

    @POST("crypto/receive")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ReceiveCryptoResponse>> receiveCrypto(@Query("tokenSymbol") String tokenSymbol);

    @POST("crypto/send")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<SendCryptoResponse>>
    sendCrypto(
            @Query("amount") String amount,
            @Query("currencyCode") String currencyCode,
            @Query("qrData") String qrData,
            @Query("receiverVaultId") String receiverVaultId,
            @Query("assetId") String assetId);

    @GET("crypto/assets")
    Single<VaultServerResponse<List<Coins>>> getCryptoTokens(@Query("page") Integer pageNo/*,@Query("cursor") String cursor*/);

    @GET("crypto/validate_address")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ValidateAddressResponse>> validateDestinationAddress(@Query("assetId") String assetId, @Query("address") String address);

    @GET("assets/{assetId}/profile")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<CoinProfile>> getCoinProfile(@Path("assetId") String assetId);

    @GET("assets/{assetId}/metrics")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<CoinMetrics>> getCoinMetrics(@Path("assetId") String assetId);

    @GET("assets/{assetId}/timeSeriesMetrics")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ChartMetrics>> getChartData(@Path("assetId") String assetId,
                                                                                     @Query("start") String start,
                                                                                     @Query("end") String end,
                                                                                     @Query("interval") String interval);


}