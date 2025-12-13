package com.sinhviencafemanagement.fragments.user;// D:/CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/HistoryFragment.javapackage com.sinhviencafemanagement.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R; // Đảm bảo import đúng tệp R

public class HistoryFragment extends Fragment { // Kế thừa từ lớp Fragment

    // Phương thức này được gọi để tạo và trả về giao diện cho Fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" (inflate) tệp layout XML của bạn thành một đối tượng View trong Java.
        // Giả sử tệp layout của bạn tên là "fragment_history.xml"
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    // Phương thức này được gọi ngay sau khi onCreateView hoàn tất.
    // Đây là nơi tốt nhất để thực hiện các thao tác trên giao diện.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Bắt đầu phần logic cho màn hình Lịch sử ---

        // 1. Ánh xạ RecyclerView từ layout
        // Giả sử bạn có một RecyclerView với id là 'rcvOrderHistory' trong fragment_history.xml
        RecyclerView rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);

        // 2. Thiết lập LayoutManager
        // LayoutManager quyết định cách các item được sắp xếp (dọc, ngang, lưới).
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvOrderHistory.setLayoutManager(linearLayoutManager);

        // 3. Tạo Adapter
        // Adapter là nơi bạn cung cấp dữ liệu (ví dụ: danh sách các đơn hàng)
        // và liên kết dữ liệu đó với layout của từng item (item_history_order.xml).
        // (Bạn sẽ cần tạo một lớp HistoryAdapter riêng)
        // HistoryAdapter historyAdapter = new HistoryAdapter(danh_sach_don_hang);

        // 4. Set Adapter cho RecyclerView
        // rcvOrderHistory.setAdapter(historyAdapter);

        // Tại đây, bạn sẽ viết code để:
        // - Lấy danh sách các đơn hàng đã đặt của người dùng từ cơ sở dữ liệu.
        // - Khởi tạo adapter với danh sách đó và gán cho RecyclerView.
    }
}
