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

public class AddCategoryActivity extends AppCompatActivity {

    private TextInputLayout layout;
    private TextInputEditText etCategoryName;
    private ImageView ivBack;
    private MaterialButton btnAddCategory;

    CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        categoryDAO = new CategoryDAO(this);

        initViews(); // Khởi tạo view
        setUpListeners(); // Set sự kiện

    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnAddCategory.setOnClickListener(v -> handleAddCategory());

    }

    private void handleAddCategory() {
        clearErrors();
        String categoryName = etCategoryName.getText() != null ? etCategoryName.getText().toString().trim() : "";

        // Kiểm tra dữ liệu nhập
        if (!validateInput(categoryName)) return;

        // Kiểm tra tồn tại
        if (categoryDAO.categoryExists(categoryName)) {
            layout.setError("Danh mục này đã tồn tại");
            return;
        }

        // Thêm vào DB và lấy ID mới
        long id = categoryDAO.addCategory(categoryName); // giả sử trả về ID
        if (id > 0) {
            // Tạo Category mới
            Category newCategory = new Category((int) id, categoryName);

            // Trả kết quả về AdminHomeActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newCategory", newCategory); // Category implements Serializable
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Log.e("AddCategory", "Thêm thất bại: " + categoryName);
            Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
        }
    }


    private void initViews() {
        layout = findViewById(R.id.layoutCategoryNameAdmin);
        etCategoryName = findViewById(R.id.etCategoryNameAdmin);
        ivBack = findViewById(R.id.ivBack);
        btnAddCategory = findViewById(R.id.btnAddCategoryAdmin);
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

}