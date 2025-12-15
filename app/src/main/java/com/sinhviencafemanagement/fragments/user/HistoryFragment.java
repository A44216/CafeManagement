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
import com.google.android.material.tabs.TabLayout;
import com.sinhviencafemanagement.R;

public class HistoryFragment extends Fragment { // Kế thừa từ lớp Fragment

    private TabLayout tabLayoutHistory;
    private RecyclerView rcvOrderHistory;

    // Phương thức này được gọi để tạo và trả về giao diện cho Fragment.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" (inflate) tệp layout XML của bạn thành một đối tượng View trong Java.
        // Giả sử tệp layout của bạn tên là "fragment_history.xml"
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayoutHistory = view.findViewById(R.id.tabLayoutHistory);
        rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);

        rcvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        // 1. Ánh xạ RecyclerView từ layout
        // Giả sử bạn có một RecyclerView với id là 'rcvOrderHistory' trong fragment_history.xml
        RecyclerView rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);

        tabLayoutHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
            }
        // 2. Thiết lập LayoutManager
        // LayoutManager quyết định cách các item được sắp xếp (dọc, ngang, lưới).
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvOrderHistory.setLayoutManager(linearLayoutManager);

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần làm gì nhiều khi tab bị bỏ chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rcvOrderHistory.scrollToPosition(0);
            }
        });
    }
}
