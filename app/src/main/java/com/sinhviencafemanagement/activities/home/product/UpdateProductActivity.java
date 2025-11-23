package com.sinhviencafemanagement.activities.home.product;

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

public class UpdateProductActivity extends AppCompatActivity {

    private TextInputLayout layoutProductName, layoutProductPrice, layoutProductStatus, layoutProductImage, layoutProductCategory, layoutProductDescription;
    private TextInputEditText etProductName, etProductPrice, etProductStatus, etProductImage, etProductCategory, etProductDescription;

    private ImageView ivBack;
    private MaterialButton btnUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        layoutProductName = findViewById(R.id.layoutProductNameAdmin);
        layoutProductPrice = findViewById(R.id.layoutProductPriceAdmin);
        layoutProductStatus = findViewById(R.id.layoutProductStatusAdmin);
        layoutProductImage = findViewById(R.id.layoutProductImageAdmin);
        layoutProductCategory = findViewById(R.id.layoutProductCategoryAdmin);
        layoutProductDescription = findViewById(R.id.layoutProductDescriptionAdmin);

        etProductName = findViewById(R.id.etProductNameAdmin);
        etProductPrice = findViewById(R.id.etProductPriceAdmin);
        etProductStatus = findViewById(R.id.etProductStatusAdmin);
        etProductImage = findViewById(R.id.etProductImageAdmin);
        etProductCategory = findViewById(R.id.etProductCategoryAdmin);
        etProductDescription = findViewById(R.id.etProductDescriptionAdmin);

        btnUpdate = findViewById(R.id.btnUpdateCategoryAdmin);

    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> handleUpdateProduct());

    }

    private void handleUpdateProduct() {

    }

}