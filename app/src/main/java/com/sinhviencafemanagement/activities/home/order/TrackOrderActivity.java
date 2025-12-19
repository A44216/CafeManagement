package com.sinhviencafemanagement.activities.home.order;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.customer.OrderReceiptAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.database.CreateDatabase; // Import lớp database để lấy hằng số status
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;

import java.util.List;

public class TrackOrderActivity extends AppCompatActivity {

    private ImageView imgBack;
    private Button btnReceiveOrder;
    private CardView cvStep1, cvStep2, cvStep3;
    private View line1, line2;
    private TextView tvStep1, tvStep2, tvStep3;
    private RecyclerView rcvTrackProducts;

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        initView();
        initData();

        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderData();
            displayProducts();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }

        imgBack.setOnClickListener(v -> finish());

        btnReceiveOrder.setOnClickListener(v -> {
            Toast.makeText(this, "Xác nhận đã nhận món thành công!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        btnReceiveOrder = findViewById(R.id.btnReceiveOrder);
        cvStep1 = findViewById(R.id.cvStep1);
        cvStep2 = findViewById(R.id.cvStep2);
        cvStep3 = findViewById(R.id.cvStep3);
        line1 = findViewById(R.id.line1);
        line2 = findViewById(R.id.line2);
        tvStep1 = findViewById(R.id.tvStep1);
        tvStep2 = findViewById(R.id.tvStep2);
        tvStep3 = findViewById(R.id.tvStep3);
        rcvTrackProducts = findViewById(R.id.rcvTrackProducts);
    }

    private void initData() {
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);
    }

    private void loadOrderData() {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            // Lấy status kiểu String từ model Order
            updateStepper(order.getStatus());
        }
    }

    private void displayProducts() {
        List<OrderDetail> details = orderDetailDAO.getDetailsByOrderId(orderId);
        OrderReceiptAdapter adapter = new OrderReceiptAdapter(this, details);
        rcvTrackProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvTrackProducts.setAdapter(adapter);
        rcvTrackProducts.setNestedScrollingEnabled(false);
    }

    private void updateStepper(String status) {
        int colorActive = Color.parseColor("#4CAF50");
        int colorInactive = Color.parseColor("#E0E0E0");
        int textActive = Color.parseColor("#5D4037");
        int textInactive = Color.parseColor("#BDBDBD");

        // Bước 1: Luôn active vì đơn đã được tạo (mặc định là pending)
        cvStep1.setCardBackgroundColor(colorActive);
        tvStep1.setTextColor(textActive);

        // Bước 2: Chuẩn bị đơn hàng
        // Trong database hiện tại chỉ có pending và completed
        // Ở đây giả định nếu đơn đã hoàn thành (completed) thì các bước trước đó phải hoàn tất
        if (CreateDatabase.ORDER_STATUS_COMPLETED.equals(status)) {
            // Hiển thị bước 2 là hoàn tất
            cvStep2.setCardBackgroundColor(colorActive);
            line1.setBackgroundColor(colorActive);
            tvStep2.setTextColor(textActive);

            // Hiển thị bước 3 là hoàn tất và bật nút nhận hàng
            cvStep3.setCardBackgroundColor(colorActive);
            line2.setBackgroundColor(colorActive);
            tvStep3.setTextColor(textActive);

            btnReceiveOrder.setEnabled(true);
            btnReceiveOrder.setBackgroundTintList(ColorStateList.valueOf(textActive));
        } else {
            // Nếu vẫn đang ở trạng thái pending
            cvStep2.setCardBackgroundColor(colorInactive);
            line1.setBackgroundColor(colorInactive);
            tvStep2.setTextColor(textInactive);

            cvStep3.setCardBackgroundColor(colorInactive);
            line2.setBackgroundColor(colorInactive);
            tvStep3.setTextColor(textInactive);

            btnReceiveOrder.setEnabled(false);
            btnReceiveOrder.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
        }
    }
}