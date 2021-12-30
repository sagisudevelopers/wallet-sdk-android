package com.sagisu.vault.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.sagisu.vault.models.Account;
import com.sagisu.vault.models.Payments;
import com.sagisu.vault.models.Transaction;
import com.sagisu.vault.models.ValidateAddressResponse;
import com.sagisu.vault.models.WaitList;
import com.sagisu.vault.network.ApiClient;
import com.sagisu.vault.network.ApiInterface;
import com.sagisu.vault.network.ErrorHandlingAdapter;
import com.sagisu.vault.network.Result;
import com.sagisu.vault.network.ServerResponse;
import com.sagisu.vault.ui.contacts.ContactsInfo;
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
import com.sagisu.vault.ui.transfer.BankDetailsResponse;
import com.sagisu.vault.ui.transfer.LinkTokenResponse;
import com.sagisu.vault.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class NetworkRepository {

    private static NetworkRepository customerRepository;
    ApiInterface api;


    public static NetworkRepository getInstance() {
        if (customerRepository == null) {
            customerRepository = new NetworkRepository();
        }
        return customerRepository;
    }

    private NetworkRepository() {
        api = ApiClient.buildRetrofitService();
    }

    private String getResponseError(int code) {
        String msg;
        switch (code) {
            case 401:
                msg = "Unauthorized user";
                break;
            case 413:
                msg = "Entity Too Large";
                break;
            case 502:
                msg = "Bad Gateway";
                break;
            case 504:
                msg = "Gateway Timeout";
                break;
            default:
                msg = "Network problem";
                break;
        }

        return msg;
    }

    public MutableLiveData<Result<LoginResponse>> login(User user) {

        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<Result<LoginResponse>> userRes = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LoginResponse> call = api.login(user);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new M<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                   // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return userRes;
    }

    public MutableLiveData<Result<LoginResponse>> signUp(User user) {
        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<Result<LoginResponse>> userRes = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LoginResponse> call = api.signUp(user);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return userRes;
    }

    public MutableLiveData<Result<LoginResponse>> forgotPassword(User user) {
        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<Result<LoginResponse>> userRes = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LoginResponse> call = api.forgotPassword(user);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return userRes;
    }


    public MutableLiveData<Result<LoginResponse>> getProfile(String phone) {
        phone = Util.phone_prefix + phone;
        MutableLiveData<Result<LoginResponse>> userRes = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LoginResponse> call = api.getProfile(phone);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return userRes;
    }

    public MutableLiveData<Result<LoginResponse>> getProfile() {
        MutableLiveData<Result<LoginResponse>> userRes = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LoginResponse> call = api.getProfile();
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return userRes;
    }

    public MutableLiveData<Result<String>> createLinkToken() {
        MutableLiveData<Result<String>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<LinkTokenResponse> call = api.getPlaidLinkToken();
        call.enqueue(new ErrorHandlingAdapter.MyCallback<LinkTokenResponse>() {
            @Override
            public void success(Response<LinkTokenResponse> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getLink_token()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
       /* call.enqueue(new Callback<LinkTokenResponse>() {
            @Override
            public void onResponse(Call<LinkTokenResponse> call, Response<LinkTokenResponse> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getLink_token()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LinkTokenResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<List<Account>>> getPlaidAccounts() {
        MutableLiveData<Result<List<Account>>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<List<BankDetailsResponse>> call = api.getAccounts();
        call.enqueue(new ErrorHandlingAdapter.MyCallback<List<BankDetailsResponse>>() {
            @Override
            public void success(Response<List<BankDetailsResponse>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(bankResponseIntercept(response.body())));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<List<BankDetailsResponse>>() {
            @Override
            public void onResponse(Call<List<BankDetailsResponse>> call, Response<List<BankDetailsResponse>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(bankResponseIntercept(response.body())));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<List<BankDetailsResponse>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    private List<Account> bankResponseIntercept(List<BankDetailsResponse> bankDetailsResponses) {
        List<Account> accountList = new ArrayList<>();
        for (BankDetailsResponse bankDetailsResponse : bankDetailsResponses) {
            String instName = bankDetailsResponse.getName();
            if (bankDetailsResponse.getAccounts() == null) break;
            for (Account account : bankDetailsResponse.getAccounts()) {
                account.setInstitutionName(instName);
                account.setName(getMaskedName(account, instName));
                accountList.add(account);
            }

        }
        return accountList;
    }

    private String getMaskedName(Account account, String instName) {
        return instName.concat(" - ").concat(account.getAccount().substring(account.getAccount().length() - 4));
    }

    public MutableLiveData<Result<Transaction>> createBankTransfer(Payments bankTransferRequest) {
        MutableLiveData<Result<Transaction>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<Transaction>> call = api.createBankTransfer(bankTransferRequest);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<Transaction>>() {
            @Override
            public void success(Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
       /* call.enqueue(new Callback<ServerResponse<Transaction>>() {
            @Override
            public void onResponse(Call<ServerResponse<Transaction>> call, Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<Transaction>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<Transaction>> fundWallet(Payments payments) {
        MutableLiveData<Result<Transaction>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<Transaction>> call = api.fundWallet(payments);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<Transaction>>() {
            @Override
            public void success(Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<ServerResponse<Transaction>>() {
            @Override
            public void onResponse(Call<ServerResponse<Transaction>> call, Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<Transaction>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<Transaction>> contactTransfer(ContactsInfo contactsInfo, Integer amount) {
        MutableLiveData<Result<Transaction>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<Transaction>> call = api.contactTransfer(contactsInfo.getPhoneNumber(), contactsInfo.getDisplayName(), amount);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<Transaction>>() {
            @Override
            public void success(Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<ServerResponse<Transaction>>() {
            @Override
            public void onResponse(Call<ServerResponse<Transaction>> call, Response<ServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<Transaction>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }


    public MutableLiveData<Result<BankDetailsResponse>> bankDetails(String publicToken, String institutionName) {
        MutableLiveData<Result<BankDetailsResponse>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<BankDetailsResponse> call = api.bankDetails(publicToken, institutionName);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<BankDetailsResponse>() {
            @Override
            public void success(Response<BankDetailsResponse> response) {
                if (response.isSuccessful()) {
                    BankDetailsResponse bankDetailsResponse = response.body();
                    String instName = bankDetailsResponse.getName();
                    for (Account account : bankDetailsResponse.getAccounts()) {
                        account.setInstitutionName(instName);
                        account.setName(getMaskedName(account, instName));
                    }
                    resResponse.postValue(new Result.Success<>(bankDetailsResponse));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new Callback<BankDetailsResponse>() {
            @Override
            public void onResponse(Call<BankDetailsResponse> call, Response<BankDetailsResponse> response) {
                if (response.isSuccessful()) {
                    BankDetailsResponse bankDetailsResponse = response.body();
                    String instName = bankDetailsResponse.getName();
                    for (Account account : bankDetailsResponse.getAccounts()) {
                        account.setInstitutionName(instName);
                        account.setName(getMaskedName(account, instName));
                    }
                    resResponse.postValue(new Result.Success<>(bankDetailsResponse));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<BankDetailsResponse> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<Balances>> walletBalance() {
        MutableLiveData<Result<Balances>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<Balances> call = api.walletBalance();
        call.enqueue(new ErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void success(Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new ErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void onResponse(Call<Balances> call, Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Balances> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<Balances>> cryptoWalletBalance() {
        MutableLiveData<Result<Balances>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<Balances> call = api.cryptoWalletBalance();
        call.enqueue(new ErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void success(Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
        /*call.enqueue(new ErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void onResponse(Call<Balances> call, Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Balances> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<WaitList>> joinWaitLists(String featureName) {
        MutableLiveData<Result<WaitList>> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<WaitList>> call = api.joinWaitLists(featureName);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<WaitList>>() {
            @Override
            public void success(Response<ServerResponse<WaitList>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new Result.Error(response.errorBody()));
            }
        });
       /* call.enqueue(new Callback<ServerResponse<WaitList>>() {
            @Override
            public void onResponse(Call<ServerResponse<WaitList>> call, Response<ServerResponse<WaitList>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<WaitList>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Transaction> getTransaction(String txid) {
        MutableLiveData<Transaction> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<Transaction> call = api.getTransaction(txid);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<Transaction>() {
            @Override
            public void success(Response<Transaction> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
       /* call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(Call<Transaction> call, Response<Transaction> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body());
                } else {
                   // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Transaction> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException){

                }
                   // resResponse.postValue(new Result.Error((HttpException) t));
            }
        });*/
        return resResponse;
    }

    public MutableLiveData<Result<KycBean>> postKyc(KycBean kycBean) {
        MutableLiveData<Result<KycBean>> userRes = new MutableLiveData<>();
        Call<ServerResponse<KycBean>> call = api.postKyc(kycBean);
        call.enqueue(new Callback<ServerResponse<KycBean>>() {
            @Override
            public void onResponse(Call<ServerResponse<KycBean>> call, Response<ServerResponse<KycBean>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<KycBean>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<Result<JsonObject>> postKycScanId(String scanId) {
        MutableLiveData<Result<JsonObject>> userRes = new MutableLiveData<>();
        Call<ServerResponse<JsonObject>> call = api.postKycScanId(scanId);
        call.enqueue(new Callback<ServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ServerResponse<JsonObject>> call, Response<ServerResponse<JsonObject>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<Result<KycScanResultBean>> getKycScanDetails() {
        MutableLiveData<Result<KycScanResultBean>> userRes = new MutableLiveData<>();
        Call<ServerResponse<KycScanResultBean>> call = api.getKycScanDetails();
        call.enqueue(new Callback<ServerResponse<KycScanResultBean>>() {
            @Override
            public void onResponse(Call<ServerResponse<KycScanResultBean>> call, Response<ServerResponse<KycScanResultBean>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new Result.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<ServerResponse<KycScanResultBean>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new Result.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<ReceiveCryptoResponse> receiveCrypto(String tokenSymbol) {
        MutableLiveData<ReceiveCryptoResponse> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<ReceiveCryptoResponse>> call = api.receiveCrypto(tokenSymbol);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<ReceiveCryptoResponse>>() {
            @Override
            public void success(Response<ServerResponse<ReceiveCryptoResponse>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

    public MutableLiveData<SendCryptoResponse> sendCrypto(String amount, String currencyCode, String qrData, String vaultId, String assetId) {
        MutableLiveData<SendCryptoResponse> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<SendCryptoResponse>> call = api.sendCrypto(amount, currencyCode, qrData, vaultId, assetId);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<SendCryptoResponse>>() {
            @Override
            public void success(Response<ServerResponse<SendCryptoResponse>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

    public MutableLiveData<ValidateAddressResponse> validateDestinationAddress(String assetId, String address) {
        MutableLiveData<ValidateAddressResponse> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<ValidateAddressResponse>> call = api.validateDestinationAddress(assetId, address);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<ValidateAddressResponse>>() {
            @Override
            public void success(Response<ServerResponse<ValidateAddressResponse>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

    public MutableLiveData<CoinProfile> getCoinProfile(String assetId) {
        MutableLiveData<CoinProfile> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<CoinProfile>> call = api.getCoinProfile(assetId);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<CoinProfile>>() {
            @Override
            public void success(Response<ServerResponse<CoinProfile>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

    public MutableLiveData<CoinMetrics> getCoinMetrics(String assetId) {
        MutableLiveData<CoinMetrics> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<CoinMetrics>> call = api.getCoinMetrics(assetId);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<CoinMetrics>>() {
            @Override
            public void success(Response<ServerResponse<CoinMetrics>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

    public MutableLiveData<ChartMetrics> fetchChartData(String assetId,String start, String end, String interval) {
        MutableLiveData<ChartMetrics> resResponse = new MutableLiveData<>();
        ErrorHandlingAdapter.MyCall<ServerResponse<ChartMetrics>> call = api.getChartData(assetId,start,end,interval);
        call.enqueue(new ErrorHandlingAdapter.MyCallback<ServerResponse<ChartMetrics>>() {
            @Override
            public void success(Response<ServerResponse<ChartMetrics>> response) {
                if (response.isSuccessful()) {

                    resResponse.postValue(response.body().getData());
                } else {
                    // resResponse.postValue(new Result.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {

            }
        });
        return resResponse;
    }

}
