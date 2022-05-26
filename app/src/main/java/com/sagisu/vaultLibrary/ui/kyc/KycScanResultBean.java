package com.sagisu.vaultLibrary.ui.kyc;

import androidx.databinding.BaseObservable;

public class KycScanResultBean extends BaseObservable {
    private String scanReference;
    private KycBean document;
    private Transaction transaction;

    public String getScanReference() {
        return scanReference;
    }

    public void setScanReference(String scanReference) {
        this.scanReference = scanReference;
    }

    public KycBean getDocument() {
        return document;
    }

    public void setDocument(KycBean document) {
        this.document = document;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    class Transaction {
        private String status;

        public String getStatus() {
            return status;
        }
    }
}
