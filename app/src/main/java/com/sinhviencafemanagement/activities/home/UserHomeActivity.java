package com.sinhviencafemanagement.activities.home;// UserHomeActivity.java

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.fragments.user.HomeFragment;
import com.sinhviencafemanagement.fragments.user.HistoryFragment;
import com.sinhviencafemanagement.fragments.user.AccountFragment;

public class UserHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        bottomNavigationView = findViewById(R.id.bnvCustomerMenu);

        // 1. Thiết lập màn hình mặc định khi mở ứng dụng
        if (savedInstanceState == null) {
            // Mở HomeFragment làm màn hình đầu tiên
            loadFragment(new HomeFragment());
        }

        // 2. Lắng nghe sự kiện khi người dùng chọn một mục trên menu
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) { // Thay ID cho đúng
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_order) { // Thay ID cho đúng
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.nav_account) { // Thay ID cho đúng
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true; // Trả về true để đánh dấu mục đã được chọn
            }

            return false;
        });
    }

    // 3. Phương thức để thay thế Fragment trong FragmentContainerView
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }
}
