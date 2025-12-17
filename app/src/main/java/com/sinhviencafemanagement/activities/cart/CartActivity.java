package com.sinhviencafemanagement.activities.cart;

// ... các import ...
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.order.OrderSuccessActivity;
import com.sinhviencafemanagement.adapter.customer.CartAdapter;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.OrderDetailToppingDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.CartItem;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.models.OrderDetailTopping;
import com.sinhviencafemanagement.models.Topping;
import com.sinhviencafemanagement.utils.CartManager;
import com.sinhviencafemanagement.utils.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {
    private RecyclerView rcvCartItems;
    private CartAdapter cartAdapter;
    private Button btnOrder;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice;
    private OrderDAO orderDAO;
    private OrderDetailToppingDAO orderDetailToppingDAO;
    private OrderDetailDAO orderDetailDAO;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // --- KHỞI TẠO ---
        initView();
        initData();
        setupRecyclerView();
        setEventListeners();

        // Cập nhật giao diện lần đầu
        updateTotalPrice();
    }

    private void initView() {
        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void initData() {
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);
        sessionManager = new SessionManager(this);
        orderDetailToppingDAO = new OrderDetailToppingDAO(this);
        cartItemList = CartManager.getInstance().getCartItems();
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(this, cartItemList, this);
        rcvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rcvCartItems.setAdapter(cartAdapter);
        rcvCartItems.setNestedScrollingEnabled(false);
    }

    private void setEventListeners() {
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        btnOrder.setOnClickListener(v -> {
            if (cartItemList == null || cartItemList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            placeOrder();
        });
    }

    private void placeOrder() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Order
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        order.setStatus(CreateDatabase.ORDER_STATUS_PENDING);
        order.setTotalPrice(calculateTotalPrice());

        // Thêm đơn hàng vào CSDL
        long orderId = orderDAO.addOrder(order);

        if (orderId != -1) {
            // Thêm chi tiết đơn hàng
            for (CartItem item : cartItemList) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId((int) orderId);
                detail.setProductId(item.getProductId());
                detail.setQuantity(item.getQuantity());
                orderDetailDAO.addOrderDetail(detail);

                // TODO: Xử lý lưu topping cho item này vào bảng order_detail_toppings
                if (item.getSelectedToppings() != null && !item.getSelectedToppings().isEmpty()) {
                    // Lặp qua danh sách các topping đã chọn của item
                    for (Topping selectedTopping : item.getSelectedToppings()) {
                        // Gọi DAO để thêm từng topping vào bảng order_detail_toppings
                        OrderDetailTopping odt = new OrderDetailTopping(
                                (int) orderId,
                                item.getProductId(),
                                selectedTopping.getToppingId(),
                                1
                        );
                        orderDetailToppingDAO.addOrderDetailTopping(odt);
                    }
                }
            }

            // Dọn dẹp và chuyển màn hình
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(CartActivity.this, OrderSuccessActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Các phương thức còn lại không thay đổi ---

    private double calculateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private void updateTotalPrice() {
        double total = calculateTotalPrice();
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(format.format(total) + "đ");
    }

    @Override
    public void onCartChanged() {
        updateTotalPrice();
    }
}
