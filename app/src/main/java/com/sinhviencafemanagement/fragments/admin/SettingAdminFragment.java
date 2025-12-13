package com.sinhviencafemanagement.fragments.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.topping.ToppingActivity;
import com.sinhviencafemanagement.activities.login.LoginActivity;
import com.sinhviencafemanagement.adapter.admin.adapter.SettingAdminAdapter;

import java.util.Arrays;
import java.util.List;

public class SettingAdminFragment extends Fragment {

    public SettingAdminFragment() { }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_setting_admin, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvSettingAdmin = view.findViewById(R.id.rcvSettingAdmin);
        rvSettingAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        // Danh sách setting (KHÔNG CSDL)
        List<String> settingTitles = Arrays.asList(
                "Đồ uống đi kèm",
                "Đăng xuất"
        );

        SettingAdminAdapter adapter = new SettingAdminAdapter(settingTitles, this::handleItemClick);
        rvSettingAdmin.setAdapter(adapter);
    }

    private void handleItemClick(int position) {
        switch (position) {
            case 0:
                Intent it = new Intent(requireContext(), ToppingActivity.class);
                startActivity(it);
                break;

            case 1:
                // Đăng xuất
                confirmLogout();
                break;
        }
    }

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
