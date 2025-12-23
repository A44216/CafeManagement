package com.sinhviencafemanagement.activities.home.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.UserHomeActivity;
import com.sinhviencafemanagement.adapters.customer.OrderReceiptAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.utils.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderSuccessActivity extends AppCompatActivity {

    private ImageView imgBack;
    private Button btnTrackOrder;
    private TextView tvTransactionId, tvOrderDate, tvTotalFinal, tvName, tvPhone, tvAddress;
    private RecyclerView rcvOrderProducts;

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        initView();
        initData();

        // Nhận ID đơn hàng từ CartActivity gửi sang
        int orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderInfo(orderId);
            displayProducts(orderId);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đơn hàng!", Toast.LENGTH_SHORT).show();
        }

        setupClickListeners(orderId);
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        tvTransactionId = findViewById(R.id.tvTransactionId);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvTotalFinal = findViewById(R.id.tvTotalFinal);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        rcvOrderProducts = findViewById(R.id.rcvOrderProducts);
    }

    private void initData() {
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);
        sessionManager = new SessionManager(this);
    }

    private void loadOrderInfo(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

            tvTransactionId.setText(String.valueOf(order.getOrderId()));
            tvOrderDate.setText(order.getOrderDate());
            tvTotalFinal.setText(currencyFormat.format(order.getTotalPrice()) + "vnd");

            // Sử dụng các hàm trong SessionManager
            tvName.setText(sessionManager.getUserFullName());
            tvPhone.setText(sessionManager.getUserPhone());
            // tvAddress có thể set mặc định hoặc lấy từ User model nếu bạn bổ sung field address
            if (order.getAddress() != null && !order.getAddress().isEmpty()) {
                tvAddress.setText(order.getAddress());
            } else {
                tvAddress.setText("Nhận tại cửa hàng");
            }
        }
    }

    private void displayProducts(int orderId) {
        // Lấy danh sách chi tiết đơn hàng (Sử dụng đúng tên hàm trong OrderDetailDAO của bạn)
        List<OrderDetail> details = orderDetailDAO.getDetailsByOrderId(orderId);

        // Thiết lập Adapter
        OrderReceiptAdapter adapter = new OrderReceiptAdapter(this, details);
        rcvOrderProducts.setLayoutManager(new LinearLayoutManager(this));
        rcvOrderProducts.setAdapter(adapter);

        // Vô hiệu hóa scrolling của RecyclerView để NestedScrollView bên ngoài xử lý
        rcvOrderProducts.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners(int orderId) {
        imgBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSuccessActivity.this, UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnTrackOrder.setOnClickListener(v -> {
            // Chuyển sang màn hình theo dõi đơn hàng
            Intent intent = new Intent(OrderSuccessActivity.this, TrackOrderActivity.class);
            intent.putExtra("ORDER_ID", orderId);
            startActivity(intent);
        });
    }
}