package com.sinhviencafemanagement.models;

public class CartItem {
    // Các thuộc tính tương ứng với các View trong item_cart.xml
    private int id;
    private String name;
    private String description;
    private double price;    private int quantity;
    private String imageUrl; // Dùng String để linh hoạt, có thể là link ảnh hoặc tên file trong drawable

    // Constructor
    public CartItem(int id, String name, String description, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
