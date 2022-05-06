package com.sagisu.vaultLibrary.ui.trade.receive;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.models.Coins;
import com.sagisu.vaultLibrary.repository.NetworkRepository;

public class ReceiveCryptoViewModel extends ViewModel {
    private MutableLiveData<Coins> coins = new MutableLiveData<>();
    private MediatorLiveData<ReceiveCryptoResponse> receiveCryptoResponse = new MediatorLiveData<>();
    private MutableLiveData<Integer> pageNo = new MutableLiveData<>(1);
    private MutableLiveData<Integer> addPage = new MutableLiveData<>(pageNo.getValue());
    private final MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> needCoinSelection = new MutableLiveData<>(true);

    private Integer totalPages = 3;

    public void init() {
        if (receiveCryptoResponse.getValue() != null && receiveCryptoResponse.getValue().getAssetId().equals(coins.getValue().getSymbol())) {
            return;
        }
        receiveCryptoResponse.setValue(null);
        receiveCrypto();
    }

    public void receiveCrypto() {
        loadingObservable.setValue("Generating QR Code");
        LiveData<ReceiveCryptoResponse> qrCodeLiveData = NetworkRepository.getInstance().receiveCrypto(coins.getValue().getSymbol());
        receiveCryptoResponse.addSource(qrCodeLiveData, new Observer<ReceiveCryptoResponse>() {
            @Override
            public void onChanged(ReceiveCryptoResponse s) {
                loadingObservable.setValue(null);
                receiveCryptoResponse.postValue(s);
            }
        });
    }

    public void nextPage() {
        Integer page = pageNo.getValue();
        if (page + 1 > totalPages) return;

        page = page + 1;
        pageNo.setValue(page);
        addPage.setValue(page);
    }

    public void goBack(boolean addPage) {
        Integer page = pageNo.getValue();
        if (page - 1 < 0) return;

        page = page - 1;
        if (page == 1 && !needCoinSelection.getValue()) page = 0;
        pageNo.setValue(page);
        if (page == 0 || addPage)
            this.addPage.setValue(pageNo.getValue());
    }

    public void setCoins(Coins coins) {
        this.coins.setValue(coins);
    }

    public MutableLiveData<Coins> getCoins() {
        return coins;
    }

    public MediatorLiveData<ReceiveCryptoResponse> getReceiveCryptoResponse() {
        return receiveCryptoResponse;
    }

    public MutableLiveData<Integer> getPageNo() {
        return pageNo;
    }

    public MutableLiveData<Integer> getAddPage() {
        return addPage;
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }

    public void setNeedCoinSelection(Boolean needCoinSelection) {
        this.needCoinSelection.setValue(needCoinSelection);
    }
}