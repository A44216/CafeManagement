package com.sinhviencafemanagement.adapter.admin;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.OrderAdminVH;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.models.User;

import java.util.List;

public class OrderAdminAdapter extends RecyclerView.Adapter<OrderAdminVH> {

    private final List<Order> orderList;
    private OnOrderActionListener listener;

    private final UserDAO userDAO;
    private final ProductDAO productDAO;
    private final OrderDetailDAO orderDetailDAO;

    public interface OnOrderActionListener {
        void onTrack(Order order);  // Xem chi tiết/tracking
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public OrderAdminAdapter(List<Order> orderList, UserDAO userDAO, ProductDAO productDAO, OrderDetailDAO orderDetailDAO) {
        this.orderList = orderList;
        this.userDAO = userDAO;
        this.productDAO = productDAO;
        this.orderDetailDAO = orderDetailDAO;
    }

    @NonNull
    @Override
    public OrderAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_admin, parent, false);
        return new OrderAdminVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderAdminVH holder, int position) {
        Order order = orderList.get(position);

        // Lấy email từ UserDAO
        String email = "";
        User user = userDAO.getUserById(order.getUserId());
        if (user != null) {
            email = user.getEmail();
        }

        // Lấy danh sách sản phẩm từ OrderDetailDAO + ProductDAO
        List<OrderDetail> details = orderDetailDAO.getDetailsByOrderId(order.getOrderId());
        StringBuilder description = new StringBuilder();

        // Biến để lưu sản phẩm đầu tiên
        Product firstProduct = null;

        for (int i = 0; i < details.size(); i++) {
            Product product = productDAO.getProductById(details.get(i).getProductId());
            if (product != null) {
                description.append(product.getProductName());
                if (i < details.size() - 1) description.append(", ");

                // Lấy sản phẩm đầu tiên
                if (i == 0) firstProduct = product;
            }
        }

        // Hiển thị
        holder.tvOrderId.setText(String.valueOf(order.getOrderId()));
        holder.tvEmail.setText("Người đặt: " + email);
        holder.tvTotalPrice.setText(String.valueOf(order.getTotalPrice()) + " VND");
        holder.tvDescription.setText(description.toString());

        // Hiển thị ảnh sản phẩm đầu tiên (nếu có)
        // Load ảnh sản phẩm đầu tiên bằng Glide
        Glide.with(holder.imgProduct.getContext()).clear(holder.imgProduct); // clear request cũ
        if (firstProduct != null) {
            int imageResId = firstProduct.getImageResId();
            String imagePath = firstProduct.getImagePath();

            if (imageResId != 0) {
                Glide.with(holder.imgProduct.getContext())
                        .load(imageResId)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.imgProduct);
            } else if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(holder.imgProduct.getContext())
                        .load(imagePath)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.imgProduct);
            } else {
                holder.imgProduct.setImageResource(R.drawable.ic_broken_image);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_broken_image);
        }

        holder.btnOrderTracking.setOnClickListener(v -> {
            if (listener != null) listener.onTrack(order);
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Order> newOrders) {
        orderList.clear();
        orderList.addAll(newOrders);
        notifyDataSetChanged();
    }
}
