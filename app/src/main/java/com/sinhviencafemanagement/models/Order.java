package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Order implements Clonable<Order>{
    private int orderId;         // order_id
    private int userId;          // user_id (người phụ trách)
    private String orderDate;    // order_date
    private String status;       // status (mặc định "pending")
    private double total;        // total (tổng tiền)
    private Integer tableId;     // table_id (null nếu mua mang về)

    public Order() {

    }

    // Constructor đầy đủ (dùng khi đọc từ DB)
    public Order(int orderId, int userId, String orderDate, String status, double total, Integer tableId) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status != null ? status : "pending";
        this.total = total;
        this.tableId = tableId;
    }

    // Constructor thêm mới (không cần orderId, SQLite tự sinh)
    public Order(int userId, String orderDate, String status, double total, Integer tableId) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status != null ? status : "pending";
        this.total = total;
        this.tableId = tableId;
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

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Integer getTableId() { return tableId; }
    public void setTableId(Integer tableId) { this.tableId = tableId; }

    @NonNull
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", orderDate='" + orderDate + '\'' +
                ", status='" + status + '\'' +
                ", total=" + total +
                ", tableId=" + tableId +
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
                this.total,
                this.tableId
        );

    }

}
