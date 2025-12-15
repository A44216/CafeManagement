package com.sinhviencafemanagement.activities.home.order;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;

import com.sinhviencafemanagement.R;

public class CartActivity extends AppCompatActivity {

    private Button btnOrder;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        btnOrder = findViewById(R.id.btnOrder);

        btnOrder.setOnClickListener(v -> {
            boolean orderPlaced = processOrderPlacement();

            if (orderPlaced) {
                Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);

                finish();
            } else {
                // Xử lý lỗi (ví dụ: hiển thị Toast "Đặt hàng thất bại")
            }
        });
    }
    private boolean processOrderPlacement() {
        return true;
    }
}
