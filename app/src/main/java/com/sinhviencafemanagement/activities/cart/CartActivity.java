package com.sinhviencafemanagement.activities.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.cart.SelectAddressActivity;
import com.sinhviencafemanagement.activities.home.order.OrderSuccessActivity;
import com.sinhviencafemanagement.adapters.customer.CartAdapter;
import com.sinhviencafemanagement.dao.AddressDAO;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.OrderDetailToppingDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Address;
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

    // === KHAI BÁO BIẾN ===
    private RecyclerView rcvCartItems;
    private CartAdapter cartAdapter;
    private Button btnOrder;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice, tvSubTotal, tvDiscountAmount, tvDiscountNote, tvItemCount;
    private TextView tvSelectedAddress; // <-- THÊM KHAI BÁO
    private RadioGroup rgDiscount;
    private LinearLayout layoutSelectAddress;

    // DAO & Managers
    private AddressDAO addressDAO;
    private OrderDAO orderDAO;
    private OrderDetailDAO orderDetailDAO;
    private OrderDetailToppingDAO orderDetailToppingDAO;
    private SessionManager sessionManager;

    // Data state
    private int selectedAddressId = -1;
    private double discountPercent = 0.0;

    // ActivityResultLauncher
    private final ActivityResultLauncher<Intent> selectAddressLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedAddressId = result.getData().getIntExtra(
                            SelectAddressActivity.EXTRA_SELECTED_ADDRESS_ID, -1);
                    loadAndDisplaySelectedAddress();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initView();
        initData();
        setupRecyclerView();
        setEventListeners();

        // Cập nhật giao diện lần đầu tiên
        updatePriceUI();
        loadAndDisplaySelectedAddress();
    }

    private void initView() {
        rcvCartItems = findViewById(R.id.rcvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnOrder = findViewById(R.id.btnOrder);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvDiscountNote = findViewById(R.id.tvDiscountNote);
        tvItemCount = findViewById(R.id.tvItemCount);
        rgDiscount = findViewById(R.id.rgDiscount);
        layoutSelectAddress = findViewById(R.id.selectAddress);
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress);
    }

    private void initData() {
        orderDAO = new OrderDAO(this);
        orderDetailDAO = new OrderDetailDAO(this);
        sessionManager = new SessionManager(this);
        orderDetailToppingDAO = new OrderDetailToppingDAO(this);
        addressDAO = new AddressDAO(this);
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

        layoutSelectAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, SelectAddressActivity.class);
            selectAddressLauncher.launch(intent);
        });
    }

    private void loadAndDisplaySelectedAddress() {
        if (selectedAddressId != -1) {
            Address selectedAddress = addressDAO.getAddressById(selectedAddressId);
            if (selectedAddress != null) {
                tvSelectedAddress.setText(selectedAddress.getAddress());
                tvSelectedAddress.setTextColor(getResources().getColor(R.color.black, getTheme()));
            }
        } else {
            // Lấy địa chỉ mặc định của user nếu có (cải tiến)
            // Tạm thời để mặc định là chưa chọn
            tvSelectedAddress.setText("Chưa chọn địa chỉ giao hàng");
        }
    }

    private void updatePriceUI() {
        // ... (phần này không đổi)
        double subTotal = 0;
        int totalQuantity = 0;
        for (CartItem item : cartItemList) {
            subTotal += item.getPrice() * item.getQuantity();
            totalQuantity += item.getQuantity();
        }

        double discountAmount = subTotal * discountPercent;
        double finalTotal = subTotal - discountAmount;

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));

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
        if (selectedAddressId == -1) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        // ... (logic tính toán giá và tạo đối tượng Order không đổi)
        double subTotal = 0;
        for (CartItem item : cartItemList) subTotal += item.getPrice() * item.getQuantity();
        double finalTotal = subTotal * (1 - discountPercent);

        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        order.setStatus(CreateDatabase.ORDER_STATUS_PENDING);
        order.setTotalPrice(finalTotal);

        Address address = addressDAO.getAddressById(selectedAddressId);

        order.setAddress(address.getAddress());
        long orderId = orderDAO.addOrder(order);

        if (orderId != -1) {
            for (CartItem item : cartItemList) {
                // ... (logic thêm order_detail và order_detail_toppings không đổi)
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

            Intent intent = new Intent(this, OrderSuccessActivity.class);
            intent.putExtra("ORDER_ID", (int) orderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // Không cần gọi finish() ở đây
        }
    }

    @Override
    public void onCartChanged() {
        updatePriceUI();
    }
}
