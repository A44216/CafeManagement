package com.sinhviencafemanagement.activities.account;


import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView; // Import lớp ImageView
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhviencafemanagement.R;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // --- Bắt đầu phần xử lý nút Back ---

        // 1. Ánh xạ ImageView từ layout bằng ID của nó
        ImageView imgBack = findViewById(R.id.imgBack);

        // 2. Gán sự kiện click cho ImageView
        imgBack.setOnClickListener(v -> {
            // Gọi phương thức finish() để đóng Activity hiện tại và quay lại màn hình trước đó.
            finish();
        });

        Button btnSubmit = findViewById(R.id.btnSendFeedback);
        btnSubmit.setOnClickListener(v -> {
            // Gọi phương thức finish() để đóng Activity hiện tại và quay lại màn hình trước đó.
            Toast.makeText(FeedbackActivity.this, "Gửi phản hồi thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
