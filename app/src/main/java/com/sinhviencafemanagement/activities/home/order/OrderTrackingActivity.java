package com.sinhviencafemanagement.activities.home.order;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.UserDAO;

public class OrderTrackingActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RadioGroup rgOrderStatus;
    private RadioButton rbPending, rbDone;

    private RecyclerView rvOrderItems;

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private ProductDAO productDAO;
    private UserDAO userDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_tracking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setListeners();

    }

    // Ánh xạ view
    public void initViews() {
        ivBack = findViewById(R.id.ivBack);
        rgOrderStatus = findViewById(R.id.rgOrderStatus);
        rbPending = findViewById(R.id.rbPending);
        rbDone = findViewById(R.id.rbDone);

        // Mặc định chọn bước đầu tiên
        rbPending.setChecked(true);
    }

    // Thiết lập sự kiện
    public void setListeners() {
        // Xử lý nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý chọn bước timeline
        rgOrderStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPending) {
                Toast.makeText(this, "Chọn: Chưa xử lý", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.rbDone) {
                Toast.makeText(this, "Chọn: Hoàn thành", Toast.LENGTH_SHORT).show();
            }
        });
    }

}