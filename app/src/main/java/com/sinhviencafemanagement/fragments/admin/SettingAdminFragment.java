package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.FaceCaptureActivity;
import com.sinhviencafemanagement.activities.home.topping.ToppingActivity;
import com.sinhviencafemanagement.activities.login.LoginActivity;
import com.sinhviencafemanagement.adapters.admin.adapter.SettingAdminAdapter;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;

import java.util.Arrays;
import java.util.List;

public class SettingAdminFragment extends Fragment {

    private TextView tvEmail;
    private UserDAO userDAO;
    private int currentUserId;

    // Launcher để nhận kết quả từ FaceCaptureActivity
    private final ActivityResultLauncher<Intent> faceCaptureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String embeddingJson = result.getData().getStringExtra(FaceCaptureActivity.EXTRA_EMBEDDING);
                    if (embeddingJson != null && currentUserId != -1) {
                        boolean success = userDAO.updateFaceEmbedding(currentUserId, embeddingJson);
                        if (success) {
                            Toast.makeText(requireContext(), "Đăng ký Face ID thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Lỗi khi lưu Face ID", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

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

        // Thêm mục "Đăng ký Face ID" vào danh sách
        List<String> settingTitles = Arrays.asList(
                "Đồ uống đi kèm",
                "Đăng ký Face ID",
                "Đăng xuất"
        );

        SettingAdminAdapter adapter =
                new SettingAdminAdapter(settingTitles, this::handleItemClick);

        rvSettingAdmin.setAdapter(adapter);
    }

    // Email

    @SuppressLint("SetTextI18n")
    private void setupEmailHeader() {
        currentUserId = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        if (currentUserId == -1) {
            tvEmail.setText("Chưa đăng nhập");
            return;
        }

        User user = userDAO.getUserById(currentUserId);

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
            case 1: // Đăng ký Face ID
                Intent intent = new Intent(requireContext(), FaceCaptureActivity.class);
                faceCaptureLauncher.launch(intent);
                break;
            case 2: // Đăng xuất (index thay đổi do thêm item mới)
                confirmLogout();
                break;
        }
    }

    // Đăng xuất

    @SuppressLint("CommitPrefEdits")
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
                            .remove("session_token");
                    // XÓA SESSION
                    requireContext()
                            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .remove("user_id")
                            .remove("session_token") // Đảm bảo xóa token để tránh auto-login
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
