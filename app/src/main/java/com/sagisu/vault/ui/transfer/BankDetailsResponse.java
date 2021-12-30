package com.sagisu.vault.ui.transfer;

import com.sagisu.vault.models.Account;
import com.sagisu.vault.models.Institution;

import java.util.List;

public class BankDetailsResponse {
    List<Account> accounts;
    Institution institution;
    String name;

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
