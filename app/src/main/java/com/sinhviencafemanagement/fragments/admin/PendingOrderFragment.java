package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.OrderAdminAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Order;

import java.util.ArrayList;
import java.util.List;

public class PendingOrderFragment extends Fragment {

    private OrderAdminAdapter adapter;
    private final List<Order> pendingOrders = new ArrayList<>();
    private OrderDAO orderDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pending_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserDAO userDAO = new UserDAO(requireContext());
        ProductDAO productDAO = new ProductDAO(requireContext());
        OrderDetailDAO orderDetailDAO = new OrderDetailDAO(requireContext());
        orderDAO = new OrderDAO(requireContext());

        RecyclerView rvOrders = view.findViewById(R.id.rvPendingOrder);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdminAdapter(pendingOrders, userDAO, productDAO, orderDetailDAO);
        rvOrders.setAdapter(adapter);

        adapter.setOnOrderActionListener(order -> {
            // TODO: mở màn hình chi tiết/tracking đơn hàng
        });

        loadPendingOrders();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPendingOrders() {
        new Thread(() -> {
            List<Order> orderList = orderDAO.getAllOrders();
            pendingOrders.clear();
            for (Order o : orderList) {
                if (CreateDatabase.ORDER_STATUS_PENDING.equals(o.getStatus())) {
                    pendingOrders.add(o);
                }
            }
            requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
        }).start();
    }
}
