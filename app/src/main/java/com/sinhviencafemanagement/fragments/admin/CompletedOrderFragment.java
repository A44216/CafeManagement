package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.order.OrderTrackingActivity;
import com.sinhviencafemanagement.adapters.admin.adapter.OrderAdminAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Order;

import java.util.ArrayList;
import java.util.List;

public class CompletedOrderFragment extends Fragment {

    private OrderAdminAdapter adapter;
    private final List<Order> completedOrders = new ArrayList<>();
    private OrderDAO orderDAO;

    private final ActivityResultLauncher<Intent> orderTrackingLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    // Reload danh sách khi trạng thái đơn hàng thay đổi
                    loadCompletedOrders();
                }
            }
    );

    @Override
    public void onResume() {
        super.onResume();
        // Khi fragment hiển thị lại, reload danh sách từ DB
        loadCompletedOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_completed_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserDAO userDAO = new UserDAO(requireContext());
        ProductDAO productDAO = new ProductDAO(requireContext());
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO(requireContext());
        orderDAO = new OrderDAO(requireContext());

        RecyclerView rvOrders = view.findViewById(R.id.rvCompletedOrder);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdminAdapter(completedOrders, userDAO, productDAO, orderDetailDAO);
        rvOrders.setAdapter(adapter);

        adapter.setOnOrderActionListener(order -> {
            // Mở chi tiết đơn hàng và nhận kết quả khi trạng thái thay đổi
            Intent intent = new Intent(requireContext(), OrderTrackingActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            orderTrackingLauncher.launch(intent);
        });

        loadCompletedOrders();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCompletedOrders() {
        new Thread(() -> {
            List<Order> orderList = orderDAO.getAllOrders();
            completedOrders.clear();
            for (Order o : orderList) {
                if (CreateDatabase.ORDER_STATUS_COMPLETED.equals(o.getStatus())) {
                    completedOrders.add(o);
                }
            }
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }
}
