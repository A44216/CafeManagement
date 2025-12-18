package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Address {
    private int addressId;   // address_id
    private String address;  // address
    private int userId;      // user_id

    // Constructor đầy đủ
    public Address(int addressId, String address, int userId) {
        this.addressId = addressId;
        this.address = address;
        this.userId = userId;
    }

    // Constructor thêm mới (không cần id, tự sinh trong DB)
    public Address(String address, int userId) {
        this.address = address;
        this.userId = userId;
    }

    // Getter và Setter
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", address='" + address + '\'' +
                ", userId=" + userId +
                '}';
    }

}
