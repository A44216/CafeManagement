package com.sinhviencafemanagement.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // Khai báo các View để lấy dữ liệu từ UI
    private TextInputEditText etFullName, etDisplayName, etUsername, etEmail, etPhone, etPassword, etPasswordConfirm;
    private TextInputLayout layoutFullName, layoutDisplayName, layoutUsername, layoutEmail, layoutPhone, layoutPassword, layoutPasswordConfirm;
    MaterialButton btnNext;
    private ImageView ivBack;

    // DAO để thao tác với database User
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo DAO để thao tác SQLite
        userDAO = new UserDAO(this);

        initViews();        // ánh xạ View
        setUpListeners();   // xử lý sự kiện

    }

    // Hàm xử lý khi nhấn nút "Next"
    private void handleNext() {
        // Lấy dữ liệu từ EditText
        String fullName = getTextSafe(etFullName);
        String displayName = getTextSafe(etDisplayName);
        String username = getTextSafe(etUsername).toLowerCase();
        String email = getTextSafe(etEmail).toLowerCase();
        String phone = getTextSafe(etPhone);
        String password = getTextSafe(etPassword);
        String passwordConfirm = getTextSafe(etPasswordConfirm);
        // Xóa lỗi cũ nếu có
        clearErrors();

        // Kiểm tra dữ liệu nhập
        if (!validateInput(fullName,displayName, username, email, phone, password, passwordConfirm)) return;

        // Nếu tất cả hợp lệ -> chuyển sang màn hình đăng ký tiếp theo
        Intent intent = new Intent(this, NextRegisterActivity.class);
        intent.putExtra("fullName", fullName);
        intent.putExtra("displayName", displayName);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    // Hàm xóa lỗi cũ
    private void clearErrors() {
        layoutFullName.setError(null);
        layoutDisplayName.setError(null);
        layoutUsername.setError(null);
        layoutEmail.setError(null);
        layoutPhone.setError(null);
        layoutPassword.setError(null);
        layoutPasswordConfirm.setError(null);
    }

    // Khởi tạo và ánh xạ các View trên layout
    private void initViews() {
        // Khởi tạo các EditText để lấy dữ liệu người dùng
        etFullName = findViewById(R.id.etFullName);
        etDisplayName = findViewById(R.id.etDisplayName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        // Khởi tạo để gán sự kiện click
        btnNext = findViewById(R.id.btnNext);
        ivBack = findViewById(R.id.ivBack);

        // Khởi tạo các TextInputLayout để hiển thị lỗi
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutDisplayName = findViewById(R.id.layoutDisplayName);
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutEmail = findViewById(R.id.layoutEmail);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutPassword = findViewById(R.id.layoutPassword);
        layoutPasswordConfirm = findViewById(R.id.layoutPasswordConfirm);

    }

    // Thiết lập các sự kiện click, listener cho View
    private void setUpListeners() {
        // Map các EditText với TextInputLayout tương ứng
        Map<TextInputEditText, TextInputLayout> fields = Map.of(
                etFullName, layoutFullName,
                etDisplayName, layoutDisplayName,
                etUsername, layoutUsername,
                etEmail, layoutEmail,
                etPhone, layoutPhone,
                etPassword, layoutPassword,
                etPasswordConfirm, layoutPasswordConfirm
        );

        // Thêm listener xóa lỗi khi focus mất cho tất cả field (key, value)
        fields.forEach(this::addClearErrorListener);
        // Gán sự kiện cho nút "Back" va "Next"
        ivBack.setOnClickListener(v -> finish());
        btnNext.setOnClickListener(v -> handleNext());

    }

    // Thêm listener để xóa lỗi khi EditText mất focus
    private void addClearErrorListener(TextInputEditText editText, TextInputLayout layout) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !getTextSafe(editText).isEmpty()) {
                layout.setError(null);
            }
        });
    }

    // Hàm kiểm tra dữ liệu nhập
    private boolean validateInput(String fullName, String displayName, String username, String email, String phone,
                                  String password, String passwordConfirm) {

        // 1. Kiểm tra Họ và tên
        if (fullName.isEmpty()) {
            layoutFullName.setError("Vui lòng nhập họ và tên");
            return false;
        }

        // 2. Kiểm tra Tên hiển thị
        if (displayName.isEmpty()) {
            layoutDisplayName.setError("Vui lòng nhập tên hiển thị");
            return false;
        }
        if (userDAO.displayNameExists(displayName)) {
            layoutDisplayName.setError("Tên hiển thị đã tồn tại");
            return false;
        }

        // 3. Kiểm tra Username
        if (username.isEmpty()) {
            layoutUsername.setError("Vui lòng nhập tên tài khoản");
            return false;
        }
        if (username.length() < 7 || username.length() > 25) {
            layoutUsername.setError("Tên tài khoản phải từ 7 đến 25 ký tự");
            return false;
        }
        if (userDAO.usernameExists(username)) {
            layoutUsername.setError("Tên tài khoản đã tồn tại");
            return false;
        }

        // 4. Kiểm tra Email
        if (email.isEmpty()) {
            layoutEmail.setError("Vui lòng nhập Email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Email không đúng định dạng");
            return false;
        }
        if (userDAO.emailExists(email)) {
            layoutEmail.setError("Email đã được đăng ký");
            return false;
        }

        // 5. Kiểm tra Phone
        if (phone.isEmpty()) {
            layoutPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (!phone.matches("\\d{10}")) {
            layoutPhone.setError("Số điện thoại phải gồm 10 chữ số");
            return false;
        }

        // 6. Kiểm tra Password
        if (password.isEmpty()) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        if (password.length() < 7 || password.length() > 25) {
            layoutPassword.setError("Mật khẩu phải từ 7 đến 25 ký tự");
            return false;
        }

        // 7. Kiểm tra PasswordConfirm
        if (passwordConfirm.isEmpty()) {
            layoutPasswordConfirm.setError("Vui lòng nhập lại mật khẩu");
            return false;
        }
        if (!password.equals(passwordConfirm)) {
            layoutPasswordConfirm.setError("Mật khẩu nhập lại không khớp");
            return false;
        }

        return true; // Nếu tất cả hợp lệ
    }



    // Helper để lấy text an toàn
    private String getTextSafe(TextInputEditText editText) {
        return (editText != null && editText.getText() != null) ? editText.getText().toString().trim() : "";
    }

    // Hàm check rỗng
    private boolean checkEmpty(String value, TextInputLayout layout, String errorMsg) {
        if (value == null || value.trim().isEmpty()) {
            layout.setError(errorMsg);
            return true;
        }
        layout.setError(null);
        return false;
    }

}