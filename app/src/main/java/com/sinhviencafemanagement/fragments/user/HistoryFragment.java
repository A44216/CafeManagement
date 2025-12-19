package com.sinhviencafemanagement.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.customer.OrderHistoryAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.utils.SessionManager;

import java.util.List;

public class HistoryFragment extends Fragment {

    private TabLayout tabLayoutHistory;
    private RecyclerView rcvOrderHistory;
    private OrderDAO orderDAO;
    private SessionManager sessionManager;
    private OrderHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo View và Data
        tabLayoutHistory = view.findViewById(R.id.tabLayoutHistory);
        rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);
        orderDAO = new OrderDAO(getContext());
        sessionManager = new SessionManager(getContext());

        rcvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mặc định load đơn hàng đang xử lý (pending) khi mở màn hình
        loadOrdersByStatus(CreateDatabase.ORDER_STATUS_PENDING);

        tabLayoutHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadOrdersByStatus(CreateDatabase.ORDER_STATUS_PENDING);
                } else {
                    loadOrdersByStatus(CreateDatabase.ORDER_STATUS_COMPLETED);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rcvOrderHistory.scrollToPosition(0);
            }
        });
    }

    private void loadOrdersByStatus(String status) {
        int userId = sessionManager.getUserId();
        // Lấy danh sách đơn hàng từ Database theo UserId và Status
        List<Order> orderList = orderDAO.getOrdersByUserIdAndStatus(userId, status);

        adapter = new OrderHistoryAdapter(getContext(), orderList);
        rcvOrderHistory.setAdapter(adapter);
    }
}