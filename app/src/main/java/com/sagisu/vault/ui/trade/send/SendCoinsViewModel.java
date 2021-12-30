package com.sagisu.vault.ui.trade.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vault.models.Coins;
import com.sagisu.vault.models.ValidateAddressResponse;
import com.sagisu.vault.repository.NetworkRepository;

public class SendCoinsViewModel extends ViewModel {
    private MutableLiveData<SendViewType> viewType = new MutableLiveData<>();
    private MutableLiveData<Coins> coins = new MutableLiveData<>();
    private MutableLiveData<String> qrData = new MutableLiveData<>();
    private MutableLiveData<String> receiverVaultId = new MutableLiveData<>();
    private MutableLiveData<String> assetId = new MutableLiveData<>();
    private MediatorLiveData<SendCryptoResponse> sendResponse = new MediatorLiveData<>();
    private MediatorLiveData<ValidateAddressResponse> addressValidationResponse = new MediatorLiveData<>();
    private MutableLiveData<Integer> pageNo = new MutableLiveData<>(1);
    private Integer totalPages = 3;
    private SendViewType[] pages = new SendViewType[]{SendViewType.DESTROY, SendViewType.SELECT_COIN, SendViewType.ENTER_ADDRESS, SendViewType.ENTER_AMOUNT};
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private MutableLiveData<String> toastMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> needCoinSelection = new MutableLiveData<>(true);


    public enum SendViewType {
        SELECT_COIN,
        ENTER_ADDRESS,
        SCAN_QR,
        ENTER_AMOUNT,
        DESTROY
    }

    public void actionSend(String amount, String currencyCode) {
        loadingObservable.setValue("Submitting transaction");
        LiveData<SendCryptoResponse> sendCryptoLiveData = NetworkRepository.getInstance().sendCrypto(amount, currencyCode, qrData.getValue(), receiverVaultId.getValue(), coins.getValue().getSymbol());
        sendResponse.addSource(sendCryptoLiveData, new Observer<SendCryptoResponse>() {
            @Override
            public void onChanged(SendCryptoResponse sendCryptoResponse) {
                loadingObservable.setValue(null);
                sendResponse.setValue(sendCryptoResponse);
            }
        });
    }

    public void validateDestinationAddress(String assetId, String address) {
        loadingObservable.setValue("Validating destination address");
        LiveData<ValidateAddressResponse> validateAddressResponseLiveData = NetworkRepository.getInstance().validateDestinationAddress(assetId, address);
        addressValidationResponse.addSource(validateAddressResponseLiveData, new Observer<ValidateAddressResponse>() {
            @Override
            public void onChanged(ValidateAddressResponse sendCryptoResponse) {
                loadingObservable.setValue(null);
                addressValidationResponse.setValue(sendCryptoResponse);
            }
        });
    }

    public boolean matchAsset(String assetId) {
        //Check if coin selected is same as the qr code asset
        return assetId.equals(coins.getValue().getSymbol());
    }

    public void addressPageNextClick() {
        if (qrData.getValue() == null || qrData.getValue().isEmpty()) {
            toastMsg.setValue("Please input address");
            return;
        }
        ;

        //validateDestinationAddress(coins.getValue().getSymbol(), qrData.getValue());
        setViewType(SendViewType.ENTER_AMOUNT);

    }

    public void scanQrCodeBtnClick() {
        setViewType(SendViewType.SCAN_QR);

    }

    public void setViewType(SendViewType viewType) {
        this.viewType.setValue(viewType);
    }

    public MutableLiveData<SendViewType> getViewType() {
        return viewType;
    }

    public void setCoins(Coins coins) {
        this.coins.setValue(coins);
    }

    public MutableLiveData<Coins> getCoins() {
        return coins;
    }

    public void setReceiverVaultId(String vaultId) {
        this.receiverVaultId.setValue(vaultId);
    }

    public void setQrData(String qrData) {
        this.qrData.setValue(qrData);
    }

    public MutableLiveData<String> getQrData() {
        return qrData;
    }

    public void setAssetId(String assetId) {
        this.assetId.setValue(assetId);
    }

    public MediatorLiveData<SendCryptoResponse> getSendResponse() {
        return sendResponse;
    }

    public MutableLiveData<Integer> getPageNo() {
        return pageNo;
    }

    public void nextPage() {
        Integer page = pageNo.getValue();
        if (page + 1 > totalPages) return;

        page = page + 1;
        pageNo.setValue(page);
    }

    public void goBack() {
        Integer page = pageNo.getValue();
        if (page - 1 < 0) return;

        page = page - 1;
        pageNo.setValue(page);
    }

    public void setPages(Integer pageNo) {
        viewType.setValue(pages[pageNo]);
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public MediatorLiveData<ValidateAddressResponse> getAddressValidationResponse() {
        return addressValidationResponse;
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setNeedCoinSelection(Boolean needCoinSelection) {
        this.needCoinSelection.setValue(needCoinSelection);
    }

    public MutableLiveData<Boolean> getNeedCoinSelection() {
        return needCoinSelection;
    }
}