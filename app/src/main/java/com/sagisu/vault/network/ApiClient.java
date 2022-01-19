package com.sagisu.vault.network;

/**
 * Created by Anusha on 06-02-2018.
 */

import com.loopj.android.http.BuildConfig;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String LOCAL_NGROK = "http://3d14-2401-4900-32aa-c5c1-6c8d-9b2e-2e98-3122.ngrok.io";
    private static final String LOCAL_DEV_URL = "http://44.198.41.195";
    private static final String PROD_URL = "https://walletprod.sagisu.com";

    public static final String PUBNUB_SUB_KEY = "sub-c-3e3b0ce2-0c52-11ec-9c1c-9adb7f1f2877";
    public static final String PUBNUB_PUB_KEY = "pub-c-f5d299e8-ecd4-4a4a-aed8-7080abd60f2f";

    public static final String STORYTELLER_API_KEY = "adcabf5b-4ddc-4e43-a9b4-056f9f75d73c";

    public static final String JUMIO_KEY_API_TOKEN = "8262ded1-493f-4ee8-b6a0-7561cb6278b8";
    public static final String JUMIO_KEY_API_SECRET = "LhRDD7LR7dBdYpcAukxXiVWFFbb5otRs";

    public static String URL;
    private static Retrofit retrofit;

    public static ApiInterface buildRetrofitService() {
        URL = PROD_URL + "/v1/";
        return buildService();
    }

    public static ApiInterface buildService() {
        OkHttpClient.Builder okHttpClient;
       if (URL.contains(LOCAL_DEV_URL) || URL.contains(LOCAL_NGROK)) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new ConnectivityInterceptor())
                    //.authenticator(new TokenAuthenticator())
                    .connectTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS);   // To connect to local server use this

        } else {
            ConnectionSpec spec = new
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                    .build();
            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(1);
            dispatcher.setMaxRequestsPerHost(1);
            okHttpClient = new OkHttpClient.Builder()
                    .connectionSpecs(Collections.singletonList(spec))
                    .dispatcher(dispatcher)
                    .addInterceptor(new ConnectivityInterceptor())
                    //.authenticator(new TokenAuthenticator())
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .readTimeout(120, TimeUnit.SECONDS); //For secure server use this
        }


        if (BuildConfig.DEBUG) {
            //Set logging of server response and request
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient.addInterceptor(logging);
        }
        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addCallAdapterFactory(new ErrorHandlingAdapter.ErrorHandlingCallAdapterFactory())
                .client(okHttpClient.build())
                .build();

        return retrofit.create(ApiInterface.class);
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
