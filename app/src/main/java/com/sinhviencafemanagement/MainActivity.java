package com.sinhviencafemanagement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Session;
import com.sinhviencafemanagement.models.User;

import java.util.List;

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

        SessionDAO sessionDAO = new SessionDAO(this);
        Session session = sessionDAO.getSessionByUserId(4);
        Log.d("UserInfo", session.toString());

        sessionDAO.deleteSessionsByUserId(4);

    }


//    private void addDefaultAdmin(Context context) {
//        UserDAO userDAO = new UserDAO(context);
//
//        // Kiểm tra nếu admin chưa tồn tại
//        if (!userDAO.usernameExists("admin")) {
//            User admin = new User();
//            admin.setFullName("Administrator");
//            admin.setDisplayName("Admin");
//            admin.setUsername("admin123");
//            admin.setEmail("admin@example.com");
//            admin.setPhone("0123456789");
//            admin.setRoleId(CreateDatabase.ROLE_ADMIN);
//
//            // Hash password
//            String hashedPassword = userDAO.hashPassword("admin123");
//            if (hashedPassword == null) {
//                Log.e("MainActivity", "Hash mật khẩu thất bại, không thể tạo admin");
//                return;
//            }
//            admin.setPassword(hashedPassword);
//
//            long result = userDAO.addUser(admin);
//            if (result == -1) {
//                Log.e("MainActivity", "Tạo admin mặc định thất bại");
//            } else {
//                Log.i("MainActivity", "Admin mặc định đã được tạo, id=" + result);
//            }
//        }
//    }

}