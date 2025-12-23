package com.sinhviencafemanagement.adapters.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.models.Product;
import java.util.List;

// Import thư viện ảnh (giả định, bạn cần thêm thư viện này vào build.gradle)
// import com.bumptech.glide.Glide;

public class ProductCustomerAdapter extends RecyclerView.Adapter<ProductCustomerAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
    private final Context context;
    private final List<Product> productList;
    private final OnProductClickListener listener;
    public ProductCustomerAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_product_customer.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_customer, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // 1. Gán Tên, Mô tả, và Giá
        holder.tvName.setText(product.getProductName());

        // Sử dụng Mô tả nếu có, nếu không thì hiển thị một chuỗi mặc định
        String description = product.getDescription();
        holder.tvDescription.setText(description != null && !description.isEmpty() ? description : "Thức uống thơm ngon");

        // Định dạng giá tiền (ví dụ: 45,000đ)
        holder.tvPrice.setText(String.format("%,.0fđ", product.getPrice()));

        // Lưu ý: Hiện tại, Model Product không có trường Rating, nên chúng ta sẽ dùng giá trị cố định hoặc bỏ qua.
        // holder.tvRating.setText("⭐ 4.5"); // Giữ rating cố định

        // 2. Xử lý hiển thị hình ảnh
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            // Tải ảnh từ đường dẫn lưu trữ ngoài (Sử dụng Glide/Picasso)
            // Ví dụ: Glide.with(context).load(product.getImagePath()).centerCrop().into(holder.imgProduct);
        } else if (product.getImageResId() != 0) {
            // Hiển thị ảnh mặc định từ drawable
            holder.imgProduct.setImageResource(product.getImageResId());
        } else {
            // Ảnh placeholder cuối cùng
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
        }

        // 3. Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product); // Gọi callback khi click
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class: Giữ các tham chiếu đến các View trong item_product_customer.xml
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName;
        TextView tvDescription;
        TextView tvRating;
        TextView tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ ID từ item_product_customer.xml
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }

    public void updateList(List<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }
}
