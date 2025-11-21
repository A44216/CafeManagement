package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Product implements Clonable<Product>{
    private int productId;           // product_id
    private String productName;      // product_name
    private double price;            // price
    private String status;           // status (mặc định "available")
    private int imageResId;          // lưu id ảnh trực tiếp
    private Integer categoryId;      // category_id (có thể null)
    private String description;      // description (có thể null)

    public Product() {

    }

    // Constructor đầy đủ
    public Product(int productId, String productName, double price, String status,
                   int imageResId, Integer categoryId, String description) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.status = status != null ? status : "available"; // mặc định
        this.imageResId = imageResId;
        this.categoryId = categoryId;
        this.description = description;
    }

    // Constructor thêm mới (không cần productId, SQLite tự sinh)
    public Product(String productName, double price, String status,
                   int imageResId, Integer categoryId, String description) {
        this.productName = productName;
        this.price = price;
        this.status = status != null ? status : "available"; // mặc định
        this.imageResId = imageResId;
        this.categoryId = categoryId;
        this.description = description;
    }

    // Getter và Setter
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status != null ? status : "available";
    }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", imageResId='" + imageResId + '\'' +
                ", categoryId=" + categoryId +
                ", description='" + description + '\'' +
                '}';
    }

    @NonNull
    @Override
    public Product clone() {
        return new Product(
                0,                       // productId = 0 cho sản phẩm mới
                this.productName,
                this.price,
                this.status,
                this.imageResId,
                this.categoryId,
                this.description
        );
    }

}
