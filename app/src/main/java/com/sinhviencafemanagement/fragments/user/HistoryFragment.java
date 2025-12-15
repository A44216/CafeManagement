// CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/HistoryFragment

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

public class HistoryFragment extends Fragment {

    private TabLayout tabLayoutHistory;
    private RecyclerView rcvOrderHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayoutHistory = view.findViewById(R.id.tabLayoutHistory);
        rcvOrderHistory = view.findViewById(R.id.rcvOrderHistory);

        rcvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        tabLayoutHistory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
            }

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
