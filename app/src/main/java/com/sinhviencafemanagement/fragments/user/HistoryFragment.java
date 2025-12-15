package com.sinhviencafemanagement.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.sinhviencafemanagement.R;
// Giả sử bạn sẽ có một OrderAdapter
// import com.sinhviencafemanagement.adapters.OrderAdapter;
// import com.sinhviencafemanagement.models.Order;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private TabLayout tabLayoutHistory;
    private RecyclerView rcvOrderHistory;
    // Khai báo Adapter và danh sách dữ liệu
    // private OrderAdapter orderAdapter;
    // private List<Order> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" (inflate) layout fragment_history.xml
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- BƯỚC 1: ÁNH XẠ CÁC VIEW ---
        tabLayoutHistory = view.findViewById(R.id.tabLayoutHistory);
        rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);

        // --- BƯỚC 2: CÀI ĐẶT CHO RECYCLERVIEW ---
        // Khởi tạo danh sách và adapter
        // orderList = new ArrayList<>();
        // orderAdapter = new OrderAdapter(getContext(), orderList);

        // Thiết lập LayoutManager và gán Adapter
        rcvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        // rcvOrderHistory.setAdapter(orderAdapter);


        // --- BƯỚC 3: CÀI ĐẶT SỰ KIỆN CHO TABLAYOUT ---
        tabLayoutHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Khi một tab mới được chọn, lọc danh sách đơn hàng
                filterOrdersByStatus(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Không cần làm gì khi tab bị bỏ chọn
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Khi người dùng nhấn lại vào tab đang được chọn, cuộn lên đầu danh sách
                rcvOrderHistory.smoothScrollToPosition(0);
            }
        });

        // --- BƯỚC 4: TẢI DỮ LIỆU LẦN ĐẦU ---
        // Giả sử tab đầu tiên (vị trí 0) là "Tất cả" hoặc "Đang xử lý"
        filterOrdersByStatus(0);
    }

    /**
     * Phương thức để lọc và hiển thị danh sách đơn hàng dựa trên trạng thái (tab được chọn)
     * @param tabPosition Vị trí của tab (ví dụ: 0: Đang xử lý, 1: Đã giao, 2: Đã hủy)
     */
    private void filterOrdersByStatus(int tabPosition) {
        // TODO: Viết logic lấy dữ liệu từ SQLite dựa trên tabPosition
        // Ví dụ:
        //
        // orderList.clear(); // Xóa dữ liệu cũ
        // switch (tabPosition) {
        //     case 0:
        //         // Lấy tất cả đơn hàng đang xử lý từ DB
        //         orderList.addAll(orderDAO.getOrdersByStatus("processing"));
        //         break;
        //     case 1:
        //         // Lấy tất cả đơn hàng đã giao từ DB
        //         orderList.addAll(orderDAO.getOrdersByStatus("delivered"));
        //         break;
        //     case 2:
        //         // Lấy tất cả đơn hàng đã hủy từ DB
        //         orderList.addAll(orderDAO.getOrdersByStatus("cancelled"));
        //         break;
        // }
        //
        // // Thông báo cho adapter rằng dữ liệu đã thay đổi
        // orderAdapter.notifyDataSetChanged();

        // Hiển thị một Toast tạm thời để kiểm tra
        Toast.makeText(getContext(), "Đã chọn tab: " + tabPosition, Toast.LENGTH_SHORT).show();
    }
}
