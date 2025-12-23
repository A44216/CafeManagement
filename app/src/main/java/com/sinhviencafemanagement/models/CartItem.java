package com.sinhviencafemanagement.models;

import java.util.List;

public class CartItem {
    // Các thuộc tính tương ứng với các View trong item_cart.xml
    private int id;
    private String name;
    private int productId;
    private String description;
    private double price;
    private int quantity;
    private Object imageIdentifier;
    private List<Topping> selectedToppings;

    // Constructor
    public CartItem(int id, int productId, String name, String description, double price, int quantity, Object imageIdentifier, List<Topping> selectedToppings) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageIdentifier = imageIdentifier;
        this.selectedToppings = selectedToppings;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public Object getImageIdentifier() { return imageIdentifier; }
    public List<Topping> getSelectedToppings() {
        return selectedToppings;
    }
    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getProductId() {
        return productId;
    }
}
