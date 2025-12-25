// D:/CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/AccountFragment.java
package com.sinhviencafemanagement.fragments.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.FaceCaptureActivity;
import com.sinhviencafemanagement.activities.account.FeedbackActivity;
import com.sinhviencafemanagement.activities.account.ContactActivity;
import com.sinhviencafemanagement.activities.account.ChangePasswordActivity;
import com.sinhviencafemanagement.activities.login.LoginActivity;
import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.command.DeleteFaceCommand;
import com.sinhviencafemanagement.command.FaceDeleteReceiver;
import com.sinhviencafemanagement.command.FaceRegisterReceiver;
import com.sinhviencafemanagement.command.RegisterFaceCommand;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;
import com.sinhviencafemanagement.utils.FaceRecognitionHelper;
;

public class AccountFragment extends Fragment {

    private UserDAO userDAO;
    private int currentUserId;
    private FaceRecognitionHelper faceHelper;

    // Biến trạng thái để phân biệt giữa Đăng ký và Xóa
    private boolean isDeleteMode = false;

    // Launcher nhận kết quả từ FaceCaptureActivity
    private final ActivityResultLauncher<Intent> faceCaptureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String capturedEmbeddingJson = result.getData().getStringExtra(FaceCaptureActivity.EXTRA_EMBEDDING);
                    
                    if (currentUserId == -1 || capturedEmbeddingJson == null) return;

                    if (isDeleteMode) {
                        // Logic XÓA: Phải xác thực khuôn mặt trước
                        handleDeleteFace(capturedEmbeddingJson);
                    } else {
                        // Logic ĐĂNG KÝ: Lưu trực tiếp
                        handleRegisterFace(capturedEmbeddingJson);
                    }
                }
                // Reset mode về mặc định (đăng ký) sau khi xử lý xong hoặc hủy
                isDeleteMode = false;
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userDAO = new UserDAO(requireContext());
        faceHelper = new FaceRecognitionHelper(requireContext());
        
        // Lấy ID người dùng hiện tại
        currentUserId = requireContext()
                .getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        // --- Bắt đầu phần logic ---

        // 1. Ánh xạ các TextView từ layout
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView menuFeedback = view.findViewById(R.id.menuFeedback);
        TextView menuContact = view.findViewById(R.id.menuContact);
        TextView menuChangePass = view.findViewById(R.id.menuChangePass);
        TextView menuLogout = view.findViewById(R.id.menuLogout);
        TextView menuFaceId = view.findViewById(R.id.menuFaceId);
        TextView menuDeleteFaceId = view.findViewById(R.id.menuDeleteFaceId);

        // 2. Hiển thị email người dùng
        if (currentUserId != -1) {
            User user = userDAO.getUserById(currentUserId);
            if (user != null) {
                tvEmail.setText(user.getEmail());
            }
        }

        // 3. Gán sự kiện click cho từng mục menu

        // Nút Phản hồi
        menuFeedback.setOnClickListener(v -> {
             Intent intent = new Intent(getActivity(), FeedbackActivity.class);
             startActivity(intent);
        });

        // Nút Liên hệ
        menuContact.setOnClickListener(v -> {
             Intent intent = new Intent(getActivity(), ContactActivity.class);
             startActivity(intent);
        });

        // Nút Đổi mật khẩu
        menuChangePass.setOnClickListener(v -> {
             Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
             startActivity(intent);
        });

        // Nút Đăng ký Face ID (Command Pattern)
        FaceRegisterReceiver registerReceiver = new FaceRegisterReceiver(requireContext(), faceCaptureLauncher);
        Command registerFaceCommand = new RegisterFaceCommand(registerReceiver);
        
        menuFaceId.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }
            // Đảm bảo không phải mode xóa
            isDeleteMode = false; 
            registerFaceCommand.execute();
        });

        // Nút Xóa Face ID (Command Pattern)
        FaceDeleteReceiver deleteReceiver = new FaceDeleteReceiver(requireContext(), faceCaptureLauncher, () -> {
            // Callback này sẽ được gọi trong execute() của Command
            isDeleteMode = true; 
        });
        Command deleteFaceCommand = new DeleteFaceCommand(deleteReceiver);

        menuDeleteFaceId.setOnClickListener(v -> {
            if (currentUserId == -1) {
                Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Kiểm tra xem user có FaceID để xóa không đã
            User user = userDAO.getUserById(currentUserId);
            if (user == null || user.getFaceEmbedding() == null) {
                Toast.makeText(getContext(), "Bạn chưa đăng ký Face ID", Toast.LENGTH_SHORT).show();
                return;
            }

            deleteFaceCommand.execute();
        });


        // Nút Đăng xuất
        menuLogout.setOnClickListener(v -> {
            // Hiển thị một thông báo cho người dùng
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // 1. Khởi tạo SessionDAO để xử lý dữ liệu
            // Cần getActivity() để lấy Context cho DAO
            if (getActivity() == null) return;
            SessionDAO sessionDAO = new SessionDAO(getActivity());

            // 2. Lấy session token hiện tại đã lưu
            String currentToken = sessionDAO.getSessionToken();

            // 3. Xóa session trong cơ sở dữ liệu và SharedPreferences
            // Phương thức `logoutUser(token)` sẽ xóa cả trong DB và prefs
            if (currentToken != null && !currentToken.isEmpty()) {
                sessionDAO.logoutUser(currentToken);
            }

            // 4. Tạo Intent để quay về màn hình Đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);

            // 5. Xóa tất cả các Activity cũ khỏi hàng đợi (back stack)
            // Điều này đảm bảo người dùng không thể nhấn nút "Back" để quay lại màn hình chính sau khi đã đăng xuất
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // 6. Thực hiện chuyển màn hình
            startActivity(intent);
        });
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
}
