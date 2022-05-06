package com.sagisu.vaultLibrary.ui.home;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.sagisu.vaultLibrary.models.Business;
import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.models.Profile;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultApiClient;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.repository.CryptoTokensPagingSource;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.ui.login.fragments.LoginResponse;
import com.sagisu.vaultLibrary.ui.login.fragments.User;
import com.sagisu.vaultLibrary.utils.SharedPref;
import com.sagisu.vaultLibrary.utils.Util;

import io.reactivex.rxjava3.core.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class TradeHomeViewModel extends ViewModel {
    private MediatorLiveData<Balances> balances = new MediatorLiveData<>();
    private MutableLiveData<Util.TradeTypes> tradeTypes = new MutableLiveData<>();
    private MutableLiveData<String> assetToBuy = new MutableLiveData<>();
    private MutableLiveData<String> assetToReceive = new MutableLiveData<>();
    private MutableLiveData<String> joinWaitList = new MutableLiveData<>();
    private MutableLiveData<String> toastMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> shimmerBalanceView = new MutableLiveData<>(true);
    private MediatorLiveData<User> userData = new MediatorLiveData<>();
    private Flowable<PagingData<Coins>> flowable;
    private Business businessSelected;
    private ProfileEnum switchProfile = null;
    private MutableLiveData<Profile> currentProfile = new MutableLiveData<>(new Profile());
    private ObservableField<Boolean> hasBusinessProfile = new ObservableField<>(true);

    public enum ProfileEnum {
        BUSINESS,
        PERSONAL
    }

    public void init() {
        getCryptoTokens("");
        //getProfile();
        tradeTypes = new MutableLiveData<>();
        toastMsg = new MutableLiveData<>();
        joinWaitList = new MutableLiveData<>();
        checkForProfile();
    }

    public void checkForProfile() {
        /* Checks for current profile the user is in
         * Sets the username and vault id to specific data
         */
        businessSelected = new SharedPref().getBusinessVaultSelected();
        if (businessSelected == null) {
            //switchProfile = ProfileEnum.PERSONAL;
            User user = new SharedPref().getUser();
            if (user == null) {
                getProfile();
            } else {
                userData.setValue(user);
            }

        } else {
            setProfile(businessSelected.getName(), businessSelected.getVaultAccountId(), ProfileEnum.BUSINESS);
        }
    }

    public void setProfile(String userName, String vaultId, ProfileEnum profile) {
        Profile profileData = new Profile(userName, vaultId, profile);
        currentProfile.setValue(profileData);
    }

    public void checkForUserNameChange() {
        businessSelected = new SharedPref().getBusinessVaultSelected();
        if (businessSelected != null) {
            setProfile(businessSelected.getName(), businessSelected.getVaultAccountId(), ProfileEnum.BUSINESS);
        }
    }

    public void getCryptoWalletBalance() {
        shimmerBalanceView.setValue(true);
        LiveData<VaultResult<Balances>> balanceLiveData = NetworkRepository.getInstance().cryptoWalletBalance(businessSelected == null ? null : businessSelected.getVaultAccountId());
        balances.addSource(balanceLiveData, new Observer<VaultResult<Balances>>() {
            @Override
            public void onChanged(VaultResult<Balances> balancesResult) {
                shimmerBalanceView.setValue(false);
                if (balancesResult instanceof VaultResult.Success) {
                    new SharedPref().setCryptoBalanceUpdated(true);
                    balances.setValue(((VaultResult.Success<Balances>) balancesResult).getData());
                } else if (balancesResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) balancesResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void getCryptoTokens(String query) {
        // CoroutineScope helper provided by the lifecycle-viewmodel-ktx artifact.
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        Pager<Integer, Coins> pager = new Pager<>(
                new PagingConfig( /*pageSize =  */5),
                () -> new CryptoTokensPagingSource(VaultApiClient.buildRetrofitService(), query));

        flowable = PagingRx.getFlowable(pager);
        PagingRx.cachedIn(flowable, viewModelScope);
    }

    private void getProfile() {
        LiveData<VaultResult<LoginResponse>> liveData = NetworkRepository.getInstance().getProfile();
        userData.addSource(liveData, new Observer<VaultResult<LoginResponse>>() {
            @Override
            public void onChanged(VaultResult<LoginResponse> agentResult) {

                if (agentResult instanceof VaultResult.Success) {
                    User user = ((VaultResult.Success<LoginResponse>) agentResult).getData().getUser();
                    new SharedPref().setUser(user);
                    userData.setValue(user);

                } else if (agentResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) agentResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public void switchProfileButtonSubmit() {
        switchProfile = currentProfile.getValue().getProfile().equals(ProfileEnum.PERSONAL) ? ProfileEnum.BUSINESS : ProfileEnum.PERSONAL;
        if (switchProfile.equals(ProfileEnum.PERSONAL) && userData.getValue() == null) getProfile();
        else switchProfile();

    }

    public void switchProfile() {
        SharedPref sharedPref = new SharedPref();
        shimmerBalanceView.setValue(true);
        switch (switchProfile) {
            case BUSINESS:
                /* Currently user is in his personal profile
                 * Switch to business profile
                 * Get the default business for this user and make it as currentVaultId
                 * Add business selection to keep session across the app
                 */
                LiveData<Business> myDefaultBusinessLiveData = NetworkRepository.getInstance().getMyDefaultBusiness();
                userData.addSource(myDefaultBusinessLiveData, new Observer<Business>() {
                    @Override
                    public void onChanged(Business business) {
                        shimmerBalanceView.setValue(false);
                        if (business == null) {
                            toastMsg.setValue("Please add/join the business to operate with business vault");
                            return;
                        };

                        setProfile(business.getName(), business.getVaultAccountId(), ProfileEnum.BUSINESS);
                        switchProfile = null;
                        sharedPref.setValueToSharedPref(SharedPref.CURRENT_VAULT_ID, business.getVaultAccountId());
                        sharedPref.setBusinessVaultSelected(business);
                        businessSelected = business;

                    }
                });

                break;
            case PERSONAL:
                /* Currently user is in his business profile
                 * Switch to personal profile
                 * Get the personal vault id of the user and make it as currentVaultId
                 * Nullify business selection
                 */
                User user = userData.getValue();
                setProfile(user.getFullName(), user.getVaultAccountId(), ProfileEnum.PERSONAL);
                switchProfile = null;
                sharedPref.setValueToSharedPref(SharedPref.CURRENT_VAULT_ID, user.getVaultAccountId());
                sharedPref.setBusinessVaultSelected(null);
                businessSelected = null;
                shimmerBalanceView.setValue(false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + switchProfile);
        }
    }

    public void setTradeTypes(Util.TradeTypes tradeTypes) {
        this.tradeTypes.setValue(tradeTypes);
    }

    public MutableLiveData<Util.TradeTypes> getTradeTypes() {
        return tradeTypes;
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

    public MediatorLiveData<Balances> getBalances() {
        return balances;
    }

    public Flowable<PagingData<Coins>> getFlowable() {
        return flowable;
    }


    public Business getBusinessSelected() {
        return businessSelected;
    }

    public ProfileEnum getSwitchProfile() {
        return switchProfile;
    }

    public MutableLiveData<Profile> getCurrentProfile() {
        return currentProfile;
    }

    public ObservableField<Boolean> getHasBusinessProfile() {
        return hasBusinessProfile;
    }

    public void setHasBusinessProfile(Boolean hasBusinessProfile) {
        this.hasBusinessProfile.set(hasBusinessProfile);
    }
}