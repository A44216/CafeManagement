package com.sinhviencafemanagement.activities.home.order;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.OrderDetailAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.OrderDetailToppingDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;

import java.util.List;

public class OrderTrackingActivity extends AppCompatActivity {

    private ImageView ivBack, ivProduct;
    private RadioGroup rgOrderStatus;
    private RadioButton rbPending, rbDone;

    private RecyclerView rvOrderItems;

    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private ProductDAO productDAO;
    private ToppingDAO toppingDAO;
    private OrderDetailToppingDAO odtDAO;

    private int orderId;  // id đơn hàng được truyền

    private boolean isProgrammaticallySetting = false; // biến cờ để tránh dialog 2 lần

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

        // Lấy orderId từ Intent
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initDAOs();
        initViews();
        setListeners();

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        loadAllOrderDetails();
        setRadioButtonByOrderId(orderId); // đặt RadioButton theo trạng thá
    }

    // Ánh xạ view
    public void initViews() {
        ivBack = findViewById(R.id.ivBack);
        ivProduct = findViewById(R.id.ivProduct);
        rgOrderStatus = findViewById(R.id.rgOrderStatus);
        rbPending = findViewById(R.id.rbPending);
        rbDone = findViewById(R.id.rbDone);
        rvOrderItems = findViewById(R.id.rvOrderItems);

        // Mặc định chọn bước đầu tiên
        rbPending.setChecked(true);
    }

    // Thiết lập sự kiện
    public void setListeners() {
        // Xử lý nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // Xử lý chọn bước timeline
        rgOrderStatus.setOnCheckedChangeListener((group, checkedId) -> {
            if (isProgrammaticallySetting) return; // tránh trigger dialog khi set programmatically

            String newStatus = null;
            if (checkedId == R.id.rbPending) {
                newStatus = CreateDatabase.ORDER_STATUS_PENDING;
            } else if (checkedId == R.id.rbDone) {
                newStatus = CreateDatabase.ORDER_STATUS_COMPLETED;
            }

            if (newStatus != null) {
                showConfirmUpdateStatusDialog(newStatus);
            }
        });
    }

    private void showConfirmUpdateStatusDialog(String newStatus) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn cập nhật trạng thái đơn hàng?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    updateOrderStatus(newStatus);
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    // Reset RadioButton về trạng thái cũ nếu hủy
                    setRadioButtonByOrderId(orderId);
                })
                .setCancelable(false)
                .show();
    }

    // Khởi tạo DAO
    private void initDAOs() {
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);
        productDAO = new ProductDAO(this);
        toppingDAO = new ToppingDAO(this);
        odtDAO = new OrderDetailToppingDAO(this);
    }

    // Đặt RadioButton theo trạng thái đơn hàng
    private void setRadioButtonByOrderId(int orderId) {
        isProgrammaticallySetting = true; // bật cờ trước khi set
        String status = orderDAO.getOrderStatusById(orderId); // lấy status từ OrderDAO
        if (CreateDatabase.ORDER_STATUS_PENDING.equals(status)) {
            rbPending.setChecked(true);
        } else if (CreateDatabase.ORDER_STATUS_COMPLETED.equals(status)) {
            rbDone.setChecked(true);
        }
        isProgrammaticallySetting = false; // tắt cờ sau khi set
    }

    // Load tất cả chi tiết đơn hàng theo orderId
    private void loadAllOrderDetails() {
        // Lấy chi tiết đơn hàng theo orderId
        List<OrderDetail> details = orderDetailDAO.getDetailsByOrderId(orderId);
        // Tạo adapter
        OrderDetailAdapter adapter = new OrderDetailAdapter(this, details, productDAO, odtDAO, toppingDAO);
        rvOrderItems.setAdapter(adapter);
    }

    // Cập nhật trạng thái đơn hàng vào database và load lại danh sách
    private void updateOrderStatus(String newStatus) {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            int rows = orderDAO.updateOrder(order);
            if (rows > 0) {
                // Trả kết quả về Fragment
                Intent intent = new Intent();
                intent.putExtra("order_id", orderId);
                intent.putExtra("new_status", newStatus);
                setResult(RESULT_OK, intent);

                Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show();
                finish(); // kết thúc activity
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }




}