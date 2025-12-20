package com.sinhviencafemanagement.activities.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.AddressDAO;
import com.sinhviencafemanagement.models.Address;
import com.sinhviencafemanagement.utils.SessionManager;

public class AddAddressActivity extends AppCompatActivity {

    // Khai báo các View
    private TextInputEditText edtFullName, edtPhoneNumber, edtCity, edtDistrict, edtWard, edtStreetAddress;
    private CheckBox cbSetAsDefault;
    private Button btnSaveAddress;
    private ImageView imgBack;

    // Khai báo các đối tượng xử lý dữ liệu
    private AddressDAO addressDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Khởi tạo
        initView();
        initData();
        setEventListeners();
    }

    private void initView() {
        // Ánh xạ các View từ layout
        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtCity = findViewById(R.id.edtCity);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtWard = findViewById(R.id.edtWard);
        edtStreetAddress = findViewById(R.id.edtStreetAddress);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        imgBack = findViewById(R.id.imgBack);
    }

    private void initData() {
        // Khởi tạo các đối tượng DAO và SessionManager
        addressDAO = new AddressDAO(this);
        sessionManager = new SessionManager(this);
    }

    private void setEventListeners() {
        // Sự kiện cho nút Back
        imgBack.setOnClickListener(v -> finish());

        // Sự kiện cho nút Lưu địa chỉ
        btnSaveAddress.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        // 1. Lấy dữ liệu từ các ô nhập liệu
        String fullName = edtFullName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String city = edtCity.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();
        String street = edtStreetAddress.getText().toString().trim();

        // 2. Kiểm tra dữ liệu đầu vào
        if (fullName.isEmpty() || phoneNumber.isEmpty() || city.isEmpty() || district.isEmpty() || ward.isEmpty() || street.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin địa chỉ", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullAddressString = fullName + " | " + phoneNumber + "\n" +
                street + ", " + ward + ", " + district + ", " + city;
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 5. Tạo đối tượng Address mới
        Address newAddress = new Address();
        newAddress.setUserId(userId);
        newAddress.setAddress(fullAddressString);

        boolean success = addressDAO.addAddress(newAddress);

        // 7. Thông báo kết quả và kết thúc
        if (success) {
            Toast.makeText(this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity và quay lại màn hình trước
        } else {
            Toast.makeText(this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}