package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Category {
    private int categoryId;           // category_id
    private String categoryName;      // category_name
    private String image;             // image URL hoặc path

    public Category() {

    }

    // Constructor đầy đủ
    public Category(int categoryId, String categoryName, String image) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.image = image;
    }

    // Constructor thêm mới (không cần categoryId)
    public Category(String categoryName, String image) {
        this.categoryName = categoryName;
        this.image = image;
    }

    // Getter và Setter
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", image='" + image + '\'' +
                '}';
    }

}
