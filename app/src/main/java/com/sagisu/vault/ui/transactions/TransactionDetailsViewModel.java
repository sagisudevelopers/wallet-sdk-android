package com.sagisu.vault.ui.transactions;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sagisu.vault.models.Transaction;

public class TransactionDetailsViewModel extends ViewModel {
    private final MutableLiveData<Transaction> transactionData = new MutableLiveData<>();
    private MutableLiveData<String> copyToClipboard = new MutableLiveData<>();
    private MutableLiveData<String> openBlockExplorer = new MutableLiveData<>();


    public void setTransactionData(Transaction transactionData) {
        this.transactionData.setValue(transactionData);
    }

    public MutableLiveData<Transaction> getTransactionData() {
        return transactionData;
    }

    public MutableLiveData<String> getCopyToClipboard() {
        return copyToClipboard;
    }

    public void setCopyToClipboard(String copyToClipboard) {
        this.copyToClipboard.setValue(copyToClipboard);
    }

    public void copyTransactionId() {
        setCopyToClipboard(transactionData.getValue().getId());
    }

    public void viewTxDetails() {
        if (transactionData.getValue().getTxHash() != null)
            openBlockExplorer.setValue("https://etherscan.io/tx/".concat(transactionData.getValue().getTxHash()));
    }

    public MutableLiveData<String> getOpenBlockExplorer() {
        return openBlockExplorer;
    }
}