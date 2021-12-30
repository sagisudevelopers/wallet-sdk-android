package com.sagisu.vault.ui.transactions;

import com.sagisu.vault.models.Transaction;

import java.util.List;

public class GetTransactionResponse {
    List<Transaction> transactions;
    Integer nextPageNumber;
    String cursor;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transaction) {
        this.transactions = transaction;
    }

    public Integer getNextPageNumber() {
        return nextPageNumber;
    }

    public void setNextPageNumber(Integer nextPageNumber) {
        this.nextPageNumber = nextPageNumber;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
