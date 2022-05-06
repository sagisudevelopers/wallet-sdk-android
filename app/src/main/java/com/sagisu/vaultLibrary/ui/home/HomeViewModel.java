package com.sagisu.vaultLibrary.ui.home;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.ui.login.fragments.LoginResponse;
import com.sagisu.vaultLibrary.ui.login.fragments.User;
import com.sagisu.vaultLibrary.utils.TransferTypeDescriptor;

public class HomeViewModel extends ViewModel {
    private MediatorLiveData<Double> totalWalletBalance = new MediatorLiveData<>();
    private MutableLiveData<String> transferType = new MutableLiveData<>();
    private MutableLiveData<String> assetToBuy = new MutableLiveData<>();
    private MutableLiveData<String> assetToReceive = new MutableLiveData<>();
    private MutableLiveData<String> joinWaitList = new MutableLiveData<>();
    private MutableLiveData<String> toastMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> shimmerBalanceView = new MutableLiveData<>(true);
    private MediatorLiveData<User> userData = new MediatorLiveData<>();
    private MutableLiveData<Uri> deepLinkData = new MutableLiveData<>();

    public void init() {
        getCryptoWalletBalance();//TODO : Change it to wallet balance once we turn on fiat
        //getProfile();
    }

    public void getWalletBalance() {
        shimmerBalanceView.setValue(true);
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().walletBalance();
        totalWalletBalance.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                shimmerBalanceView.setValue(false);
                if (balancesResult instanceof VaultResult.Success) {
                    totalWalletBalance.setValue(((VaultResult.Success<Balances>) balancesResult).getData().getAvailable());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void getCryptoWalletBalance() {
        shimmerBalanceView.setValue(true);
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance(null);
        totalWalletBalance.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                shimmerBalanceView.setValue(false);
                if (balancesResult instanceof VaultResult.Success) {
                    totalWalletBalance.setValue(((VaultResult.Success<Balances>) balancesResult).getData().getCoinsTotal());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    private void getProfile() {
        LiveData<VaultResult<LoginResponse>> liveData = NetworkRepository.getInstance().getProfile();
        userData.addSource(liveData, new Observer<VaultResult<LoginResponse>>() {
            @Override
            public void onChanged(VaultResult<LoginResponse> agentResult) {

                if (agentResult instanceof VaultResult.Success) {
                    User user = ((VaultResult.Success<LoginResponse>) agentResult).getData().getUser();
                    userData.setValue(user);

                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void setTransferType(@TransferTypeDescriptor.TransferTypes String viewType) {
        this.transferType.setValue(viewType);
    }

    public MutableLiveData<String> getTransferType() {
        return transferType;
    }

    public void setAssetToBuy(String assetToBuy) {
        this.assetToBuy.setValue(assetToBuy);
    }

    public MutableLiveData<String> getAssetToBuy() {
        return assetToBuy;
    }

    public MutableLiveData<String> getAssetToReceive() {
        return assetToReceive;
    }

    public void setAssetToReceive(String assetToReceive) {
        this.assetToReceive.setValue(assetToReceive);
    }

    public MediatorLiveData<Double> getTotalWalletBalance() {
        return totalWalletBalance;
    }

    public MutableLiveData<String> getJoinWaitList() {
        return joinWaitList;
    }

    public void setJoinWaitList(String joinWaitList) {
        this.joinWaitList.setValue(joinWaitList);
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg.setValue(toastMsg);
    }

    public MutableLiveData<Boolean> getShimmerBalanceView() {
        return shimmerBalanceView;
    }

    public MediatorLiveData<User> getUserData() {
        return userData;
    }

    public void setTotalWalletBalance(Double totalWalletBalance) {
        shimmerBalanceView.setValue(false);
        this.totalWalletBalance.setValue(totalWalletBalance);
    }

    public void setDeepLinkData(Uri deepLinkData) {
        this.deepLinkData.setValue(deepLinkData);
    }

    public MutableLiveData<Uri> getDeepLinkData() {
        return deepLinkData;
    }
}