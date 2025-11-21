package com.sinhviencafemanagement;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.models.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Xóa database cũ nếu muốn reset (debug)
        deleteDatabase("CafeManagement");

        // Thêm các danh mục mặc định
        addDefaultCategories();

        // Thêm các product mặc định
        addDefaultProduct();

        addDefaultAdmin(this);
    }

    private void addDefaultCategories() {
        CategoryDAO categoryDAO = new CategoryDAO(this);
        categoryDAO.addCategory("Cà phê");
        categoryDAO.addCategory("Trà sữa");
        categoryDAO.addCategory("Sinh tố");
        categoryDAO.addCategory("Trà");
        categoryDAO.addCategory("Nước ép");
    }

    private void addDefaultProduct() {
        ProductDAO productDAO = new ProductDAO(this);

        // Cà phê
        productDAO.addProduct(new Product(
                "Cappuccino",
                25000,
                CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                R.drawable.cappuccino,
                1,
                "Cappuccino ngon"
        ));
        productDAO.addProduct(new Product(
                "Cà phê sữa",
                35000,
                CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                R.drawable.milk_coffee,
                1,
                "Cà phê sữa ngon"
        ));

        // Trà sữa
        productDAO.addProduct(new Product(
                "Matcha Latte",
                20000,
                CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                R.drawable.matcha_latte,
                2,
                "Trà xanh thanh mát"
        ));

        // Sinh tố
        productDAO.addProduct(new Product(
                "Sinh tố dâu",
                30000,
                CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                R.drawable.strawberry_smoothie,
                3,
                "Sinh tố dâu ngon"
        ));

        // Trà
        productDAO.addProduct(new Product(
                "Trà dâu tây",
                30000,
                CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                R.drawable.strawberry_tea,
                4,
                "Trà dâu tây ngon"
        ));

        for (int i = 0; i < 20; i++) {
            productDAO.addProduct(new Product(
                    "Trà dâu tây" + i,
                    30000,
                    CreateDatabase.PRODUCT_STATUS_AVAILABLE,
                    R.drawable.strawberry_tea,
                    4,
                    "Trà dâu tây ngon"
            ));

        }


    }

    private void addDefaultAdmin(Context context) {
        UserDAO userDAO = new UserDAO(context);

        if (!userDAO.usernameExists("admin123")) {
            User admin = new User();
            admin.setFullName("Administrator");
            admin.setDisplayName("Admin");
            admin.setUsername("admin123");
            admin.setEmail("admin@example.com");
            admin.setPhone("0123456789");
            admin.setRoleId(CreateDatabase.ROLE_ADMIN);

            // **Không hash mật khẩu ở đây**
            admin.setPassword("admin123");

            long result = userDAO.addUser(admin);
            if (result == -1) {
                Log.e("MainActivity", "Tạo admin mặc định thất bại");
            } else {
                Log.i("MainActivity", "Admin mặc định đã được tạo, id=" + result);
            }
        }
    }

}
