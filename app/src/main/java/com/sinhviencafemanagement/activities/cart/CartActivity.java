package com.sinhviencafemanagement.activities.cart;

// ... các import ...
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.order.OrderSuccessActivity;
import com.sinhviencafemanagement.adapter.customer.CartAdapter;
//import com.sinhviencafemanagement.dao.CartManager; // THÊM IMPORT
import com.sinhviencafemanagement.models.CartItem;
import com.sinhviencafemanagement.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements  CartAdapter.OnCartChangeListener {private RecyclerView rcvCartItems;
    private CartAdapter cartAdapter;
    private Button btnOrder;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice); // Ánh xạ TextView tổng tiền

        // Lấy danh sách sản phẩm từ CartManager
        cartItemList = CartManager.getInstance().getCartItems();

        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        btnOrder = findViewById(R.id.btnOrder);
        btnOrder.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast toast = Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 1. Lấy danh sách sản phẩm từ CartManager
        cartItemList = CartManager.getInstance().getCartItems();

        // 2. Khởi tạo Adapter với dữ liệu đã có
        cartAdapter = new CartAdapter(this, cartItemList, this);

        // Cấu hình RecyclerView
        rcvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rcvCartItems.setAdapter(cartAdapter);
        rcvCartItems.setNestedScrollingEnabled(false);

        // === BƯỚC 3.3: TÍNH TỔNG TIỀN LẦN ĐẦU ===
        updateTotalPrice();
    }
    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(format.format(total) + "đ");
    }

    @Override
    public void onCartChanged() {
        // Khi Adapter gọi "sứ giả", phương thức này sẽ được thực thi
        updateTotalPrice(); // Tính toán và cập nhật lại tổng tiền
    }
}
