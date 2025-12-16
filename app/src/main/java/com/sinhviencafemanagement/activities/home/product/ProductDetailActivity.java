package com.sinhviencafemanagement.activities.home.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.cart.CartActivity;
import com.sinhviencafemanagement.models.CartItem;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.utils.CartManager;

import java.text.NumberFormat;
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

    // Giá topping
    private final int PRICE_SUA_DAC = 5000;
    private final int PRICE_TRAN_CHAU = 6000;
    private final int PRICE_DUA_KHO = 3000;
    private final int PRICE_THACH_DUA = 7000;
    private final int PRICE_DUONG_DEN = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initView();
        getDataFromIntent();
        setEvent();
        updateTotalPrice();
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgProductDetail = findViewById(R.id.imgProductDetail);
        tvNameDetail = findViewById(R.id.tvNameDetail);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);
        tvDescDetail = findViewById(R.id.tvDescDetail);

        tvQuantity = findViewById(R.id.tvQuantity);
        btnPlus = findViewById(R.id.btnPlus);
        btnMinus = findViewById(R.id.btnMinus);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);

        btnAddCart = findViewById(R.id.btnAddCart);
        edtNote = findViewById(R.id.edtNote);

        // Checkbox topping (BẮT BUỘC phải có id trong XML)
        cbSuaDac = findViewById(R.id.cbSuaDac);
        cbTranChau = findViewById(R.id.cbTranChau);
        cbDuaKho = findViewById(R.id.cbDuaKho);
        cbThachDua = findViewById(R.id.cbThachDua);
        cbDuongDen = findViewById(R.id.cbDuongDen);
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
            int toppingPrice = 0;

            if (cbSuaDac.isChecked()) {
                descriptionBuilder.append("Sữa đặc, ");
                toppingPrice += PRICE_SUA_DAC;
            }
            if (cbTranChau.isChecked()) {
                descriptionBuilder.append("Trân châu, ");
                toppingPrice += PRICE_TRAN_CHAU;
            }
            if (cbDuaKho.isChecked()) {
                descriptionBuilder.append("Dừa khô, ");
                toppingPrice += PRICE_DUA_KHO;
            }
            if (cbThachDua.isChecked()) {
                descriptionBuilder.append("Thạch dừa, ");
                toppingPrice += PRICE_THACH_DUA;
            }
            if (cbDuongDen.isChecked()) {
                descriptionBuilder.append("Đường đen, ");
                toppingPrice += PRICE_DUONG_DEN;
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
            double finalPricePerItem = basePrice + toppingPrice;
            int itemId = (product.getProductId() + description).hashCode();

            // Giả sử bạn đã thêm phương thức getImageForCart() vào Product.java
            // String imageIdentifier = product.getImageForCart();
            // Nếu chưa, tạm thời dùng một ảnh mẫu:
            String imageIdentifier = "cat_coffee"; // Tạm thời

            CartItem newItem = new CartItem(itemId, name, description, finalPricePerItem, quantity, imageIdentifier);

            // 4. Thêm sản phẩm vào CartManager
            CartManager.getInstance().addItem(newItem);

            // 5. Hiển thị thông báo và quay lại màn hình trước
            Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            finish(); // Quay về màn hình Home
        });

    }

    private void updateTotalPrice() {
        if (product == null) return;

        int toppingPrice = 0;

        if (cbSuaDac.isChecked()) toppingPrice += PRICE_SUA_DAC;
        if (cbTranChau.isChecked()) toppingPrice += PRICE_TRAN_CHAU;
        if (cbDuaKho.isChecked()) toppingPrice += PRICE_DUA_KHO;
        if (cbThachDua.isChecked()) toppingPrice += PRICE_THACH_DUA;
        if (cbDuongDen.isChecked()) toppingPrice += PRICE_DUONG_DEN;

        double total = (product.getPrice() + toppingPrice) * quantity;
        tvTotalPrice.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        return format.format((long) price) + "đ"; // ép về long để không hiện .0
    }
}
