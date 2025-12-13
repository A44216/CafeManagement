package com.sinhviencafemanagement.activities.home.topping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.Topping;

public class UpdateToppingActivity extends AppCompatActivity {

    private TextInputLayout layoutName, layoutPrice;
    private TextInputEditText etToppingName, etPrice;
    private ImageView ivBack;
    private MaterialButton btnUpdateTopping;

    private ToppingDAO toppingDAO;

    private int toppingId;
    private String oldName;
    private double oldPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_topping);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toppingDAO = new ToppingDAO(this);

        initViews();
        setUpListeners();

        // Nhận topping từ intent
        Topping topping = (Topping) getIntent().getSerializableExtra("topping");
        if (topping != null) {
            toppingId = topping.getToppingId();
            oldName = topping.getToppingName();
            oldPrice = topping.getPrice();

            etToppingName.setText(oldName);
            etPrice.setText(String.valueOf(oldPrice));
        }
    }

    private void initViews() {
        layoutName = findViewById(R.id.layoutToppingNameAdmin);
        layoutPrice = findViewById(R.id.layoutToppingPriceAdmin);
        etToppingName = findViewById(R.id.etToppingNameAdmin);
        etPrice = findViewById(R.id.etToppingPriceAdmin);
        ivBack = findViewById(R.id.ivBack);
        btnUpdateTopping = findViewById(R.id.btnUpdateToppingAdmin);
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnUpdateTopping.setOnClickListener(v -> handleUpdateTopping());
    }

    private void handleUpdateTopping() {
        clearErrors();

        String name = etToppingName.getText() != null
                ? etToppingName.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null
                ? etPrice.getText().toString().trim() : "";

        if (!validateInput(name, priceStr)) return;

        double price = Double.parseDouble(priceStr);

        if (name.equalsIgnoreCase(oldName) && price == oldPrice) {
            layoutName.setError("Không có thay đổi");
            return;
        }

        if (!name.equalsIgnoreCase(oldName) && toppingDAO.toppingExists(name)) {
            layoutName.setError("Topping đã tồn tại");
            return;
        }

        Topping topping = new Topping(toppingId, name, price);
        int result = toppingDAO.updateTopping(topping);

        if (result > 0) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedTopping", topping);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Log.e("UpdateTopping", "Cập nhật thất bại: " + name);
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearErrors() {
        layoutName.setError(null);
        layoutPrice.setError(null);
    }

    private boolean validateInput(String name, String priceStr) {
        if (name.isEmpty()) {
            layoutName.setError("Vui lòng nhập tên topping");
            return false;
        }
        if (priceStr.isEmpty()) {
            layoutPrice.setError("Vui lòng nhập giá");
            return false;
        }
        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                layoutPrice.setError("Giá phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            layoutPrice.setError("Giá không hợp lệ");
            return false;
        }
        return true;
    }
}
