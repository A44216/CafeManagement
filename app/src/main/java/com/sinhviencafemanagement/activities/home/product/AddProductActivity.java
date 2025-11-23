package com.sinhviencafemanagement.activities.home.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class AddProductActivity extends AppCompatActivity {

    private TextInputLayout layoutProductName, layoutProductPrice, layoutProductStatus,
            layoutProductCategory, layoutProductImagePath, layoutProductDescription;

    private TextInputEditText etProductName, etProductPrice,
            etProductImagePath, etProductDescription;

    private ImageView ivBack;
    private MaterialButton btnAdd;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    private AutoCompleteTextView actCategory, actStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_product);
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

        btnAdd = findViewById(R.id.btnAddProductAdmin);
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnAdd.setOnClickListener(v -> handleAddProduct());
    }

    private void handleAddProduct() {
        clearErrors();

        String name = etProductName.getText() != null ? etProductName.getText().toString().trim() : "";
        String priceStr = etProductPrice.getText() != null ? etProductPrice.getText().toString().trim() : "";
        String status = actStatus.getText() != null ? actStatus.getText().toString().trim() : "";
        String categoryName = actCategory.getText() != null ? actCategory.getText().toString().trim() : ""; //
        String imagePath = etProductImagePath.getText() != null ? etProductImagePath.getText().toString().trim() : "";
        String description = etProductDescription.getText() != null ? etProductDescription.getText().toString().trim() : "";

        boolean isValid = true;

        // Validate input
        if (name.isEmpty()) {
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

        if (!isValid) return;

        // Chuyển giá sang số
        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            layoutProductPrice.setError("Giá không hợp lệ");
            return;
        }

        int categoryId = -1;
        for (Category c : categoryDAO.getAllCategories()) {
            if (c.getCategoryName().equalsIgnoreCase(categoryName)) {
                categoryId = c.getCategoryId();
                break;
            }
        }

        if (categoryId == -1) {
            layoutProductCategory.setError("Danh mục không hợp lệ");
            return;
        }

        // Tạo Product
        Product product = new Product(name, price, status, 0, imagePath, categoryId, description);

        // Thêm vào DB
        long result = new ProductDAO(this).addProduct(product);

        if (result > 0) {
            // Trả về Product mới
            product.setProductId((int) result); // giả sử Product có setProductId
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newProduct", product); // Product implements Serializable
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show();
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