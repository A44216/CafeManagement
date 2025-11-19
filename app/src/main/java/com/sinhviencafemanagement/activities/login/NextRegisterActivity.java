package com.sinhviencafemanagement.activities.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.User;

import java.util.Locale;

public class NextRegisterActivity extends AppCompatActivity {

    // --- Khai báo các View ---
    private ImageView ivBack;
    private RadioGroup rgGender;
    private NumberPicker npDay, npMonth, npYear;
    private MaterialButton btnFinish;

    private UserDAO userDAO;
    private Intent registerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo DAO để thao tác SQLite
        userDAO = new UserDAO(this);

        registerIntent = getIntent();

        // Khởi tạo View
        initViews();
        // Gán sự kiện
        setupListeners();
        // Cấu hình NumberPicker
        setupNumberPickers();
    }

    // Khởi tạo và ánh xạ các View trên layout
    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        rgGender = findViewById(R.id.rgGender);

        npDay = findViewById(R.id.npDay);
        npMonth = findViewById(R.id.npMonth);
        npYear = findViewById(R.id.npYear);

        btnFinish = findViewById(R.id.btnFinish);
    }

    // Cấu hình NumberPicker
    private void setupNumberPickers() {
        // Ngày 1 - 31
        setupNumberPicker(npDay, 1, 31, true);

        // Tháng 1 - 12
        setupNumberPicker(npMonth, 1, 12, true);

        // Năm từ 1990 - 2025
        setupNumberPicker(npYear, 1990, 2025, false);

        // Thay đổi ngày theo tháng
        npMonth.setOnValueChangedListener((picker, oldVal, newVal) -> updateDayPicker());

        // Thay đổi ngày theo năm
        npYear.setOnValueChangedListener((picker, oldVal, newVal) -> updateDayPicker());

    }

    // Thiết lập ngày tháng năm và theo dd/mm/yyyy
    private void setupNumberPicker(NumberPicker np, int min, int max, boolean formatTwoDigits) {
        np.setMinValue(min);
        np.setMaxValue(max);
        if (formatTwoDigits) {
            np.setFormatter(value -> String.format(Locale.US, "%02d", value));
        }
    }

    // Thiết lập các sự kiện click, listener cho View
    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnFinish.setOnClickListener(v -> handleFinish());
    }

    private void handleFinish() {
        if (registerIntent == null) {
            showToast("Lỗi dữ liệu đăng ký!");
            return;
        }

        // Lấy dữ liệu từ Intent, trim và chuẩn hóa
        String fullName = safeGetString(registerIntent, "fullName").trim();
        String displayName = safeGetString(registerIntent, "displayName").trim();
        String username = safeGetString(registerIntent, "username").trim().toLowerCase();
        String email = safeGetString(registerIntent, "email").trim().toLowerCase();
        String phone = safeGetString(registerIntent, "phone").trim();
        String password = safeGetString(registerIntent, "password").trim();

        // Kiểm tra rỗng
        if (fullName.isEmpty() || displayName.isEmpty() || username.isEmpty()
                || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin!");
            return;
        }

        // Kiểm tra trùng lặp lần cuối
        if (userDAO.displayNameExists(displayName)) {
            showToast("Tên hiển thị đã tồn tại!");
            return;
        }
        if (userDAO.usernameExists(username)) {
            showToast("Tên tài khoản đã tồn tại!");
            return;
        }
        if (userDAO.emailExists(email)) {
            showToast("Email đã được đăng ký!");
            return;
        }

        // Lấy giới tính
        String gender = getSelectedGender();
        if (gender.equals("Chưa chọn")) {
            showToast("Vui lòng chọn giới tính!");
            return;
        }

        // Lấy ngày sinh
        int day = npDay.getValue();
        int month = npMonth.getValue();
        int year = npYear.getValue();

        int maxDay = getDayInMonth(month, year);
        if (day < 1 || day > maxDay) {
            showToast("Ngày sinh không hợp lệ!");
            return;
        }

        String birthDate = String.format(Locale.US, "%02d/%02d/%d", day, month, year);

        // Tạo User mới
        User newUser = new User(fullName, displayName, username, password, email, phone, gender, birthDate, CreateDatabase.ROLE_CUSTOMER);

        // Thêm vào database với log
        try {
            long id = userDAO.addUser(newUser);
            if (id > 0) {
                showToast("Đăng ký thành công!");
                android.util.Log.d("RegisterDebug", "User added: " + newUser.toString());
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
            } else {
                showToast("Đăng ký thất bại. Vui lòng thử lại.");
                android.util.Log.e("RegisterError", "Insert trả về -1. User: " + newUser.toString());
            }
        } catch (Exception e) {
            showToast("Đăng ký thất bại: " + e.getMessage());
            android.util.Log.e("RegisterError", "Exception khi thêm user", e);
        }
    }

    // Lấy String từ Intent an toàn
    private String safeGetString(Intent intent, String key) {
        String value = intent.getStringExtra(key);
        return value != null ? value : "";
    }

    // Hàm lấy giới tính từ RadioGroup
    private String getSelectedGender() {
        int checkedId = rgGender.getCheckedRadioButtonId();
        if (checkedId == -1) return "Chưa chọn";
        RadioButton selected = findViewById(checkedId);
        return selected.getText() != null ? selected.getText().toString() : "Chưa chọn";
    }

    // Trả về số ngày tối đa trong tháng năm (tính năm nhuận cho tháng 2)
    private int getDayInMonth(int month, int year) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: return 31;
            case 2: return ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) ? 29 : 28;
            default: return 30;
        }
    }

    // Cập nhật số ngày tối đa của NumberPicker ngày
    private void updateDayPicker() {
        int currentDay = npDay.getValue();
        int maxDay = getDayInMonth(npMonth.getValue(), npYear.getValue());
        npDay.setMinValue(1);
        npDay.setMaxValue(maxDay);
        npDay.setValue(Math.min(currentDay, maxDay));
    }

    // Hiện thông báo lỗi theo message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}