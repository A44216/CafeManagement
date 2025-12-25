package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
// Sửa import sai ở đây: thêm 's' vào adapters
import com.sinhviencafemanagement.adapters.admin.adapter.SettingAdminAdapter;
import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.command.DeleteFaceCommand;
import com.sinhviencafemanagement.command.FaceDeleteReceiver;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;
import com.sinhviencafemanagement.utils.FaceRecognitionHelper;

import java.util.Arrays;
import java.util.List;

public class SettingAdminFragment extends Fragment {

    private TextView tvEmail;
    private UserDAO userDAO;
    private int currentUserId;
    private FaceRecognitionHelper faceHelper;

    private boolean isDeleteMode = false;

    // Launcher để nhận kết quả từ FaceCaptureActivity
    private final ActivityResultLauncher<Intent> faceCaptureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String embeddingJson = result.getData().getStringExtra(FaceCaptureActivity.EXTRA_EMBEDDING);
                    
                    if (embeddingJson != null && currentUserId != -1) {
                         if (isDeleteMode) {
                            handleDeleteFace(embeddingJson);
                        } else {
                            handleRegisterFace(embeddingJson);
                        }
                    }
                }
                isDeleteMode = false;
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
        faceHelper = new FaceRecognitionHelper(requireContext());

        setupEmailHeader();

        RecyclerView rvSettingAdmin = view.findViewById(R.id.rcvSettingAdmin);
        rvSettingAdmin.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Thêm mục "Xóa Face ID" vào danh sách
        List<String> settingTitles = Arrays.asList(
                "Đồ uống đi kèm",
                "Đăng ký Face ID",
                "Xóa Face ID",
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
            case 0: // Topping
                startActivity(new Intent(requireContext(), ToppingActivity.class));
                break;
                
            case 1: // Đăng ký Face ID
                isDeleteMode = false;
                Intent intentRegister = new Intent(requireContext(), FaceCaptureActivity.class);
                faceCaptureLauncher.launch(intentRegister);
                break;
                
            case 2: // Xóa Face ID (Command Pattern)
                User user = userDAO.getUserById(currentUserId);
                if (user == null || user.getFaceEmbedding() == null) {
                    Toast.makeText(requireContext(), "Bạn chưa đăng ký Face ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Sử dụng Command Pattern
                FaceDeleteReceiver deleteReceiver = new FaceDeleteReceiver(requireContext(), faceCaptureLauncher, () -> {
                    isDeleteMode = true;
                });
                Command deleteFaceCommand = new DeleteFaceCommand(deleteReceiver);
                deleteFaceCommand.execute();
                break;
                
            case 3: // Đăng xuất
                confirmLogout();
                break;
        }
    }
    
    private void handleRegisterFace(String embeddingJson) {
        boolean success = userDAO.updateFaceEmbedding(currentUserId, embeddingJson);
        if (success) {
            Toast.makeText(requireContext(), "Đăng ký Face ID thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Lỗi khi lưu Face ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeleteFace(String capturedEmbeddingJson) {
        User user = userDAO.getUserById(currentUserId);
        if (user == null || user.getFaceEmbedding() == null) {
            Toast.makeText(requireContext(), "Dữ liệu người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        float[] currentVector = faceHelper.stringToEmbedding(capturedEmbeddingJson);
        float[] storedVector = faceHelper.stringToEmbedding(user.getFaceEmbedding());

        double score = faceHelper.calculateCosineSimilarity(currentVector, storedVector);
        
        if (score > 0.75) { // Ngưỡng khớp
            boolean success = userDAO.deleteFaceEmbedding(currentUserId);
            if (success) {
                Toast.makeText(requireContext(), "Đã xóa Face ID thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Lỗi khi xóa Face ID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Khuôn mặt không khớp! Không thể xóa.", Toast.LENGTH_LONG).show();
        }
    }

    // Đăng xuất

    private void confirmLogout() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {

                    // XÓA SESSION
                    requireContext()
                            .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .remove("user_id")
                            .remove("session_token") // Đảm bảo xóa token để tránh auto-login
                            .apply();

                    Intent intent =
                            new Intent(requireContext(), LoginActivity.class);
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
