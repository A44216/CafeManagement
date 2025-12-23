package com.sinhviencafemanagement.utils;

import com.sinhviencafemanagement.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    private static CartManager instance;

    private final List<CartItem> cartItems;

    // Constructor private để ngăn việc tạo đối tượng từ bên ngoài
    private CartManager() {
        cartItems = new ArrayList<>();
    }
    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }
    public void addItem(CartItem newItem) {
        // Duyệt qua danh sách các item hiện có
        for (CartItem existingItem : cartItems) {
            // Nếu tìm thấy một item có cùng ID (ID này nên được tạo dựa trên ID sản phẩm và các tùy chọn)
            if (existingItem.getId() == newItem.getId()) {
                // Chỉ cộng dồn số lượng
                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                return; // Cập nhật xong và thoát khỏi phương thức
            }
        }
        // Nếu không tìm thấy item nào trùng, mới thêm item mới vào danh sách
        cartItems.add(newItem);
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void clearCart() {
        cartItems.clear();
    }
}