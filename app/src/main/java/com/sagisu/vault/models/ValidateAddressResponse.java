package com.sagisu.vault.models;

public class ValidateAddressResponse {
    private boolean isValid;
    private boolean isActive;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
