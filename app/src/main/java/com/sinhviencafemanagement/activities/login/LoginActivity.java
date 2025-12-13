package com.sinhviencafemanagement.activities.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.FaceCaptureActivity;
import com.sinhviencafemanagement.activities.home.AdminHomeActivity;
import com.sinhviencafemanagement.activities.home.UserHomeActivity;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Session;
import com.sinhviencafemanagement.models.User;
import com.sinhviencafemanagement.utils.FaceRecognitionHelper;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // EditText cho tên đăng nhập và mật khẩu
    private TextInputEditText etUsernameOrEmail, etPassword;
    // Layout để hiển thị lỗi
    private TextInputLayout layoutUsernameOrEmail, layoutPassword;
    // Nút đăng nhập
    private MaterialButton btnLogin, btnFaceId;

    // DAO thao tác với database người dùng
    private UserDAO userDAO;
    private SessionDAO sessionDAO;

    private CheckBox chkRememberLogin;

    private SharedPreferences prefs; // SharedPreferences chung
    private FaceRecognitionHelper faceHelper;

    // Launcher cho Face Login
    private final ActivityResultLauncher<Intent> faceLoginLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String embeddingJson = result.getData().getStringExtra(FaceCaptureActivity.EXTRA_EMBEDDING);
                    handleFaceLogin(embeddingJson);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE); // khởi tạo 1 lần

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo DAO
        userDAO = new UserDAO(this);
        sessionDAO = new SessionDAO(this);
        faceHelper = new FaceRecognitionHelper(this);

        initViews(); // Ánh xạ các View từ layout
        setUpListeners(); // Thiết lập các listener cho Button và EditText

        // Load username đã lưu vào EditText
        String savedUsername = prefs.getString("saved_username", "");
        if (!savedUsername.isEmpty()) {
            etUsernameOrEmail.setText(savedUsername);
        }

        // Đăng nhập tự động nếu đã lưu token
        if (checkAutoLogin()) return;

    }

    // Ánh xạ View
    private void initViews() {
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etPassword = findViewById(R.id.etPassword);
        layoutUsernameOrEmail = findViewById(R.id.layoutUsernameOrEmail);
        layoutPassword = findViewById(R.id.layoutPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnFaceId = findViewById(R.id.btnFaceId);
        chkRememberLogin = findViewById(R.id.chkRememberLogin);
    }

    // Thiết lập listener cho các sự kiện
    private void setUpListeners() {
        // Xóa lỗi khi mất focus
        etUsernameOrEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim().isEmpty()) {
                layoutUsernameOrEmail.setError(null);
            }
        });

        etPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !Objects.requireNonNull(etPassword.getText()).toString().trim().isEmpty()) {
                layoutPassword.setError(null);
            }
        });

        // Chuyển sang màn hình đăng ký khi click TextView
        findViewById(R.id.tvRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // Chuyển sang màn hình quên mật khẩu khi click TextView
        findViewById(R.id.tvForgotPassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        // Xử lý đăng nhập khi click nút
        btnLogin.setOnClickListener(v -> handleLogin());

        // Xử lý Face ID
        btnFaceId.setOnClickListener(v -> {
            if (userDAO.hasAnyFaceEmbedding()) {
                Intent intent = new Intent(this, FaceCaptureActivity.class);
                faceLoginLauncher.launch(intent);
            } else {
                showToast("Chưa đăng ký FaceID");
            }
        });
    }

    // Xử lý đăng nhập bằng khuôn mặt
    private void handleFaceLogin(String embeddingJson) {
        if (embeddingJson == null) return;

        float[] currentEmbedding = faceHelper.stringToEmbedding(embeddingJson);
        if (currentEmbedding == null) return;

        // Lấy danh sách tất cả người dùng
        List<User> allUsers = userDAO.getAllUsers();
        User matchedUser = null;
        double maxScore = 0.0;

        for (User user : allUsers) {
            String storedEmbeddingJson = user.getFaceEmbedding();
            if (storedEmbeddingJson != null && !storedEmbeddingJson.isEmpty()) {
                float[] storedEmbedding = faceHelper.stringToEmbedding(storedEmbeddingJson);
                double score = faceHelper.calculateCosineSimilarity(currentEmbedding, storedEmbedding);
                
                // Ngưỡng nhận diện
                if (score > 0.7 && score > maxScore) {
                    maxScore = score;
                    matchedUser = user;
                }
            }
        }

        if (matchedUser != null) {
            loginSuccess(matchedUser);
            showToast("Xin chào " + matchedUser.getFullName());
        } else {
            showToast("Không nhận diện được khuôn mặt");
        }
    }

    // Xử lý đăng nhập thường
    private void handleLogin() {
        clearErrors();

        String input = (etUsernameOrEmail.getText() != null) ? etUsernameOrEmail.getText().toString().trim().toLowerCase() : "";
        String password = (etPassword.getText() != null) ? etPassword.getText().toString().trim() : "";

        if (!validateInput(input, password)) return;

        // Kiểm tra đăng nhập
        if (userDAO.checkLogin(input, password)) {
            User user = userDAO.getUserByUsernameOrEmail(input);
            if (user == null) {
                showToast("Không tìm thấy người dùng tương ứng");
                return;
            }
            loginSuccess(user);
        } else {
            showLoginError();
        }
    }

    private void loginSuccess(User user) {
        SharedPreferences.Editor editor = prefs.edit();

        // Luôn lưu user_id
        editor.putInt("user_id", user.getUserId());

        // Tạo session
        long expiredAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000; // 7 ngày
        String token = sessionDAO.createSession(user.getUserId(), expiredAt);

        if (token != null) {
            editor.putString("session_token", token);
        }

        // Luôn lưu username để điền lại EditText lần sau
        editor.putString("saved_username", user.getUsername());
        editor.apply();

        // Phân quyền truy cập
        Intent intent;
        if (user.getRoleId() == CreateDatabase.ROLE_ADMIN) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else {
            intent = new Intent(this, UserHomeActivity.class);
        }

        startActivity(intent);
        finish();
    }

    // Xóa lỗi cũ
    private void clearErrors() {
        layoutUsernameOrEmail.setError(null);
        layoutPassword.setError(null);
    }

    // Kiểm tra dữ liệu nhập
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            layoutUsernameOrEmail.setError("Vui lòng nhập tên tài khoản hoặc Email");
            return false;
        }
        if (password.isEmpty()) {
            layoutPassword.setError("Vui lòng nhập mật khẩu");
            return false;
        }
        return true;
    }

    // Hiển thị lỗi đăng nhập chung
    private void showLoginError() {
        layoutPassword.setError("Tên tài khoản hoặc mật khẩu không đúng");
    }

    // Kiểm tra tự động đăng nhập
    private boolean checkAutoLogin() {
        String savedToken = prefs.getString("session_token", null);

        if (savedToken != null) {
            Session session = sessionDAO.getSessionByToken(savedToken);
            if (session != null) {
                User user = userDAO.getUserById(session.getUserId());
                if (user != null) {
                    Intent intent = (user.getRoleId() == CreateDatabase.ROLE_ADMIN)
                            ? new Intent(this, AdminHomeActivity.class)
                            : new Intent(this, UserHomeActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
            } else {
                prefs.edit().remove("session_token").apply();
            }
        }
        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
