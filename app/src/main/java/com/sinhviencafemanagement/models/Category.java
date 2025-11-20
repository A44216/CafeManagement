package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Category {
    private int categoryId;           // category_id
    private String categoryName;      // category_name

    public Category() {

    }

    // Constructor đầy đủ
    public Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Constructor thêm mới (không cần categoryId)
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    // Getter và Setter
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }


    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }

}
