package com.sinhviencafemanagement.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    // EditText cho tên đăng nhập và mật khẩu
    private TextInputEditText etUsername, etEmail;
    // Layout để hiển thị lỗi
    private TextInputLayout layoutUsername, layoutEmail;

    private ImageView ivBack;

    // Nút tiếp theo
    private MaterialButton btnNext;

    // DAO thao tác với database người dùng
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userDAO = new UserDAO(this);

        initViews(); // Ánh xạ các View từ layout
        setUpListeners(); // Thiết lập các listener cho Button và EditText

    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutEmail = findViewById(R.id.layoutEmail);
        ivBack = findViewById(R.id.ivBack);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setUpListeners() {
        btnNext.setOnClickListener(v -> handleNext());

        // Xóa lỗi khi mất focus
        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etUsername.getText()).toString().trim().isEmpty()) {
                layoutUsername.setError(null);
            }
        });

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etEmail.getText()).toString().trim().isEmpty()) {
                layoutEmail.setError(null);
            }
        });

        // Trở lại màn hình đăng nhập
        ivBack.setOnClickListener(v -> finish());

    }

    private void handleNext() {
        clearErrors(); // Xóa lỗi cũ trước khi kiểm tra

        String username = (etUsername.getText() != null) ? etUsername.getText().toString().trim().toLowerCase() : "";
        String email = (etEmail.getText() != null) ? etEmail.getText().toString().trim().toLowerCase() : "";

        // Kiểm tra rỗng
        if(!validateInput(username, email)) return;

        User user = userDAO.getUserByUsernameOrEmail(username);
        if (user == null) {
            layoutUsername.setError("Tài khoản hoặc email không tồn tại");
            return;
        }

        // Kiểm tra email có khớp ko
        if (!user.getEmail().equalsIgnoreCase(email)) {
            layoutEmail.setError("Email không khớp");
            return;
        }

        // Nếu hợp lệ chuyển sang ResetPasswordActivity
        Intent itResetPassword = new Intent(this, ResetPasswordActivity.class);
        itResetPassword.putExtra("user_id", user.getUserId());
        startActivity(itResetPassword);
        finish();
    }

    // Xóa lỗi cũ
    private void clearErrors() {
        layoutUsername.setError(null);
        layoutEmail.setError(null);
    }

    // Kiểm tra dữ liệu nhập
    private boolean validateInput(String username, String email) {
        if (username.isEmpty()) {
            layoutEmail.setError("Vui lòng nhập tên tài khoản hoặc Email");
            return false;
        }
        if (email.isEmpty()) {
            layoutEmail.setError("Vui lòng nhập email");
            return false;
        }
        return true;
    }

}