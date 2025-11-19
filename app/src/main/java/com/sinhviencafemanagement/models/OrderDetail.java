package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class OrderDetail implements Clonable<OrderDetail>{
    private int orderId;       // order_id
    private int productId;     // product_id
    private int quantity;      // quantity

    // Constructor đầy đủ
    public OrderDetail(int orderId, int productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderDetail() {
        
    }

    // Getter & Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @NonNull
    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }

    @NonNull
    @Override
    public OrderDetail clone() {
        return new OrderDetail(
                0,              // orderId = 0 cho order detail mới
                this.productId,
                this.quantity
        );
    }
}
