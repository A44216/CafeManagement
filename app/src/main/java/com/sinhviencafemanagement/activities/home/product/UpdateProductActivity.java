package com.sinhviencafemanagement.activities.home.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Category;
import com.sinhviencafemanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class UpdateProductActivity extends AppCompatActivity {

    private TextInputLayout layoutProductName, layoutProductPrice, layoutProductStatus,
            layoutProductCategory, layoutProductImagePath, layoutProductDescription;

    private TextInputEditText etProductName, etProductPrice,
            etProductImagePath, etProductDescription;

    private ImageView ivBack;
    private MaterialButton btnUpdate;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private AutoCompleteTextView actCategory, actStatus;

    private Product oldProduct;

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

        productDAO = new ProductDAO(this);
        categoryDAO = new CategoryDAO(this);

        initViews(); // Khởi tạo view
        setupStatusDropdown();
        setupCategoryDropdown();
        setUpListeners(); // Set sự kiện

        // Nhận dữ liệu
        oldProduct = (Product) getIntent().getSerializableExtra("product");

        if (oldProduct != null) {
            fillOldData();
        }

    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        layoutProductName = findViewById(R.id.layoutProductNameAdmin);
        layoutProductPrice = findViewById(R.id.layoutProductPriceAdmin);
        layoutProductStatus = findViewById(R.id.layoutProductStatusAdmin);
        layoutProductCategory = findViewById(R.id.layoutProductCategoryAdmin);
        layoutProductImagePath = findViewById(R.id.layoutProductImagePathAdmin);
        layoutProductDescription = findViewById(R.id.layoutProductDescriptionAdmin);

        etProductName = findViewById(R.id.etProductNameAdmin);
        etProductPrice = findViewById(R.id.etProductPriceAdmin);
        etProductImagePath = findViewById(R.id.etProductImagePathAdmin);
        etProductDescription = findViewById(R.id.etProductDescriptionAdmin);

        actStatus = findViewById(R.id.actProductStatusAdmin);
        actCategory = findViewById(R.id.actProductCategoryAdmin);

        btnUpdate = findViewById(R.id.btnUpdateProductAdmin);
    }

    private void fillOldData() {
        etProductName.setText(oldProduct.getProductName());
        etProductPrice.setText(String.valueOf(oldProduct.getPrice()));
        etProductImagePath.setText(oldProduct.getImagePath());
        etProductDescription.setText(oldProduct.getDescription());

        // status
        actStatus.setText(oldProduct.getStatus(), false);

        // category
        Category c = categoryDAO.getCategoryById(oldProduct.getCategoryId());
        if (c != null) {
            actCategory.setText(c.getCategoryName(), false);
        }
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> handleUpdateProduct());
    }


    private void handleUpdateProduct() {
        clearErrors();

        String productName = etProductName.getText() != null ? etProductName.getText().toString().trim() : "";
        String priceStr = etProductPrice.getText() != null ? etProductPrice.getText().toString().trim() : "";
        String status = actStatus.getText() != null ? actStatus.getText().toString().trim() : "";
        String categoryName = actCategory.getText() != null ? actCategory.getText().toString().trim() : ""; //
        String imagePath = etProductImagePath.getText() != null ? etProductImagePath.getText().toString().trim() : "";
        String description = etProductDescription.getText() != null ? etProductDescription.getText().toString().trim() : "";

        boolean isValid = true;

        // Validate input
        if (productName.isEmpty()) {
            layoutProductName.setError("Vui lòng nhập tên sản phẩm");
            isValid = false;
        }
        if (priceStr.isEmpty()) {
            layoutProductPrice.setError("Vui lòng nhập giá");
            isValid = false;
        }
        if (categoryName.isEmpty()) {
            layoutProductCategory.setError("Vui lòng chọn danh mục");
            isValid = false;
        }

        // Nếu tên mới không đổi thì không cần update
        if (productName.equalsIgnoreCase(oldProduct.getProductName())) {
            layoutProductName.setError("Tên không thay đổi");
            return;
        }

        // Kiểm tra trùng
        if (productDAO.nameExists(productName)) {
            layoutProductName.setError("Tên sản phẩm đã tồn tại");
            isValid = false;
        }

        if (!isValid) return;

        // Parse price
        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            layoutProductPrice.setError("Giá không hợp lệ");
            return;
        }

        // Lấy Category ID
        Category selected = categoryDAO.getCategoryByName(categoryName);
        if (selected == null) {
            layoutProductCategory.setError("Danh mục không hợp lệ");
            return;
        }

        // Tạo product update
        Product updatedProduct = new Product(
                oldProduct.getProductId(),
                productName,
                price,
                status,
                oldProduct.getImageResId(),
                imagePath,
                selected.getCategoryId(),
                description
        );

        // Gọi update
        int result = productDAO.updateProduct(updatedProduct);

        if (result > 0) {
            Intent intent = new Intent();
            intent.putExtra("updatedProduct", updatedProduct);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Log.e("UpdateProduct", "Cập nhật thất bại: " + productName);
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }

    }

    // Xóa lỗi cũ
    private void clearErrors() {
        layoutProductName.setError(null);
        layoutProductPrice.setError(null);
        layoutProductStatus.setError(null);
        layoutProductCategory.setError(null);
        layoutProductImagePath.setError(null);
        layoutProductDescription.setError(null);
    }

    private void setupCategoryDropdown() {
        List<Category> categoryList = categoryDAO.getAllCategories();
        List<String> categoryNames = new ArrayList<>();

        for (Category c : categoryList) {
            categoryNames.add(c.getCategoryName());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);

        actCategory.setAdapter(adapter);

        actCategory.setInputType(0); // khóa nhập tay
        actCategory.setKeyListener(null); // khóa bàn phím
    }

    private void setupStatusDropdown() {
        // Mảng trạng thái
        String[] statusOptions = new String[] {CreateDatabase.PRODUCT_STATUS_AVAILABLE, CreateDatabase.PRODUCT_STATUS_UNAVAILABLE};

        // Adapter cho dropdown
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statusOptions);

        actStatus.setAdapter(statusAdapter);

        // Khóa gõ tay
        actStatus.setInputType(InputType.TYPE_NULL);
        actStatus.setKeyListener(null);

        // Optional: click là show dropdown luôn
        actStatus.setOnClickListener(v -> actStatus.showDropDown());
    }

}