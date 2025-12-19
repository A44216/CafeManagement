package com.sinhviencafemanagement.fragments.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapters.admin.adapter.OrderPagerAdapter;

public class OrderAdminFragment extends Fragment {

    public OrderAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy TabLayout và ViewPager2 từ layout XML
        TabLayout tabOrder = view.findViewById(R.id.tabOrder);
        ViewPager2 viewPagerOrder = view.findViewById(R.id.viewPagerOrder);

        // Gắn adapter ViewPager
        OrderPagerAdapter adapter = new OrderPagerAdapter(requireActivity());
        viewPagerOrder.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabOrder, viewPagerOrder, (tab, position) -> {
            if (position == 0) {
                tab.setText("Đang xử lý");
                tab.setContentDescription("Pending");
            } else {
                tab.setText("Hoàn thành");
                tab.setContentDescription("Completed");
            }
        }).attach();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_admin, container, false);
    }
}