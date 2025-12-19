package com.sinhviencafemanagement.activities.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.order.OrderSuccessActivity;
import com.sinhviencafemanagement.adapters.customer.CartAdapter;
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

    // UI components cho phần giá
    private TextView tvTotalPrice, tvSubTotal, tvDiscountAmount, tvDiscountNote, tvItemCount;
    private RadioGroup rgDiscount;

    private OrderDAO orderDAO;
    private OrderDetailToppingDAO orderDetailToppingDAO;
    private OrderDetailDAO orderDetailDAO;
    private SessionManager sessionManager;

    private double discountPercent = 0.0; // Lưu % giảm giá đang chọn

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initView();
        initData();
        setupRecyclerView();
        setEventListeners();

        updatePriceUI();
    }

    private void initView() {
        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice); // Tổng cuối cùng ở bottom bar
        btnOrder = findViewById(R.id.btnOrder);

        // Ánh xạ các view mới ở phần thanh toán
        tvSubTotal = findViewById(R.id.tvSubTotal); // Giá gốc (tạm tính)
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount); // Số tiền trừ ra
        tvDiscountNote = findViewById(R.id.tvDiscountNote); // Chữ "Chưa áp dụng..." hoặc "%"
        tvItemCount = findViewById(R.id.tvItemCount); // "(X đồ uống)"
        rgDiscount = findViewById(R.id.rgDiscount);
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

        // Xử lý khi chọn RadioButton giảm giá
        rgDiscount.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDiscount10) {
                discountPercent = 0.10;
            } else if (checkedId == R.id.rbDiscount15) {
                discountPercent = 0.15;
            } else {
                discountPercent = 0.0;
            }
            updatePriceUI();
        });
    }

    private void updatePriceUI() {
        double subTotal = 0;
        int totalQuantity = 0;
        for (CartItem item : cartItemList) {
            subTotal += item.getPrice() * item.getQuantity();
            totalQuantity += item.getQuantity();
        }

        double discountAmount = subTotal * discountPercent;
        double finalTotal = subTotal - discountAmount;

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

        // Cập nhật text hiển thị
        tvItemCount.setText("(" + totalQuantity + " đồ uống)");
        tvSubTotal.setText(format.format(subTotal) + "đ");
        tvDiscountAmount.setText("-" + format.format(discountAmount) + "đ");
        tvTotalPrice.setText(format.format(finalTotal) + "đ");

        if (discountPercent > 0) {
            tvDiscountNote.setText("Đã áp dụng giảm giá " + (int)(discountPercent * 100) + "%");
        } else {
            tvDiscountNote.setText("Chưa áp dụng mã khuyến mại");
        }
    }

    private void placeOrder() {
        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi xác thực", Toast.LENGTH_SHORT).show();
            return;
        }

        double subTotal = 0;
        for (CartItem item : cartItemList) subTotal += item.getPrice() * item.getQuantity();
        double finalTotal = subTotal * (1 - discountPercent);

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        order.setStatus(CreateDatabase.ORDER_STATUS_PENDING);
        order.setTotalPrice(finalTotal); // Lưu giá sau khi giảm vào database

        long orderId = orderDAO.addOrder(order);

        if (orderId != -1) {
            for (CartItem item : cartItemList) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId((int) orderId);
                detail.setProductId(item.getProductId());
                detail.setQuantity(item.getQuantity());
                orderDetailDAO.addOrderDetail(detail);

                if (item.getSelectedToppings() != null) {
                    for (Topping topping : item.getSelectedToppings()) {
                        OrderDetailTopping odt = new OrderDetailTopping((int)orderId, item.getProductId(), topping.getToppingId(), 1);
                        orderDetailToppingDAO.addOrderDetailTopping(odt);
                    }
                }
            }
            CartManager.getInstance().clearCart();
//            startActivity(new Intent(this, OrderSuccessActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            // Chuyển sang OrderSuccessActivity và gửi kèm Order ID
            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("ORDER_ID", (int) orderId);
            // Xóa các activity trước đó để người dùng không back lại giỏ hàng đã trống
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCartChanged() {
        updatePriceUI();
    }
}