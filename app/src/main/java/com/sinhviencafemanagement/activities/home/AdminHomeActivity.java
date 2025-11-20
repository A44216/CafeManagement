package com.sinhviencafemanagement.activities.home;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.fragments.admin.CategoryAdminFragment;

import java.util.Objects;

public class AdminHomeActivity extends AppCompatActivity {

    TextInputEditText etSearch;
    TextInputLayout layoutSearch;

    BottomNavigationView bnvMenu;

    FloatingActionButton fabAddNew;

    FragmentContainerView listFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(); // Khởi tạo các view

        setUpListeners(); // Set sự kiện

        // Gán fragment Category mặc định
        showDefaultCategoryFragment();

    }

    // Hàm riêng để hiển thị fragment Category mặc định
    private void showDefaultCategoryFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer) == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.listFragmentContainer, new CategoryAdminFragment())
            .commit();
        }
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        layoutSearch = findViewById(R.id.layoutSearch);
        bnvMenu = findViewById(R.id.bnvMenu);
        fabAddNew = findViewById(R.id.fabAddNew);
        listFragmentContainer = findViewById(R.id.listFragmentContainer);
    }

    private void setUpListeners() {
        fabAddNew.setOnClickListener(v -> handleAddNew());
    }

    private void handleAddNew() {
        showAddCategoryDialog();
    }

    // AlertDialog thông báo để nhập thông tin
    private void showAddCategoryDialog() {
        TextInputEditText input = new TextInputEditText(this);
        input.setHint("Nhập tên danh mục");

        new MaterialAlertDialogBuilder(this)
                .setTitle("Thêm danh mục mới")
                .setView(input)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name = Objects.requireNonNull(input.getText()).toString().trim();
                    if (!name.isEmpty())
                        saveCategory(name);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Lưu category và load lại danh sách
    private void saveCategory(String name) {
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryDAO.addCategory(name);

        // Refresh fragment
        CategoryAdminFragment categoryAdminFragment = (CategoryAdminFragment)
                getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer);
        if (categoryAdminFragment != null) {
            categoryAdminFragment.loadCategories();
        }
    }

}