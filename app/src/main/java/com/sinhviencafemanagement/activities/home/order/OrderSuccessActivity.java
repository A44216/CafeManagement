package com.sinhviencafemanagement.activities.home.order;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent; // Import cần thiết
import android.widget.Button;
import android.widget.ImageView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.UserHomeActivity;

public class OrderSuccessActivity extends AppCompatActivity {

    private ImageView imgBack;
    private Button btnTrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        imgBack = findViewById(R.id.imgBack);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, UserHomeActivity.class);
            startActivity(intent);
        });

        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, TrackOrderActivity.class);
            startActivity(intent);
        });
    }
}
