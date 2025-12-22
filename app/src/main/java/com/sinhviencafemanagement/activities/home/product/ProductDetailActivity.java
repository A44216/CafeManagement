package com.sinhviencafemanagement.activities.home.product;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapters.customer.ToppingAdapter;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.CartItem;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.models.Topping;
import com.sinhviencafemanagement.utils.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    // ===================== VIEW =====================

    // Nút quay lại & hình ảnh sản phẩm
    private ImageView imgBack, imgProductDetail;

    // Hiển thị tên, giá, mô tả sản phẩm
    private TextView tvNameDetail, tvPriceDetail, tvDescDetail;

    // Điều chỉnh số lượng và tổng tiền
    private TextView tvQuantity, btnPlus, btnMinus, tvTotalPrice;

    // Nút thêm vào giỏ hàng
    private Button btnAddCart;

    // Ô nhập ghi chú cho sản phẩm
    private EditText edtNote;

    // ===================== DATA =====================

    // Sản phẩm gốc (Prototype)
    private Product product;

    // Số lượng sản phẩm
    private int quantity = 1;

    // RecyclerView hiển thị danh sách topping
    private RecyclerView rcvToppings;

    // Adapter quản lý topping
    private ToppingAdapter toppingAdapter;

    // Danh sách topping
    private List<Topping> toppingList = new ArrayList<>();

    // DAO lấy topping từ database
    private ToppingDAO toppingDAO;

    // Các nhóm lựa chọn option
    private ChipGroup cgType, cgSugar, cgIce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Ánh xạ view
        initView();

        // Khởi tạo RecyclerView topping
        setupToppingRecyclerView();

        // Nhận dữ liệu sản phẩm từ Intent
        getDataFromIntent();

        // Gắn sự kiện click
        setEvent();

        // Tính tổng tiền ban đầu
        updateTotalPrice();
    }

    /**
     * Ánh xạ các View từ layout XML
     */
    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgProductDetail = findViewById(R.id.imgProductDetail);

        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);

        // ChipGroup lựa chọn option
        cgType = findViewById(R.id.rgType);
        cgSugar = findViewById(R.id.rgSugar);
        cgIce = findViewById(R.id.rgIce);

        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnAddCart = findViewById(R.id.btnAddCart);
        edtNote = findViewById(R.id.edtNote);

        rcvToppings = findViewById(R.id.rcvToppings);
    }

    /**
     * Khởi tạo RecyclerView hiển thị topping
     */
    private void setupToppingRecyclerView() {
        toppingDAO = new ToppingDAO(this);

        // Lấy toàn bộ topping từ database
        toppingList.clear();
        toppingList.addAll(toppingDAO.getAllToppings());

        // Adapter có callback để cập nhật giá
        toppingAdapter = new ToppingAdapter(this, toppingList, this::updateTotalPrice);

        rcvToppings.setLayoutManager(new LinearLayoutManager(this));
        rcvToppings.setAdapter(toppingAdapter);
    }

    /**
     * Nhận sản phẩm từ Intent
     */
    private void getDataFromIntent() {
        Product prototypeProduct =
                (Product) getIntent().getSerializableExtra("product_detail");

        if (prototypeProduct != null) {
            this.product = prototypeProduct;

            tvNameDetail.setText(product.getProductName());
            tvPriceDetail.setText(formatPrice(product.getPrice()));

            // Nếu không có mô tả thì hiển thị mặc định
            tvDescDetail.setText(product.getDescription() != null
                    ? product.getDescription()
                    : "Không có mô tả chi tiết");

            // Hiển thị ảnh sản phẩm
            if (product.getImageResId() != 0) {
                imgProductDetail.setImageResource(product.getImageResId());
            }
        }
    }

    /**
     * Gắn sự kiện cho các nút và ChipGroup
     */
    private void setEvent() {

        // Quay lại màn hình trước
        imgBack.setOnClickListener(v -> finish());

        // Tăng số lượng
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
            updateTotalPrice();
        });

        // Giảm số lượng
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
                updateTotalPrice();
            }
        });

        // Thay đổi option sẽ cập nhật giá
        if (cgType != null)
            cgType.setOnCheckedStateChangeListener((g, ids) -> updateTotalPrice());

        if (cgSugar != null)
            cgSugar.setOnCheckedStateChangeListener((g, ids) -> updateTotalPrice());

        if (cgIce != null)
            cgIce.setOnCheckedStateChangeListener((g, ids) -> updateTotalPrice());

        // Thêm vào giỏ hàng
        btnAddCart.setOnClickListener(v -> addToCart());
    }

    /**
     * Xử lý thêm sản phẩm vào giỏ hàng
     * Áp dụng PROTOTYPE PATTERN
     */
    private void addToCart() {

        if (product == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clone sản phẩm gốc (Prototype)
        Product customizedProduct = product.clone();

        StringBuilder desc = new StringBuilder();
        double optionPrice = 0;

        Chip chip;

        // Lấy option loại
        chip = findViewById(cgType.getCheckedChipId());
        if (chip != null) desc.append(chip.getText()).append(", ");

        // Lấy option đường
        chip = findViewById(cgSugar.getCheckedChipId());
        if (chip != null) desc.append(chip.getText()).append(" đường, ");

        // Lấy option đá
        chip = findViewById(cgIce.getCheckedChipId());
        if (chip != null) desc.append(chip.getText()).append(" đá, ");

        // Lấy danh sách topping đã chọn
        List<Topping> selectedToppings = toppingAdapter.getSelectedToppings();
        for (Topping t : selectedToppings) {
            desc.append(t.getToppingName()).append(", ");
            optionPrice += t.getPrice();
        }

        // Ghi chú thêm
        String note = edtNote.getText().toString().trim();
        if (!note.isEmpty()) {
            desc.append("Ghi chú: ").append(note);
        }

        // Xóa dấu phẩy cuối
        if (desc.toString().endsWith(", ")) {
            desc.setLength(desc.length() - 2);
        }

        // Áp dụng mô tả và giá mới cho sản phẩm clone
        customizedProduct.setDescription(desc.toString());
        customizedProduct.setPrice(product.getPrice() + optionPrice);

        // Tạo ID duy nhất cho CartItem
        int itemId = (customizedProduct.getProductId() + desc.toString()).hashCode();

        // Tạo CartItem
        CartItem item = new CartItem(
                itemId,
                customizedProduct.getProductId(),
                customizedProduct.getProductName(),
                customizedProduct.getDescription(),
                customizedProduct.getPrice(),
                quantity,
                customizedProduct.getImageForCart(),
                selectedToppings
        );

        // Thêm vào giỏ hàng
        CartManager.getInstance().addItem(item);

        Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Tính và cập nhật tổng tiền
     */
    private void updateTotalPrice() {
        if (product == null) return;

        double toppingPrice = 0;

        if (toppingAdapter != null) {
            for (Topping topping : toppingAdapter.getSelectedToppings()) {
                toppingPrice += topping.getPrice();
            }
        }

        double total = (product.getPrice() + toppingPrice) * quantity;
        tvTotalPrice.setText(formatPrice(total));
    }

    /**
     * Format tiền theo chuẩn VNĐ
     */
    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format((long) price) + "đ";
    }
}
