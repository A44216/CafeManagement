package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;
import java.io.Serializable;

// Model này đã được đơn giản hóa để khớp với cấu trúc CSDL và AddressDAO
public class Address implements Serializable {

    private int addressId;
    private int userId;
    private String address; // Một trường duy nhất để lưu toàn bộ chuỗi địa chỉ

    // Constructor rỗng
    public Address() {
    }

    // Constructor đầy đủ (dùng khi đọc từ DB trong DAO)
    public Address(int addressId, String address, int userId) {
        this.addressId = addressId;
        this.address = address;
        this.userId = userId;
    }

    // Constructor để thêm địa chỉ mới (không cần addressId)
    public Address(int userId, String address) {
        this.userId = userId;
        this.address = address;
    }

    // --- Getters và Setters ---

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @NonNull
    @Override
    public String toString() {
        return "Address{" +
                "addressId=" + addressId +
                ", userId=" + userId +
                ", address='" + address + '\'' +
                '}';
    }
}
