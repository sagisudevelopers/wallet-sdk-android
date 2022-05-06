package com.sagisu.vaultLibrary.ui.OTP;

public class Otp {
    private String phone;
    private String name = "User";
    private String event;
    private String countryCode = "+91";
    private String actorType;
    private String email;

    public Otp(String phone, String name, String event, String actorType, String countryCode) {
        this.phone = phone;
        this.name = name;
        this.event = event;
        this.actorType = actorType;
        this.countryCode = countryCode;
    }

    public Otp(String email, String name) {
        this.email = email;
        if (name == null || name.isEmpty()) return;
        this.name = name;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getActorType() {
        return actorType;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) return;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
