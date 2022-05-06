package com.sagisu.vaultLibrary.network;

import com.google.gson.JsonObject;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.models.PaginationResponse;
import com.sagisu.vaultLibrary.models.Payments;
import com.sagisu.vaultLibrary.models.TOTP;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.models.ValidateAddressResponse;
import com.sagisu.vaultLibrary.models.WaitList;
import com.sagisu.vaultLibrary.ui.OTP.Otp;
import com.sagisu.vaultLibrary.ui.home.Balances;
import com.sagisu.vaultLibrary.ui.kyc.KycBean;
import com.sagisu.vaultLibrary.ui.kyc.KycScanResultBean;
import com.sagisu.vaultLibrary.ui.login.fragments.LoginResponse;
import com.sagisu.vaultLibrary.ui.login.fragments.User;
import com.sagisu.vaultLibrary.ui.trade.receive.ReceiveCryptoResponse;
import com.sagisu.vaultLibrary.ui.trade.send.SendCryptoResponse;
import com.sagisu.vaultLibrary.ui.trade.watchlists.ChartMetrics;
import com.sagisu.vaultLibrary.ui.trade.watchlists.CoinMetrics;
import com.sagisu.vaultLibrary.ui.trade.watchlists.CoinProfile;
import com.sagisu.vaultLibrary.ui.transactions.GetTransactionResponse;
import com.sagisu.vaultLibrary.ui.transfer.BankDetailsResponse;
import com.sagisu.vaultLibrary.ui.transfer.LinkTokenResponse;

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

    /*@POST("auth/login")
    VaulrErrorHandlingAdapter.MyCall<LoginResponse> login(@Query("phone") String phone,
                                                          @Query("password") String password);*/

    @POST("auth/login")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<LoginResponse>> login(@Query("phone") String phone,
                                                          @Query("code") String code);
    @POST("auth/totp/recover")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<TOTP>> recoverTotp(@Query("phone") String phone,
                                                                            @Query("recoveryCode") String code);

    @POST("auth/signUp")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<LoginResponse>> signUp(@Body User user);

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

    @GET("crypto/balance/{vaultAccountId}")
    VaulrErrorHandlingAdapter.MyCall<Balances> cryptoWalletBalance(@Path("vaultAccountId") String vaultAccountId);

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

    @FormUrlEncoded
    @POST("crypto/{vaultAccountId}/receive")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ReceiveCryptoResponse>> receiveCrypto(
            @Path("vaultAccountId") String vaultAccountId,
            @Field("tokenSymbol") String tokenSymbol);

    @POST("crypto/{vaultAccountId}/send")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<SendCryptoResponse>>
    sendCrypto(
            @Path("vaultAccountId") String vaultAccountId,
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


    @POST("business")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> postBusiness(@Body Business business,@Query("markDefault") boolean markDefault );

    @GET("business")
    Single<PaginationResponse<Business>> getBusiness(@Query("pageNo") Integer pageNo,@Query("query") String query);

    @POST("business/{businessId}/join")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> joinBusiness(@Path("businessId") String businessId,@Query("markDefault") boolean markDefault );

    @GET("business/me")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<List<MyBusinessVault>>> getMyBusiness();

    @GET("business/me/default")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> getMyDefaultBusiness();

    @GET("business/requests")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<BusinessRequest>> getBusinessRequests(@Query("requestId") String requestId);

    @POST("business/requests/{requestId}/approve")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<String>> approveBusinessRequests(@Path("requestId") String requestId);

    @POST("business/requests/{requestId}/reject")
    VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<String>> rejectBusinessRequests(@Path("requestId") String requestId);

    @GET("business/{businessId}/requests")
    Single<PaginationResponse<BusinessRequest>> getAllBusinessRequests(@Path("businessId") String businessId,
                                                                @Query("pageNo") Integer pageNo,
                                                                @Query("query") String query);


}