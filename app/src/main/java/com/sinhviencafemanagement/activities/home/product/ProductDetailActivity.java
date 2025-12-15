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
import com.sinhviencafemanagement.activities.home.order.CartActivity;
import com.sinhviencafemanagement.models.Product;

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
            String note = edtNote.getText().toString().trim();

            // Tạm thời chỉ demo
            Toast.makeText(this,
                    "Đã thêm vào giỏ hàng\n" +
                            "SP: " + product.getProductName() +
                            "\nSL: " + quantity +
                            "\nTổng: " + tvTotalPrice.getText(),
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, CartActivity.class));
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
