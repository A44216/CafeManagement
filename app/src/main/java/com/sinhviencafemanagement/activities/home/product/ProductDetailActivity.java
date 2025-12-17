package com.sinhviencafemanagement.activities.home.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.cart.CartActivity;
import com.sinhviencafemanagement.adapter.customer.ToppingAdapter;
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

    // View
    private ImageView imgBack, imgProductDetail;
    private TextView tvNameDetail, tvPriceDetail, tvDescDetail;
    private TextView tvQuantity, btnPlus, btnMinus, tvTotalPrice;
    private Button btnAddCart;
    private EditText edtNote;

    // Topping
    private CheckBox cbSuaDac, cbTranChau, cbDuaKho, cbThachDua, cbDuongDen;

    // Data
    private Product product;
    private int quantity = 1;
    // ...
    private RecyclerView rcvToppings;
    private ToppingAdapter toppingAdapter;
    private List<Topping> toppingList = new ArrayList<>();
    private ToppingDAO toppingDAO;

    private ChipGroup cgType, cgSize, cgSugar, cgIce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initView();
        getDataFromIntent();
        setEvent();
        setupToppingRecyclerView();
        updateTotalPrice();
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgProductDetail = findViewById(R.id.imgProductDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);
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
    private void setupToppingRecyclerView() {
        toppingDAO = new ToppingDAO(this);
        toppingList.clear();
        toppingList.addAll(toppingDAO.getAllToppings()); // Giả sử bạn có hàm này trong ToppingDAO

        toppingAdapter = new ToppingAdapter(this, toppingList, this::updateTotalPrice); // Báo cho updateTotalPrice mỗi khi topping thay đổi

        rcvToppings.setLayoutManager(new LinearLayoutManager(this));
        rcvToppings.setAdapter(toppingAdapter);
    }

    private void getDataFromIntent() {
        if (getIntent().hasExtra("product_detail")) {
            product = (Product) getIntent().getSerializableExtra("product_detail");
        }

        if (product != null) {
            tvNameDetail.setText(product.getProductName());
            tvPriceDetail.setText(formatPrice(product.getPrice()));
            tvDescDetail.setText(product.getDescription() != null
                    ? product.getDescription()
                    : "Không có mô tả chi tiết");

            if (product.getImageResId() != 0) {
                imgProductDetail.setImageResource(product.getImageResId());
            }
        }
    }

    private void setEvent() {

        // Back
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

        // Topping thay đổi → cập nhật giá
        CheckBox[] toppings = {
                cbSuaDac, cbTranChau, cbDuaKho, cbThachDua, cbDuongDen
        };

        for (CheckBox cb : toppings) {
            if (cb != null) {
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
            }
        }

        // Thêm vào giỏ hàng
        btnAddCart.setOnClickListener(v -> {
            // Kiểm tra null cho product để đảm bảo an toàn
            if (product == null) {
                Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Lấy thông tin cơ bản từ sản phẩm và các View
            String name = product.getProductName();
            double basePrice = product.getPrice();
            String note = edtNote.getText().toString().trim();

            // 2. Xây dựng mô tả và tính lại giá topping ngay tại thời điểm click
            StringBuilder descriptionBuilder = new StringBuilder();
            double optionsPrice = 0;

            Chip selectedTypeChip = findViewById(cgType.getCheckedChipId());
            if (selectedTypeChip != null) {
                // Nếu là "Nóng", không cần thêm "Đồ uống"
                if (selectedTypeChip.getText().toString().equals("Nóng")) {
                    descriptionBuilder.append(selectedTypeChip.getText()).append(", ");
                } else {
                    descriptionBuilder.append("Đồ uống ").append(selectedTypeChip.getText()).append(", ");
                }
            }

            // Lấy text từ Chip được chọn trong cgSugar
            Chip selectedSugarChip = findViewById(cgSugar.getCheckedChipId());
            if (selectedSugarChip != null) {
                descriptionBuilder.append(selectedSugarChip.getText()).append(" Đường, ");
            }

            // Lấy text từ Chip được chọn trong cgIce
            Chip selectedIceChip = findViewById(cgIce.getCheckedChipId());
            if (selectedIceChip != null) {
                descriptionBuilder.append(selectedIceChip.getText()).append(" Đá, ");
            }
            for (Topping topping : toppingList) {
                if (topping.isChecked()) {
                    descriptionBuilder.append(topping.getToppingName()).append(", ");
                    optionsPrice += topping.getPrice();
                }
            }

            // Thêm ghi chú của người dùng vào mô tả
            if (!note.isEmpty()) {
                descriptionBuilder.append("Ghi chú: ").append(note);
            }

            String description = descriptionBuilder.length() > 0
                    ? descriptionBuilder.toString()
                    : "Không có tùy chọn";
            // Xóa dấu phẩy và khoảng trắng thừa ở cuối (nếu có)
            if (description.endsWith(", ")) {
                description = description.substring(0, description.length() - 2);
            }

            // 3. Tạo đối tượng CartItem
            double finalPricePerItem = basePrice + optionsPrice;
            int realProductId = product.getProductId();
            int itemId = (product.getProductId() + description).hashCode();

            Object imageIdentifier = product.getImageForCart();
            List<Topping> selectedToppings = new ArrayList<>();
            for (Topping topping : toppingList) {
                if (topping.isChecked()) {
                    selectedToppings.add(topping);
                }
            }
            CartItem newItem = new CartItem(itemId, realProductId, name, description, finalPricePerItem, quantity, imageIdentifier, selectedToppings);

            // 4. Thêm sản phẩm vào CartManager
            CartManager.getInstance().addItem(newItem);

            // 5. Hiển thị thông báo và quay lại màn hình trước
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về màn hình Home
        });

    }

    // Sửa lại updateTotalPrice()
    private void updateTotalPrice() {
        if (product == null) return;

        double toppingPrice = 0;
        for (Topping topping : toppingList) {
            if (topping.isChecked()) {
                toppingPrice += topping.getPrice();
            }
        }

        double total = (product.getPrice() + toppingPrice) * quantity;
        tvTotalPrice.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format((long) price) + "đ"; // ép về long để không hiện .0
    }
}
