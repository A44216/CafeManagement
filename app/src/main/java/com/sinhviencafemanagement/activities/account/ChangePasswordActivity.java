package com.sinhviencafemanagement.activities.account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.UserDAO;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnConfirmChange;
    private ImageView imgBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Liên kết Activity với file layout của nó
        setContentView(R.layout.activity_change_password);

        // Ánh xạ các View từ layout
        initUI();

        // Cài đặt sự kiện cho các nút bấm
        initListeners();
    }

    /**
     * Phương thức để ánh xạ các thành phần giao diện từ file XML.
     */
    private void initUI() {
        edtOldPassword = findViewById(R.id.edtOldPass);
        edtNewPassword = findViewById(R.id.edtNewPass);
        edtConfirmPassword = findViewById(R.id.edtConfirmPass);
        btnConfirmChange = findViewById(R.id.btnChangePass);
        imgBack = findViewById(R.id.imgBack);
    }

    /**
     * Phương thức để cài đặt các bộ lắng nghe sự kiện (event listeners).
     */
    private void initListeners() {
        // Sự kiện cho nút quay lại
        imgBack.setOnClickListener(v -> {
            finish(); // Đóng Activity hiện tại và quay về màn hình trước đó
        });

        // Sự kiện cho nút Xác nhận thay đổi
        btnConfirmChange.setOnClickListener(v -> {
            handleChangePassword();
        });
    }

    /**
     * Xử lý logic chính khi người dùng nhấn nút đổi mật khẩu.
     */
    private void handleChangePassword() {
        // Lấy dữ liệu từ các EditText
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // 1. Kiểm tra các trường có bị bỏ trống không
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return; // Dừng xử lý
        }

        // 2. Kiểm tra mật khẩu mới có khớp với mật khẩu xác nhận không
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return; // Dừng xử lý
        }

        // 3. Kiểm tra mật khẩu cũ có đúng không

        // Bước 3.1: Lấy userId của người dùng đang đăng nhập từ SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1); // -1 là giá trị mặc định nếu không tìm thấy

        // Kiểm tra xem có lấy được userId hợp lệ không
        if (userId == -1) {
            Toast.makeText(this, "Không thể xác định người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            // Có thể chuyển về màn hình đăng nhập ở đây
            return;
        }

        // Bước 3.2: Khởi tạo UserDAO và kiểm tra mật khẩu
        UserDAO userDAO = new UserDAO(this);
        boolean isOldPasswordCorrect = userDAO.checkPassword(userId, oldPassword);


        if (!isOldPasswordCorrect) {
            Toast.makeText(this, "Mật khẩu cũ không chính xác", Toast.LENGTH_SHORT).show();
            return; // Dừng xử lý
        }

        // 4. Nếu mọi thứ đều hợp lệ, thực hiện cập nhật mật khẩu trong Database
        boolean isUpdateSuccess = userDAO.changePassword(userId, oldPassword, newPassword);

        if (isUpdateSuccess) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
            finish(); // Đóng màn hình đổi mật khẩu và quay về trang tài khoản
        } else {
            Toast.makeText(this, "Đã có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
}
