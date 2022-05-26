package com.sagisu.vaultLibrary.ui.contacts;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sagisu.vaultLibrary.models.Transaction;
import com.sagisu.vaultLibrary.network.VaultAPIError;
import com.sagisu.vaultLibrary.network.VaultResult;
import com.sagisu.vaultLibrary.repository.NetworkRepository;
import com.sagisu.vaultLibrary.utils.Globals;

public class ContactTransferViewModel extends ViewModel {
    private final MutableLiveData<String> chatMessage = new MutableLiveData<>();
    private MutableLiveData<String> toastMsg = new MutableLiveData<>();
    private MediatorLiveData<String> contactTransferResponse = new MediatorLiveData<>();
    private ObservableField<ContactsInfo> contactInfo = new ObservableField<>();
    private MutableLiveData<Boolean> showConfirmationPopup = new MutableLiveData<>(false);
    private MutableLiveData<String> loadingObservable = new MutableLiveData<>(null);
    private String countryName;

    public void onPayClicked() {
        /*if (!countryName.equals(CountryNameDescriptor.US)) {
            setToastMsg("Payments allowed only for US numbers");
            return;
        }*/

        if (chatMessage.getValue() != null && TextUtils.isDigitsOnly(chatMessage.getValue())) {
            //Show confirmation popup
            //doTransfer();
            showConfirmationPopup.setValue(true);
        } else {
            setToastMsg("Enter a valid amount");
        }
    }

    public void doTransfer() {
        setLoadingObservable("Transferring amount");
        LiveData<VaultResult<Transaction>> contactTransferRes = NetworkRepository.getInstance().contactTransfer(contactInfo.get(), Integer.valueOf(chatMessage.getValue()));
        contactTransferResponse.addSource(contactTransferRes, new Observer<VaultResult<Transaction>>() {
            @Override
            public void onChanged(VaultResult<Transaction> jsonObjectResult) {
                setLoadingObservable(null);
                if (jsonObjectResult instanceof VaultResult.Success) {
                    Transaction transaction = ((VaultResult.Success<Transaction>) jsonObjectResult).getData();
                    contactTransferResponse.setValue(transaction.getId());
                } else if (jsonObjectResult instanceof VaultResult.Error) {
                    VaultAPIError vaultApiError = ((VaultResult.Error) jsonObjectResult).getError();
                    toastMsg.setValue(vaultApiError.message());
                }
            }
        });
    }

    public LiveData<Transaction> getTransaction(String txid) {
        return NetworkRepository.getInstance().getTransaction(txid);
    }

    public void sendMessage() {
        setToastMsg(chatMessage.getValue());
    }

    public MutableLiveData<String> getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage.setValue(chatMessage);
    }

    public MutableLiveData<String> getToastMsg() {
        return toastMsg;
    }

    public void setToastMsg(String toastMsg) {
        this.toastMsg.setValue(toastMsg);
    }

    public void setContactInfo(ContactsInfo contactInfo) {
        this.contactInfo.set(contactInfo);
        this.countryName = Globals.getCountryName(contactInfo.getPhoneNumber());
    }

    public ObservableField<ContactsInfo> getContactInfo() {
        return contactInfo;
    }

    public MediatorLiveData<String> getContactTransferResponse() {
        return contactTransferResponse;
    }

    public void setContactTransferResponse(String contactTransferResponse) {
        this.contactTransferResponse.setValue(contactTransferResponse);
    }

    public MutableLiveData<Boolean> getShowConfirmationPopup() {
        return showConfirmationPopup;
    }

    public void setShowConfirmationPopup(Boolean showConfirmationPopup) {
        this.showConfirmationPopup.setValue(showConfirmationPopup);
    }

    public void setLoadingObservable(String loadingObservable) {
        this.loadingObservable.setValue(loadingObservable);
    }

    public MutableLiveData<String> getLoadingObservable() {
        return loadingObservable;
    }
}