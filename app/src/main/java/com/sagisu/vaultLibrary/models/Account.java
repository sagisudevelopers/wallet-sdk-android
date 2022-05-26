package com.sagisu.vaultLibrary.models;

import com.google.gson.annotations.SerializedName;
import com.sagisu.vaultLibrary.ui.home.Balances;

import org.jetbrains.annotations.NotNull;

public class Account {
    @SerializedName("accountNumber")
    private String account;
    private String name;
    private String official_name;
    private String account_id;
    private Balances balances;
    private String institutionName;
    private String owner;
    private String routingNumber;
    private String accountType;

    public Account() {

    }

    public Account(String account_id, String name) {
        this.setAccount_id(account_id);
        setName(name);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public String getOfficial_name() {
        return official_name;
    }

    public void setOfficial_name(String official_name) {
        this.official_name = official_name;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public Balances getBalances() {
        return balances;
    }

    public void setBalances(Balances balances) {
        this.balances = balances;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber) {
        this.routingNumber = routingNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /*@NotNull
    @Override
    public String toString() {
        if (account == null) return name;
        return account.substring(account.length() - 4);
    }*/

    @NotNull
    @Override
    public String toString() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
