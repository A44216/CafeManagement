package com.sinhviencafemanagement.adapters.admin.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapters.admin.viewholder.ProductAdminVH;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Category;
import com.sinhviencafemanagement.models.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminVH> {

    private final List<Product> productList; // Danh sách Category
    private final Map<Integer, String> categoryMap = new HashMap<>();

    private final Context context; // Context để inflate layout

    public interface OnProductActionListener {
        void onEdit(Product product);
        void onDelete(Product product);
    }

    private ProductAdminAdapter.OnProductActionListener listener; // Biến listener

    // Setter listener
    public void setOnProductActionListener(ProductAdminAdapter.OnProductActionListener listener) {
        this.listener = listener;
    }

    public ProductAdminAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;

        // Load tất cả category 1 lần
        CategoryDAO categoryDAO = new CategoryDAO(context);
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category c : categories) {
            categoryMap.put(c.getCategoryId(), c.getCategoryName());
        }

    }

    // Tạo ViewHolder mới khi RecyclerView cần hiển thị 1 item
    @NonNull
    @Override
    public ProductAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_product_admin.xml thành View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_admin, parent, false);
        // Tạo ViewHolder và trả về
        return new ProductAdminVH(view);
    }

    // Bind dữ liệu của product vào ViewHolder
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdminVH holder, int position) {
        // Lấy dữ liệu product tại vị trí position
        Product product = productList.get(position);
        if (product == null) return;

        // Clear request Glide cũ để tránh hiển thị nhầm ảnh
        Glide.with(context).clear(holder.imgProduct);
        holder.imgProduct.setImageResource(R.drawable.ic_broken_image); // reset ngay

        int imageResId = product.getImageResId();
        String imagePath = product.getImagePath();

        if (imageResId != 0) {
            // Load ảnh drawable, bỏ qua imagePath
            Glide.with(context)
                    .load(imageResId)
                    .placeholder(R.drawable.ic_broken_image)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .signature(new com.bumptech.glide.signature.ObjectKey(product.getProductId()))
                    .into(holder.imgProduct);
        } else if (imagePath != null && !imagePath.isEmpty()) {
            // Load ảnh từ path nếu drawable không tồn tại
            Glide.with(context)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_broken_image)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .signature(new com.bumptech.glide.signature.ObjectKey(product.getProductId()))
                    .into(holder.imgProduct);
        } else {
            // Cả 2 không có thì load ảnh mặc định lỗi
            holder.imgProduct.setImageResource(R.drawable.ic_broken_image);
        }

        // Tên sản phẩm
        String name = product.getProductName();
        holder.tvProductName.setText(name != null ? name : "Không có tên");

        // Giá sản phẩm
        holder.tvProductPrice.setText((product.getPrice() >= 0 ? String.valueOf(product.getPrice()) : "0") + " vnd");

        // Tên danh mục từ map đã preload
        int categoryId = product.getCategoryId();
        String categoryName = categoryMap.getOrDefault(categoryId, "Unknown");
        holder.tvProductCategory.setText("Thể loại: " + categoryName);

        // Trạng thái sản phẩm
        String status = product.getStatus() != null ? product.getStatus() : "";
        holder.tvProductStatus.setText("Trạng thái: " + (getStatusVietnamese(status)));

        // Click Edit
        holder.ivEditProduct.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(product);
        });

        // Click Delete
        holder.ivDeleteProduct.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(product);
        });
    }

    private String getStatusVietnamese(String status) {
        return switch (status.toLowerCase()) {
            case CreateDatabase.PRODUCT_STATUS_AVAILABLE -> "Có sẵn";
            case CreateDatabase.PRODUCT_STATUS_UNAVAILABLE -> "Hết hàng";
            default -> "Không xác định";
        };
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
