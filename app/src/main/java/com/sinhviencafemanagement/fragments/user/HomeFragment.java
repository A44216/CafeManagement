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
        View view = inflater.inflate(R.layout.activity_customer_home, container, false);

        // Trả về View đã được tạo để hệ thống có thể hiển thị nó.
        return view;
    }

    // Bước 3: Ghi đè phương thức onViewCreated (Tùy chọn nhưng rất hữu ích)
    // Phương thức này được gọi ngay sau khi onCreateView hoàn tất.
    // Đây là nơi tốt nhất để thực hiện các thao tác trên giao diện như ánh xạ View và cài đặt sự kiện.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ví dụ: Ánh xạ các View từ layout fragment_home.xml
        // TextView welcomeText = view.findViewById(R.id.welcome_text);
        // RecyclerView productList = view.findViewById(R.id.product_recycler_view);

        // Tại đây bạn sẽ viết code để xử lý logic cho màn hình Home:
        // - Lấy dữ liệu sản phẩm từ cơ sở dữ liệu hoặc API.
        // - Hiển thị dữ liệu lên RecyclerView.
        // - Cài đặt sự kiện click cho các nút...
    }
}
