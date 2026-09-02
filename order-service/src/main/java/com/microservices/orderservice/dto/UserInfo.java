package com.microservices.orderservice.dto;

public class UserInfo {

    private Long id;
    private String name;
    private String email;
    private String accountStatus;

    public UserInfo() {
    }

    public UserInfo(
            Long id,
            String name,
            String email,
            String accountStatus) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.accountStatus = accountStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                '}';
    }
}