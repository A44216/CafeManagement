// D:/CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/HomeFragment.java

package com.sinhviencafemanagement.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhviencafemanagement.R; // Đảm bảo import đúng R của project

public class HomeFragment extends Fragment { // Bước 1: Kế thừa từ lớp Fragment

    // Bước 2: Ghi đè phương thức onCreateView
    // Đây là phương thức quan trọng nhất, nó được gọi để tạo và trả về giao diện cho Fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" (inflate) tệp layout XML của bạn thành một đối tượng View trong Java.
        // Tệp layout này sẽ chứa giao diện của màn hình Trang chủ (ví dụ: thanh tìm kiếm, RecyclerView...).
        // Giả sử tệp layout của bạn tên là "fragment_home.xml"
        View view = inflater.inflate(R.layout.fragment_home_customer, container, false);

        // Trả về View đã được tạo để hệ thống có thể hiển thị nó.
        return view;
    }

    // Bước 3: Ghi đè phương thức onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // === BẮT ĐẦU PHẦN XỬ LÝ LOGIC ===

        // 1. Import các lớp cần thiết ở đầu file
        // import android.content.Intent;
        // import android.widget.ImageView;
        // import com.sinhviencafemanagement.activities.cart.CartActivity;

        // 2. Ánh xạ ImageView của nút giỏ hàng từ layout
        android.widget.ImageView ivCart = view.findViewById(com.sinhviencafemanagement.R.id.ivCart);

        // 3. Cài đặt sự kiện click cho nút giỏ hàng
        ivCart.setOnClickListener(v -> {
            // Tạo một Intent để mở CartActivity
            android.content.Intent intent = new android.content.Intent(getActivity(), com.sinhviencafemanagement.activities.cart.CartActivity.class);

            // Thực hiện chuyển màn hình
            startActivity(intent);
        });

        // Tại đây bạn sẽ tiếp tục viết code cho các chức năng khác của màn hình Home:
        // - Ánh xạ RecyclerView cho danh mục và sản phẩm.
        // - Lấy dữ liệu và gán vào Adapter...
    }

}
