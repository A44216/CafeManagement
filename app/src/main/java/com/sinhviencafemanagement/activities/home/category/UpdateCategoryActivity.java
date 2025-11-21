package com.sinhviencafemanagement.activities.home.category;

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
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

public class UpdateCategoryActivity extends AppCompatActivity {

    private TextInputLayout layout;
    private TextInputEditText etCategoryName;
    private ImageView ivBack;
    private MaterialButton btnUpdateCategory;

    CategoryDAO categoryDAO;

    int categoryId;
    String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryDAO = new CategoryDAO(this);

        initViews(); // Khởi tạo view
        setUpListeners(); // Set sự kiện

        // Nhận dữ liệu
        categoryId = getIntent().getIntExtra("category_id", -1);
        oldName = getIntent().getStringExtra("category_name");

        // Gán vào EditText
        etCategoryName.setText(oldName);

    }

    private void initViews() {
        layout = findViewById(R.id.layoutCategoryNameAdmin);
        etCategoryName = findViewById(R.id.etCategoryNameAdmin);
        ivBack = findViewById(R.id.ivBack);
        btnUpdateCategory = findViewById(R.id.btnUpdateCategoryAdmin);
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnUpdateCategory.setOnClickListener(v -> handleUpdateCategory());
    }

    // Xóa lỗi cũ
    private void clearErrors() {
        layout.setError(null);
    }

    // Kiểm tra dữ liệu nhập
    private boolean validateInput(String categoryName) {
        if (categoryName.isEmpty()) {
            layout.setError("Vui lòng nhập tên");
            return false;
        }
        return true;
    }

    private void handleUpdateCategory() {
        clearErrors();
        String categoryName = etCategoryName.getText() != null ? etCategoryName.getText().toString().trim() : "";

        // Kiểm tra dữ liệu nhập
        if (!validateInput(categoryName)) return;

        // Nếu tên mới không đổi thì không cần update
        if (categoryName.equalsIgnoreCase(oldName)) {
            layout.setError("Tên không thay đổi");
            return;
        }

        // Kiểm tra trùng
        if (categoryDAO.categoryExists(categoryName)) {
            layout.setError("Danh mục này đã tồn tại");
            return;
        }

        // Update vào DB
        int result = categoryDAO.updateCategory(categoryId, categoryName);

        if (result > 0) {
            // Tạo Category mới để trả về fragment
            Category updatedCategory = new Category(categoryId, categoryName);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedCategory", updatedCategory); // Category implements Serializable/Parcelable
            setResult(RESULT_OK, resultIntent);
            finish();

        } else {
            Log.e("UpdateCategory", "Cập nhật thất bại: " + categoryName);
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }

    }

}