package com.sinhviencafemanagement.adapters.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Thư viện tải ảnh hiệu quả
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<CartItem> cartItems;
    private final Context context;

    public interface OnCartChangeListener {
        void onCartChanged();
    }
    private OnCartChangeListener cartChangeListener; // Biến để lưu "sứ giả"

    //Constructor
    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartChangeListener = listener; // Lưu lại "sứ giả"
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Thổi phồng" layout item_cart.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Lấy dữ liệu của item tại vị trí `position`
        CartItem currentItem = cartItems.get(position);

        // Gán dữ liệu lên các View
        holder.tvProductName.setText(currentItem.getName());
        holder.tvDescription.setText(currentItem.getDescription());
        holder.tvQuantity.setText(String.valueOf(currentItem.getQuantity()));
        holder.tvQuantityText.setText("x" + currentItem.getQuantity());

        // Định dạng và hiển thị giá tiền
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvPrice.setText(currencyFormatter.format(currentItem.getPrice()));

        // Tải hình ảnh bằng Glide (thay thế bằng ảnh thật)
        // Ví dụ này giả sử imageUrl là tên file trong drawable
        Object imageIdentifier = currentItem.getImageIdentifier();

        if (imageIdentifier instanceof String) {
            // Nếu là String, đây là đường dẫn file hoặc tên file trong drawable
            Glide.with(context)
                    .load(imageIdentifier)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        } else if (imageIdentifier instanceof Integer) {
            // Nếu là Integer, đây là Resource ID
            Glide.with(context)
                    .load((Integer) imageIdentifier) // Ép kiểu về Integer
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        } else {
            // Nếu không có ảnh, hiển thị một ảnh mặc định
            holder.ivProductImage.setImageResource(R.drawable.cat_coffee);
        }

        // --- Cài đặt sự kiện cho các nút ---
        holder.btnPlus.setOnClickListener(v -> {
            int quantity = currentItem.getQuantity();
            quantity++;
            currentItem.setQuantity(quantity);
            notifyItemChanged(holder.getAdapterPosition());
            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged(); // Báo cho Activity
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            int quantity = currentItem.getQuantity();
            if (quantity > 1) {
                quantity--;
                currentItem.setQuantity(quantity);
                notifyItemChanged(holder.getAdapterPosition());
            }
            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged(); // Báo cho Activity
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            cartItems.remove(currentPosition);
            notifyItemRemoved(currentPosition);
            notifyItemRangeChanged(currentPosition, cartItems.size());
            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged(); // Báo cho Activity
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            // TODO: Xử lý sự kiện khi người dùng muốn chỉnh sửa chi tiết sản phẩm
            // Ví dụ: Mở lại màn hình chi tiết sản phẩm với thông tin của item này
        });
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số item trong danh sách
        return cartItems != null ? cartItems.size() : 0;
    }

    /**
     * Lớp ViewHolder để "giữ" các tham chiếu đến View của một item,
     * giúp tăng hiệu suất bằng cách tránh gọi findViewById() nhiều lần.
     */
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        // Khai báo tất cả các View có trong item_cart.xml
        ImageView ivProductImage, btnEdit, btnDelete;
        TextView tvProductName, tvPrice, tvDescription, tvQuantityText, tvQuantity;
        TextView btnMinus, btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View từ layout
            ivProductImage = itemView.findViewById(R.id.cvProductImage).findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvQuantityText = itemView.findViewById(R.id.tvQuantityText);

            // Các nút
            btnEdit = itemView.findViewById(R.id.editIcon);
            btnDelete = itemView.findViewById(R.id.deleteIcon);
            LinearLayout quantityControlLayout = itemView.findViewById(R.id.quantityControlLayout);
            btnMinus = quantityControlLayout.findViewById(R.id.minusButton);
            tvQuantity = quantityControlLayout.findViewById(R.id.quantityTextView);
            btnPlus = quantityControlLayout.findViewById(R.id.plusButton);
        }
    }
}
