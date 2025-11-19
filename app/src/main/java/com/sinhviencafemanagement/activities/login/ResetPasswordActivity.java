package com.sinhviencafemanagement.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {


    // EditText cho newPassword và passwordConfirm
    private TextInputEditText etNewPassword, etPasswordConfirm;
    // Layout để hiển thị lỗi
    private TextInputLayout layoutNewPassword, layoutPasswordConfirm;

    // Nút tiếp theo
    private MaterialButton btnFinish;
    private ImageView ivBack;

    // DAO thao tác với database người dùng
    private UserDAO userDAO;
    private int userId; // user_id được truyền từ ForgotPasswordActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userDAO = new UserDAO(this);

        // Lấy user_id từ Intent
        userId = getIntent().getIntExtra("user_id", -1);

        initViews(); // Ánh xạ các View từ layout
        setUpListeners(); // Thiết lập các listener cho Button và EditText

    }

    private void initViews() {
        etNewPassword = findViewById(R.id.etNewPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutPasswordConfirm = findViewById(R.id.layoutPasswordConfirm);
        ivBack = findViewById(R.id.ivBack);
        btnFinish = findViewById(R.id.btnFinish);
    }

    private void setUpListeners() {
        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Xóa lỗi khi mất focus
        etNewPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etNewPassword.getText()).toString().trim().isEmpty()) {
                layoutNewPassword.setError(null);
            }
        });
        etPasswordConfirm.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etPasswordConfirm.getText()).toString().trim().isEmpty()) {
                layoutPasswordConfirm.setError(null);
            }
        });

        // Nút hoàn tất
        btnFinish.setOnClickListener(v -> handleResetPassword());
    }

    private void handleResetPassword() {
        clearErrors();

        String newPassword = (etNewPassword.getText() != null) ? etNewPassword.getText().toString().trim() : "";
        String confirmPassword = (etPasswordConfirm.getText() != null) ? etPasswordConfirm.getText().toString().trim() : "";

        if (!validateInput(newPassword, confirmPassword)) return;

        if(userId <= 0){
            Toast.makeText(this, "Người dùng không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật mật khẩu
        boolean success = userDAO.resetPassword(userId, newPassword);
        if (!success) {
            layoutNewPassword.setError("Đặt mật khẩu thất bại, thử lại");
            return;
        }

        // Xóa tất cả session cũ của user
        SessionDAO sessionDAO = new SessionDAO(this);
        sessionDAO.deleteSessionsByUserId(userId);

        Toast.makeText(this, "Đặt mật khẩu thành công!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));

        finish();

    }

    private void clearErrors() {
        layoutNewPassword.setError(null);
        layoutPasswordConfirm.setError(null);
    }

    private boolean validateInput(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty()) {
            layoutNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            layoutPasswordConfirm.setError("Vui lòng xác nhận mật khẩu");
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            layoutPasswordConfirm.setError("Mật khẩu không khớp");
            return false;
        }
        return true;
    }

}