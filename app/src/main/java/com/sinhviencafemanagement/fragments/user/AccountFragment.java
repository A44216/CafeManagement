// D:/CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/AccountFragment.java
package com.sinhviencafemanagement.fragments.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhviencafemanagement.R;
 import com.sinhviencafemanagement.activities.account.FeedbackActivity;
 import com.sinhviencafemanagement.activities.account.ContactActivity;
 import com.sinhviencafemanagement.activities.account.ChangePasswordActivity;
 import com.sinhviencafemanagement.activities.account.LogoutActivity;;

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Bắt đầu phần logic ---

        // 1. Ánh xạ các TextView từ layout
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView menuFeedback = view.findViewById(R.id.menuFeedback);
        TextView menuContact = view.findViewById(R.id.menuContact);
        TextView menuChangePass = view.findViewById(R.id.menuChangePass);
        TextView menuLogout = view.findViewById(R.id.menuLogout);

        // 2. Lấy dữ liệu người dùng (ví dụ) và hiển thị
        // (Bạn sẽ thay thế bằng logic lấy email thật của người dùng)
        tvEmail.setText("CatCafe2025@gmail.com");

        // 3. Gán sự kiện click cho từng mục menu

        // Nút Phản hồi
        menuFeedback.setOnClickListener(v -> {
             Intent intent = new Intent(getActivity(), FeedbackActivity.class);
             startActivity(intent);
        });

        // Nút Liên hệ
        menuContact.setOnClickListener(v -> {
//           Toast.makeText(getContext(), "Chức năng Liên hệ", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(getActivity(), ContactActivity.class);
             startActivity(intent);
        });

        // Nút Đổi mật khẩu
        menuChangePass.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Chức năng Đổi mật khẩu", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            // startActivity(intent);
        });

        // Nút Đăng xuất
        menuLogout.setOnClickListener(v -> {
            // Xử lý logic đăng xuất tại đây
            // Ví dụ: xóa thông tin đăng nhập đã lưu, sau đó chuyển về màn hình Login
            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Intent intent = new Intent(getActivity(), LoginActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa hết các Activity cũ
            // startActivity(intent);
        });
    }
}
