package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Order implements Clonable<Order>{
    private int orderId;         // order_id
    private int userId;          // user_id (người phụ trách)
    private String orderDate;    // order_date
    private String status;       // status (mặc định "pending")
    private double totalPrice;   // total (tổng tiền)
    private int addressId;   // address_id (bắt buộc – delivery)

    public Order() {

    }

    // Constructor đầy đủ (dùng khi đọc từ DB)
    public Order(int orderId, int userId, String orderDate, String status, double totalPrice, int addressId) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status != null ? status : "pending";
        this.totalPrice = totalPrice;
        this.addressId = addressId;
    }

    // Constructor tạo đơn mới – KHÔNG truyền orderId, orderDate
    public Order(int userId, int addressId) {
        this.userId = userId;
        this.addressId = addressId;
        this.status = "pending";
        this.totalPrice = 0;
    }

    // Getter & Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status != null ? status : "pending"; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }

    @NonNull
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", totalPrice=" + totalPrice +
                ", addressId=" + addressId +
                '}';
    }

    @NonNull
    @Override
    public Order clone() {
        // Tạo bản sao shallow copy các trường hiện tại
        return new Order(
                0,                  // orderId = 0 cho đơn mới
                this.userId,
                this.orderDate,
                this.status,
                this.totalPrice,
                this.addressId
        );

    }

}
