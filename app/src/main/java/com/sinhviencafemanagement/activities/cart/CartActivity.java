package com.sinhviencafemanagement.activities.cart;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.user.CartAdapter;
import com.sinhviencafemanagement.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rcvCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Ánh xạ RecyclerView
        rcvCartItems = findViewById(R.id.rcvCartItems);

        // Khởi tạo danh sách và adapter
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);

        // Cài đặt LayoutManager và Adapter cho RecyclerView
        rcvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rcvCartItems.setAdapter(cartAdapter);

        // Tắt tính năng cuộn của RecyclerView để cho phép NestedScrollView cuộn
        rcvCartItems.setNestedScrollingEnabled(false);

        // Tải dữ liệu giỏ hàng (ví dụ dữ liệu mẫu)
        loadCartData();
    }

    private void loadCartData() {
        // Đây là nơi bạn sẽ lấy dữ liệu từ SQLite
        // Hiện tại, chúng ta sẽ dùng dữ liệu mẫu để kiểm tra

        // Xóa danh sách cũ để tránh thêm trùng lặp nếu gọi lại hàm này
        cartItemList.clear();

        // BẰNG CÁC DÒNG MỚI NÀY (truyền vào tên ảnh dạng String):
        cartItemList.add(new CartItem(1, "Cà Phê Sữa Nóng", "Size: M, Ít đường", 59000, 1, "cafe_sua_nong"));
        cartItemList.add(new CartItem(2, "Trà Sữa Trân Châu", "Size: L, 70% đá", 65000, 2, "tra_sua"));
        cartItemList.add(new CartItem(3, "Bạc Xỉu", "Size: M, Nóng", 55000, 1, "bac_xiu"));

        // Thông báo cho adapter rằng dữ liệu đã thay đổi để nó cập nhật giao diện
        cartAdapter.notifyDataSetChanged();
    }
}
