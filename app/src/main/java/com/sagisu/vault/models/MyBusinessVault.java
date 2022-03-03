package com.sagisu.vault.models;

public class MyBusinessVault {
    Business business;
    String status;

    public MyBusinessVault() {

    }

    public MyBusinessVault(Business business) {
        this.business = business;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        MyBusinessVault article = (MyBusinessVault) obj;
        return article.business.get_id().equals(this.business.get_id());
    }

}
