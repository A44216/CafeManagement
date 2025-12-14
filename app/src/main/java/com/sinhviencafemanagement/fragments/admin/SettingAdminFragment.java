package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.topping.ToppingActivity;
import com.sinhviencafemanagement.activities.login.LoginActivity;
import com.sinhviencafemanagement.adapter.admin.adapter.SettingAdminAdapter;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;

import java.util.Arrays;
import java.util.List;

public class SettingAdminFragment extends Fragment {

    private TextView tvEmail;
    private UserDAO userDAO;

    public SettingAdminFragment() {
        // Required empty public constructor
    }

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

        tvEmail = view.findViewById(R.id.tvEmail);
        userDAO = new UserDAO(requireContext());

        setupEmailHeader();

        RecyclerView rvSettingAdmin = view.findViewById(R.id.rcvSettingAdmin);
        rvSettingAdmin.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<String> settingTitles = Arrays.asList(
                "Đồ uống đi kèm",
                "Đăng xuất"
        );

        SettingAdminAdapter adapter =
                new SettingAdminAdapter(settingTitles, this::handleItemClick);

        rvSettingAdmin.setAdapter(adapter);
    }

    // Email

    @SuppressLint("SetTextI18n")
    private void setupEmailHeader() {
        int userId = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        if (userId == -1) {
            tvEmail.setText("Chưa đăng nhập");
            return;
        }

        User user = userDAO.getUserById(userId);

        if (user == null) {
            tvEmail.setText("Chưa đăng nhập");
            return;
        }

        tvEmail.setText(user.getEmail());
    }

    // Click

    private void handleItemClick(int position) {
        switch (position) {
            case 0:
                startActivity(new Intent(requireContext(), ToppingActivity.class));
                break;

            case 1:
                confirmLogout();
                break;
        }
    }

    // Đăng xuất
    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {

                    SharedPreferences prefs =
                            requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

                    String token = prefs.getString("session_token", null);
                    int userId = prefs.getInt("user_id", -1);

                    // XÓA SESSION TRONG DB
                    SessionDAO sessionDAO = new SessionDAO(requireContext());

                    if (token != null) {
                        sessionDAO.deleteSession(token);
                    }

                    if (userId != -1) {
                        sessionDAO.deleteSessionsByUserId(userId);
                    }

                    // XÓA SHAREDPREFERENCES
                    prefs.edit()
                            .remove("session_token")
                            .remove("user_id")
                            .apply();

                    // QUAY VỀ LOGIN
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
