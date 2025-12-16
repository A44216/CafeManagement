// D:/CafeManagement/app/src/main/java/com/sinhviencafemanagement/fragments/user/HomeFragment.java

package com.sinhviencafemanagement.fragments.user;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.product.ProductDetailActivity;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.adapter.customer.ProductCustomerAdapter;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ProductCustomerAdapter.OnProductClickListener {

    private static final String TAG = "HomeFragment";
    private RecyclerView rcvProductList;
    private ProductCustomerAdapter productAdapter;
    private ProductDAO productDAO;
    private TabLayout tabLayout;
    private CategoryDAO categoryDAO;

    private ImageView ivCart;

    // Khai báo Chip và Danh sách gốc của Category hiện tại
    private Chip chipAll;
    private Chip chipRating;
    private Chip chipPrice;
    private Chip chipPromo;
    private List<Product> categoryFullProductList = new ArrayList<>();
    private final List<Product> displayedProductList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_customer, container, false);
    }

    // Bước 3: Ghi đè phương thức onViewCreated
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Khởi tạo DAO
        if (getContext() != null) {
            productDAO = new ProductDAO(getContext());
            categoryDAO = new CategoryDAO(getContext());
        } else {
            return;
        }

        // 2. Ánh xạ và Thiết lập RecyclerView
        rcvProductList = view.findViewById(R.id.rcvProduct);
        tabLayout = view.findViewById(R.id.tabLayout);
        chipAll = view.findViewById(R.id.chipAll);
        chipRating = view.findViewById(R.id.chipRating);
        chipPrice = view.findViewById(R.id.chipPrice);
        chipPromo = view.findViewById(R.id.chipPromo);

        // Sử dụng LinearLayoutManager theo yêu cầu
        rcvProductList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Khởi tạo Adapter CHỈ MỘT LẦN
        productAdapter = new ProductCustomerAdapter(getContext(), displayedProductList, this);
        rcvProductList.setAdapter(productAdapter);
        ivCart = view.findViewById(R.id.ivCart);

        // 2. Cài đặt sự kiện click cho nút giỏ hàng
        ivCart.setOnClickListener(v -> {
            // Tạo một Intent để mở CartActivity
            Intent intent = new Intent(getActivity(), com.sinhviencafemanagement.activities.cart.CartActivity.class);

            // Thực hiện chuyển màn hình
            startActivity(intent);
        });

        // 4. Thiết lập Listener và Kích hoạt Tab đầu tiên (sẽ tải dữ liệu)
        setupCategoryFiltering();
        setupChipFiltering();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        intent.putExtra("product_detail", product);
        startActivity(intent);
    }
    private void setupCategoryFiltering() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedCategoryName = tab.getText().toString();
                loadProductsByCategoryName(selectedCategoryName);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                rcvProductList.scrollToPosition(0);
            }
        });

        // Kích hoạt Tab đầu tiên để trigger onTabSelected và tải dữ liệu mặc định
        if (tabLayout.getTabCount() > 0) {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);

            if (firstTab != null && firstTab.getText() != null) {

                tabLayout.selectTab(firstTab);

                // Gọi hàm loadProductsByCategoryName() thủ công
                // Đảm bảo dữ liệu hiển thị ngay lập tức, không phụ thuộc vào event listener.
                loadProductsByCategoryName(firstTab.getText().toString());
            }
        }
    }

    private void setupChipFiltering() {
        View.OnClickListener chipListener = v -> {
            Chip selectedChip = (Chip) v;
            String filterType = "";

            // Xác định loại lọc/sắp xếp dựa trên ID của Chip
            int chipId = selectedChip.getId();
            if (chipId == R.id.chipAll) {
                filterType = "All";
            } else if (chipId == R.id.chipRating) {
                filterType = "Rating";
            } else if (chipId == R.id.chipPrice) {
                filterType = "Price";
            } else if (chipId == R.id.chipPromo) {
                filterType = "Promo";
            }

            applySortAndFilter(filterType);
        };

        chipAll.setOnClickListener(chipListener);
        chipRating.setOnClickListener(chipListener);
        chipPrice.setOnClickListener(chipListener);
        chipPromo.setOnClickListener(chipListener);

        // Đảm bảo chipAll được chọn mặc định khi Fragment tải (nếu chưa được thiết lập trong XML)
        // chipAll.setChecked(true); // Chỉ dùng nếu style là Chip.Choice
    }

    private void loadProductsByCategoryName(String categoryName) {

        String standardizedName = null;

        // 1. Chuẩn hóa tên Tab để khớp với tên trong DB
        switch (categoryName.trim()) {
            case "CÀ PHÊ":
                standardizedName = "Cà phê";
                break;
            case "TRÀ SỮA":
                standardizedName = "Trà sữa";
                break;
            case "SINH TỐ":
                standardizedName = "Sinh tố";
                break;
            // Thêm các Tab khác nếu có
            default:
                Log.e(TAG, "Tên Tab không khớp với bất kỳ Category nào: " + categoryName);
                break;
        }

        List<Product> products = new ArrayList<>();

        // 2. Lấy Category ID và sản phẩm
        if (standardizedName != null) {
            Category category = categoryDAO.getCategoryByName(standardizedName);

            if (category != null) {
                int categoryId = category.getCategoryId();

                // Lấy sản phẩm có sẵn theo Category ID
                products = productDAO.getAvailableProductsByCategory(categoryId);

                Log.d(TAG, "Đã tải SP cho: " + standardizedName + ", Số lượng: " + products.size());
            } else {
                Log.w(TAG, "Category '" + standardizedName + "' không có trong DB.");
            }
        }

        // 3. Cập nhật dữ liệu cho Adapter
        if (productAdapter != null) {
            productAdapter.updateList(products);
        }

        if (!products.isEmpty()) {
            categoryFullProductList = products;
        } else {
            categoryFullProductList = new ArrayList<>();
        }
        applySortAndFilter("All");
    }

    private void applySortAndFilter(String filterType) {
        if (categoryFullProductList.isEmpty()) {
            Log.d(TAG, "Danh sách sản phẩm của Category hiện tại trống.");
            return;
        }

        // Luôn bắt đầu từ bản sao của danh sách gốc để thao tác (không làm thay đổi categoryFullProductList)
        List<Product> filteredList = new ArrayList<>(categoryFullProductList);

        switch (filterType) {
            case "Rating":
                // --- LOGIC LỌC/SẮP XẾP XẾP HẠNG ---
                // YÊU CẦU: Thêm trường Rating vào Product model
                // Tạm thời sắp xếp theo tên để minh họa nếu chưa có Rating
                // Collections.sort(filteredList, (p1, p2) -> p1.getProductName().compareTo(p2.getProductName()));
                Log.d(TAG, "Đã kích hoạt sắp xếp theo Xếp hạng.");
                break;

            case "Price":
                // --- LOGIC SẮP XẾP GIÁ (Giảm dần: Giá cao nhất lên đầu) ---
                // Sử dụng Comparator trong Java
                filteredList.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                Log.d(TAG, "Đã sắp xếp theo Giá (Cao nhất).");
                break;

            case "Promo":
                // --- LOGIC LỌC KHUYẾN MÃI ---
                // YÊU CẦU: Thêm trường isPromo (boolean) vào Product model
                // Tạm thời, lọc các sản phẩm có giá < 30000 để mô phỏng Khuyến mãi
                filteredList.removeIf(product -> product.getPrice() >= 30000);
                Log.d(TAG, "Đã kích hoạt lọc Khuyến mãi.");
                break;

            case "All":
            default:
                // --- LOGIC MẶC ĐỊNH (Reset) ---
                // filteredList đã là bản sao của categoryFullProductList, không cần làm gì thêm
                Log.d(TAG, "Đã reset bộ lọc/sắp xếp (Hiển thị Tất cả).");
                break;
        }

        // Cập nhật Adapter
        if (productAdapter != null) {
            productAdapter.updateList(filteredList);
        }
    }
}