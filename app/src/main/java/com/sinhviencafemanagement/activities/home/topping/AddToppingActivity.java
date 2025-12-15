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

public class AddToppingActivity extends AppCompatActivity {

    private TextInputLayout layoutName, layoutPrice;
    private TextInputEditText etToppingName, etPrice;
    private ImageView ivBack;
    private MaterialButton btnAddTopping;

    private ToppingDAO toppingDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_topping);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toppingDAO = new ToppingDAO(this);

        initViews();
        setUpListeners();
    }

    private void initViews() {
        layoutName = findViewById(R.id.layoutToppingNameAdmin);
        layoutPrice = findViewById(R.id.layoutToppingPriceAdmin);
        etToppingName = findViewById(R.id.etToppingNameAdmin);
        etPrice = findViewById(R.id.etToppingPriceAdmin);
        ivBack = findViewById(R.id.ivBack);
        btnAddTopping = findViewById(R.id.btnAddToppingAdmin);
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnAddTopping.setOnClickListener(v -> handleAddTopping());
    }

    private void handleAddTopping() {
        clearErrors();

        String name = etToppingName.getText() != null
                ? etToppingName.getText().toString().trim() : "";
        String priceStr = etPrice.getText() != null
                ? etPrice.getText().toString().trim() : "";

        if (!validateInput(name, priceStr)) return;

        if (toppingDAO.toppingExists(name)) {
            layoutName.setError("Topping đã tồn tại");
            return;
        }

        double price = Double.parseDouble(priceStr);
        long id = toppingDAO.addTopping(new Topping(name, price));

        if (id > 0) {
            Topping newTopping = new Topping((int) id, name, price);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newTopping", newTopping);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Log.e("AddTopping", "Thêm thất bại: " + name);
            Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
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
