package com.sinhviencafemanagement.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.sinhviencafemanagement.activities.home.category.AddCategoryActivity;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.fragments.admin.CategoryAdminFragment;
import com.sinhviencafemanagement.models.Category;

import java.util.Objects;

public class AdminHomeActivity extends AppCompatActivity {

    TextInputEditText etSearch;
    TextInputLayout layoutSearch;

    BottomNavigationView bnvMenu;

    FloatingActionButton fabAddNew;

    FragmentContainerView listFragmentContainer;

    public final ActivityResultLauncher<Intent> categoryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    CategoryAdminFragment fragment = (CategoryAdminFragment)
                            getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer);
                    if (fragment != null) {
                        // Nếu nhận được category mới
                        Category newCategory = (Category) result.getData().getSerializableExtra("newCategory");
                        if (newCategory != null) {
                            fragment.addCategory(newCategory); // Thêm item mới vào RecyclerView
                            return;
                        }
                        // Nếu nhận được category đã update
                        Category updatedCategory = (Category) result.getData().getSerializableExtra("updatedCategory");
                        if (updatedCategory != null) {
                            fragment.updateCategory(updatedCategory); // Cập nhật trực tiếp item
                        }
                    }
                }
            });


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

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        layoutSearch = findViewById(R.id.layoutSearch);
        bnvMenu = findViewById(R.id.bnvMenu);
        fabAddNew = findViewById(R.id.fabAddNew);
        listFragmentContainer = findViewById(R.id.listFragmentContainer);
    }

    private void setUpListeners() {
        fabAddNew.setOnClickListener(v -> handleAddCategory());
    }


    // Hàm riêng để hiển thị fragment Category mặc định
    private void showDefaultCategoryFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer) == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.listFragmentContainer, new CategoryAdminFragment())
                .commit();
        }
    }

    private void handleAddCategory() {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        categoryLauncher.launch(intent);
    }

}