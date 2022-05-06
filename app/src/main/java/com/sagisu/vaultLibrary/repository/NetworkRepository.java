package com.sagisu.vaultLibrary.repository;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.sagisu.vaultLibrary.models.Account;
import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.BusinessRequest;
import com.sagisu.vaultLibrary.models.MyBusinessVault;
import com.sagisu.vaultLibrary.models.Payments;
import com.sagisu.vaultLibrary.models.TOTP;
import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.models.ValidateAddressResponse;
import com.sagisu.vaultLibrary.models.WaitList;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultApiInterface;
import com.sagisu.vaultLibrary.network.VaulrErrorHandlingAdapter;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.network.VaultServerResponse;
import com.sagisu.vaultLibrary.ui.contacts.ContactsInfo;
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
import com.sagisu.vaultLibrary.ui.transfer.BankDetailsResponse;
import com.sagisu.vaultLibrary.ui.transfer.LinkTokenResponse;
import com.sagisu.vaultLibrary.utils.SharedPref;
import com.sagisu.vaultLibrary.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class NetworkRepository {

    private static NetworkRepository customerRepository;
    VaultApiInterface api;


    public static NetworkRepository getInstance() {
        if (customerRepository == null) {
            customerRepository = new NetworkRepository();
        }
        return customerRepository;
    }

    private NetworkRepository() {
        api = VaultApiClient.buildRetrofitService();
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

   /* public MutableLiveData<VaultResult<LoginResponse>> login(User user) {

        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        //VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.login(user);
        VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.login(user.getPhone(), user.getPassword());
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        *//*call.enqueue(new M<LoginResponse>() {
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
        });*//*
        return userRes;
    }*/

    public MutableLiveData<VaultResult<LoginResponse>> login(String phone, String code) {
        phone = Util.phone_prefix + phone;
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        //VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.login(user);
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<LoginResponse>> call = api.login(phone, code);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<LoginResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<LoginResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess())
                        userRes.postValue(new VaultResult.Success<>(response.body().getData(), response.body().getMessage()));
                    else
                        userRes.postValue(new VaultResult.Error(new VaultAPIError(response.body().getMessage(), response.code())));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<LoginResponse>> signUp(User user) {
        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<LoginResponse>> call = api.signUp(user);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<LoginResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<LoginResponse>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess())
                        userRes.postValue(new VaultResult.Success<>(response.body().getData()));
                    else  userRes.postValue(new VaultResult.Error(new VaultAPIError(response.body().getMessage(),response.code())));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<LoginResponse>> forgotPassword(User user) {
        try {
            user = (User) user.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        user.setPhone(Util.phone_prefix + user.getPhone());
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.forgotPassword(user);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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


    public MutableLiveData<VaultResult<LoginResponse>> getProfile(String phone) {
        phone = Util.phone_prefix + phone;
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.getProfile(phone);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<LoginResponse>> getProfile() {
        MutableLiveData<VaultResult<LoginResponse>> userRes = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.getProfile();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<LoginResponse>() {
            @Override
            public void success(Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<String>> createLinkToken() {
        MutableLiveData<VaultResult<String>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<LinkTokenResponse> call = api.getPlaidLinkToken();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<LinkTokenResponse>() {
            @Override
            public void success(Response<LinkTokenResponse> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body().getLink_token()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<List<Account>>> getPlaidAccounts() {
        MutableLiveData<VaultResult<List<Account>>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<List<BankDetailsResponse>> call = api.getAccounts();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<List<BankDetailsResponse>>() {
            @Override
            public void success(Response<List<BankDetailsResponse>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(bankResponseIntercept(response.body())));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<Transaction>> createBankTransfer(Payments bankTransferRequest) {
        MutableLiveData<VaultResult<Transaction>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> call = api.createBankTransfer(bankTransferRequest);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Transaction>>() {
            @Override
            public void success(Response<VaultServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<Transaction>> fundWallet(Payments payments) {
        MutableLiveData<VaultResult<Transaction>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> call = api.fundWallet(payments);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Transaction>>() {
            @Override
            public void success(Response<VaultServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<Transaction>> contactTransfer(ContactsInfo contactsInfo, Integer amount) {
        MutableLiveData<VaultResult<Transaction>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Transaction>> call = api.contactTransfer(contactsInfo.getPhoneNumber(), contactsInfo.getDisplayName(), amount);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Transaction>>() {
            @Override
            public void success(Response<VaultServerResponse<Transaction>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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


    public MutableLiveData<VaultResult<BankDetailsResponse>> bankDetails(String publicToken, String institutionName) {
        MutableLiveData<VaultResult<BankDetailsResponse>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<BankDetailsResponse> call = api.bankDetails(publicToken, institutionName);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<BankDetailsResponse>() {
            @Override
            public void success(Response<BankDetailsResponse> response) {
                if (response.isSuccessful()) {
                    BankDetailsResponse bankDetailsResponse = response.body();
                    String instName = bankDetailsResponse.getName();
                    for (Account account : bankDetailsResponse.getAccounts()) {
                        account.setInstitutionName(instName);
                        account.setName(getMaskedName(account, instName));
                    }
                    resResponse.postValue(new VaultResult.Success<>(bankDetailsResponse));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<Balances>> walletBalance() {
        MutableLiveData<VaultResult<Balances>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<Balances> call = api.walletBalance();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void success(Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    /* @Params : vaultAccountId null for personal account balance, VaultId for business accounts
     * Gives coinsTotal and list of coins with coin data
     */
    public MutableLiveData<VaultResult<Balances>> cryptoWalletBalance(String vaultAccountId) {
        MutableLiveData<VaultResult<Balances>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<Balances> call = vaultAccountId == null ? api.cryptoWalletBalance() : api.cryptoWalletBalance(vaultAccountId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<Balances>() {
            @Override
            public void success(Response<Balances> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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

    public MutableLiveData<VaultResult<WaitList>> joinWaitLists(String featureName) {
        MutableLiveData<VaultResult<WaitList>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<WaitList>> call = api.joinWaitLists(featureName);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<WaitList>>() {
            @Override
            public void success(Response<VaultServerResponse<WaitList>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
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
        VaulrErrorHandlingAdapter.MyCall<Transaction> call = api.getTransaction(txid);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<Transaction>() {
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

    public MutableLiveData<VaultResult<KycBean>> postKyc(KycBean kycBean) {
        MutableLiveData<VaultResult<KycBean>> userRes = new MutableLiveData<>();
        Call<VaultServerResponse<KycBean>> call = api.postKyc(kycBean);
        call.enqueue(new Callback<VaultServerResponse<KycBean>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<KycBean>> call, Response<VaultServerResponse<KycBean>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<KycBean>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new VaultResult.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<VaultResult<JsonObject>> postKycScanId(String scanId) {
        MutableLiveData<VaultResult<JsonObject>> userRes = new MutableLiveData<>();
        Call<VaultServerResponse<JsonObject>> call = api.postKycScanId(scanId);
        call.enqueue(new Callback<VaultServerResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<JsonObject>> call, Response<VaultServerResponse<JsonObject>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<JsonObject>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new VaultResult.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<VaultResult<KycScanResultBean>> getKycScanDetails() {
        MutableLiveData<VaultResult<KycScanResultBean>> userRes = new MutableLiveData<>();
        Call<VaultServerResponse<KycScanResultBean>> call = api.getKycScanDetails();
        call.enqueue(new Callback<VaultServerResponse<KycScanResultBean>>() {
            @Override
            public void onResponse(Call<VaultServerResponse<KycScanResultBean>> call, Response<VaultServerResponse<KycScanResultBean>> response) {
                if (response.isSuccessful()) {
                    userRes.postValue(new VaultResult.Success<>(response.body().getData()));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void onFailure(Call<VaultServerResponse<KycScanResultBean>> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof HttpException)
                    userRes.postValue(new VaultResult.Error((HttpException) t));
            }
        });
        return userRes;
    }

    public MutableLiveData<ReceiveCryptoResponse> receiveCrypto(String tokenSymbol) {
        MutableLiveData<ReceiveCryptoResponse> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ReceiveCryptoResponse>> call = api.receiveCrypto(new SharedPref().getValue(SharedPref.CURRENT_VAULT_ID), tokenSymbol);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<ReceiveCryptoResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<ReceiveCryptoResponse>> response) {
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
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<SendCryptoResponse>> call = api.sendCrypto(new SharedPref().getValue(SharedPref.CURRENT_VAULT_ID), amount, currencyCode, qrData, vaultId, assetId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<SendCryptoResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<SendCryptoResponse>> response) {
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
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ValidateAddressResponse>> call = api.validateDestinationAddress(assetId, address);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<ValidateAddressResponse>>() {
            @Override
            public void success(Response<VaultServerResponse<ValidateAddressResponse>> response) {
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
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<CoinProfile>> call = api.getCoinProfile(assetId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<CoinProfile>>() {
            @Override
            public void success(Response<VaultServerResponse<CoinProfile>> response) {
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
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<CoinMetrics>> call = api.getCoinMetrics(assetId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<CoinMetrics>>() {
            @Override
            public void success(Response<VaultServerResponse<CoinMetrics>> response) {
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

    public MutableLiveData<ChartMetrics> fetchChartData(String assetId, String start, String end, String interval) {
        MutableLiveData<ChartMetrics> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<ChartMetrics>> call = api.getChartData(assetId, start, end, interval);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<ChartMetrics>>() {
            @Override
            public void success(Response<VaultServerResponse<ChartMetrics>> response) {
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

    public MutableLiveData<VaultResult<Business>> postBusiness(Business business, boolean markDefault) {
        MutableLiveData<VaultResult<Business>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> call = api.postBusiness(business, markDefault);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Business>>() {
            @Override
            public void success(Response<VaultServerResponse<Business>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<Business>(response.body().getData(), response.body().getMessage()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }


    public MutableLiveData<VaultResult<Business>> joinBusiness(String businessId, boolean markDefault) {
        MutableLiveData<VaultResult<Business>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> call = api.joinBusiness(businessId, markDefault);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Business>>() {
            @Override
            public void success(Response<VaultServerResponse<Business>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(new VaultResult.Success<Business>(response.body().getData(), response.body().getMessage()));
                } else {
                    resResponse.postValue(new VaultResult.Error(response.errorBody()));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }

    public MutableLiveData<List<MyBusinessVault>> getMyBusiness() {
        MutableLiveData<List<MyBusinessVault>> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<List<MyBusinessVault>>> call = api.getMyBusiness();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<List<MyBusinessVault>>>() {
            @Override
            public void success(Response<VaultServerResponse<List<MyBusinessVault>>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body().getData());
                } else {
                    //resResponse.postValue();
                }
            }

            @Override
            public void clientError(Response<?> response) {
                //resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }

    public MutableLiveData<Business> getMyDefaultBusiness() {
        MutableLiveData<Business> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<Business>> call = api.getMyDefaultBusiness();
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<Business>>() {
            @Override
            public void success(Response<VaultServerResponse<Business>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body().getData());
                } else {
                    resResponse.postValue(null);
                }
            }

            @Override
            public void clientError(Response<?> response) {
                resResponse.postValue(null);
            }
        });
        return resResponse;
    }


    public MutableLiveData<BusinessRequest> getBusinessRequest(String requestId) {
        MutableLiveData<BusinessRequest> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<BusinessRequest>> call = api.getBusinessRequests(requestId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<BusinessRequest>>() {
            @Override
            public void success(Response<VaultServerResponse<BusinessRequest>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body().getData());
                } else {
                    //resResponse.postValue();
                }
            }

            @Override
            public void clientError(Response<?> response) {
                //resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }

    public MutableLiveData<String> approveBusinessRequest(String requestId) {
        MutableLiveData<String> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<String>> call = api.approveBusinessRequests(requestId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<String>>() {
            @Override
            public void success(Response<VaultServerResponse<String>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body().getData());
                } else {
                    //resResponse.postValue();
                }
            }

            @Override
            public void clientError(Response<?> response) {
                //resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }

    public MutableLiveData<String> rejectBusinessRequest(String requestId) {
        MutableLiveData<String> resResponse = new MutableLiveData<>();
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<String>> call = api.rejectBusinessRequests(requestId);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<String>>() {
            @Override
            public void success(Response<VaultServerResponse<String>> response) {
                if (response.isSuccessful()) {
                    resResponse.postValue(response.body().getData());
                } else {
                    //resResponse.postValue();
                }
            }

            @Override
            public void clientError(Response<?> response) {
                //resResponse.postValue(new VaultResult.Error(response.errorBody()));
            }
        });
        return resResponse;
    }


    public MutableLiveData<VaultResult<TOTP>> recoverTotp(String phone, String code) {
        phone = Util.phone_prefix + phone;
        MutableLiveData<VaultResult<TOTP>> userRes = new MutableLiveData<>();
        //VaulrErrorHandlingAdapter.MyCall<LoginResponse> call = api.login(user);
        VaulrErrorHandlingAdapter.MyCall<VaultServerResponse<TOTP>> call = api.recoverTotp(phone, code);
        call.enqueue(new VaulrErrorHandlingAdapter.MyCallback<VaultServerResponse<TOTP>>() {
            @Override
            public void success(Response<VaultServerResponse<TOTP>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess())
                        userRes.postValue(new VaultResult.Success<>(response.body().getData(), response.body().getMessage()));
                    else
                        userRes.postValue(new VaultResult.Error(new VaultAPIError(response.body().getMessage(), response.code())));
                } else {
                    userRes.postValue(new VaultResult.Error(response.errorBody()));
                    // userRes.postValue(new Result.Error(new APIError(response.errorBody().toString(), response.code())));
                }
            }

            @Override
            public void clientError(Response<?> response) {
                userRes.postValue(new VaultResult.Error(response.errorBody()));
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
}
