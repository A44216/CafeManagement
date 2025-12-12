package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class OrderDetailTopping implements Serializable, Clonable<OrderDetailTopping> {
    private int orderId;      // order_id
    private int productId;    // product_id
    private int toppingId;    // topping_id
    private int quantity;     // quantity

    public OrderDetailTopping() { }

    // Constructor đầy đủ
    public OrderDetailTopping(int orderId, int productId, int toppingId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.toppingId = toppingId;
        this.quantity = quantity;
    }

    // Getter và Setter
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @NonNull
    @Override
    public String toString() {
        return "OrderDetailTopping{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", toppingId=" + toppingId +
                ", quantity=" + quantity +
                '}';
    }

    @NonNull
    @Override
    public OrderDetailTopping clone() {
        return new OrderDetailTopping(this.orderId, this.productId, this.toppingId, this.quantity);
    }
}
