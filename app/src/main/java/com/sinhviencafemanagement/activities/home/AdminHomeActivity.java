package com.sinhviencafemanagement.activities.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.category.AddCategoryActivity;
import com.sinhviencafemanagement.activities.home.product.AddProductActivity;
import com.sinhviencafemanagement.fragments.admin.CategoryAdminFragment;
import com.sinhviencafemanagement.fragments.admin.ProductAdminFragment;
import com.sinhviencafemanagement.models.Category;
import com.sinhviencafemanagement.models.Product;

public class AdminHomeActivity extends AppCompatActivity {

    TextInputEditText etSearch;
    TextInputLayout layoutSearch;

    BottomNavigationView bnvMenu;

    FloatingActionButton fabAddNew;

    FragmentContainerView listFragmentContainer;

    // Launcher cho Add/Update Category
    public final ActivityResultLauncher<Intent> categoryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    CategoryAdminFragment fragment = (CategoryAdminFragment)
                            getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer);
                    if (fragment != null) {
                        // Lấy category mới
                        Category newCategory = (Category) result.getData().getSerializableExtra("newCategory");
                        if (newCategory != null) {
                            fragment.addCategoryAdmin(newCategory); // cập nhật RecyclerView
                            return;
                        }
                        // Lấy category đã cập nhật
                        Category updatedCategory = (Category) result.getData().getSerializableExtra("updatedCategory");
                        if (updatedCategory != null) {
                            fragment.updateCategoryAdmin(updatedCategory); // cập nhật RecyclerView
                        }
                    }
                }
            });


    // Launcher cho Add/Update Product
    public final ActivityResultLauncher<Intent> productLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ProductAdminFragment fragment = (ProductAdminFragment)
                            getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer);
                    if (fragment != null) {
                        Product newProduct = (Product) result.getData().getSerializableExtra("newProduct");
                        if (newProduct != null) {
                            fragment.addProductAdmin(newProduct);
                            return;
                        }
                        Product updatedProduct = (Product) result.getData().getSerializableExtra("updatedProduct");
                        if (updatedProduct != null) {
                            fragment.updateProductAdmin(updatedProduct);
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

        // Chọn Category làm tab mặc định
        bnvMenu.setSelectedItemId(R.id.menu_category);

        // Gán fragment Category mặc định
        showDefaultCategoryFragment();

        setUpListeners(); // Set sự kiện

    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        layoutSearch = findViewById(R.id.layoutSearch);
        bnvMenu = findViewById(R.id.bnvMenu);
        fabAddNew = findViewById(R.id.fabAddNew);
        listFragmentContainer = findViewById(R.id.listFragmentContainer);
    }

    private void setUpListeners() {
        fabAddNew.setOnClickListener(v -> {
            int selectedId = bnvMenu.getSelectedItemId(); // Lấy tab đang chọn

            if (selectedId == R.id.menu_category) {
                handleAddCategoryAdmin(); // Thêm category
            } else if (selectedId == R.id.menu_product) {
                handleAddProductAdmin(); // Thêm product
            }
        });

        bnvMenu.setOnItemSelectedListener(item -> {
            int selectedId = item.getItemId();

            if (selectedId == R.id.menu_category) {
                switchToCategoryFragment();
            }
            else if (selectedId == R.id.menu_product) {
                switchToProductFragment();
            }
//            else if (selectedId == R.id.menu_order) {
//                switchToOrderFragment();
//            }
//            else if (selectedId == R.id.menu_setting) {
//                switchToSettingFragment();
//            }

            return true;

        });
    }

    // Chuyển sang Category Fragment
    private void switchToCategoryFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.listFragmentContainer, new CategoryAdminFragment())
                .commit();
    }

    // Chuyển sang Product Fragment
    private void switchToProductFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.listFragmentContainer, new ProductAdminFragment())
                .commit();
    }

//    // Chuyển sang Order (tạo fragment rỗng trước cũng được)
//    private void switchToOrderFragment() {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.listFragmentContainer, new OrderAdminFragment())
//                .commit();
//    }
//
//    // Chuyển sang Setting
//    private void switchToSettingFragment() {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.listFragmentContainer, new SettingAdminFragment())
//                .commit();
//    }


    // Hiển thị Category Fragment mặc định
    private void showDefaultCategoryFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.listFragmentContainer) == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.listFragmentContainer, new CategoryAdminFragment())
                    .commitAllowingStateLoss();
        }
    }

    private void handleAddCategoryAdmin() {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        categoryLauncher.launch(intent);
    }

    private void handleAddProductAdmin() {
        Intent intent = new Intent(this, AddProductActivity.class);
        productLauncher.launch(intent);
    }

}