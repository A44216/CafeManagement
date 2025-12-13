package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Topping implements Serializable, Clonable<Topping> {
    private int toppingId;       // topping_id
    private String toppingName;  // topping_name
    private double price;        // price

    public Topping() { }

    // Constructor đầy đủ
    public Topping(int toppingId, String toppingName, double price) {
        this.toppingId = toppingId;
        this.toppingName = toppingName;
        this.price = price;
    }

    // Constructor thêm mới (không cần toppingId)
    public Topping(String toppingName, double price) {
        this.toppingName = toppingName;
        this.price = price;
    }

    // Getter và Setter
    public int getToppingId() { return toppingId; }
    public void setToppingId(int toppingId) { this.toppingId = toppingId; }

    public String getToppingName() { return toppingName; }
    public void setToppingName(String toppingName) { this.toppingName = toppingName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @NonNull
    @Override
    public String toString() {
        return "Topping{" +
                "toppingId=" + toppingId +
                ", toppingName='" + toppingName + '\'' +
                ", price=" + price +
                '}';
    }

    @NonNull
    @Override
    public Topping clone() {
        return new Topping(this.toppingId, this.toppingName, this.price);
    }
}
