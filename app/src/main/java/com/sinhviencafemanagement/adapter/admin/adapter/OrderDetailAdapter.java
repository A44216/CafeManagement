package com.sinhviencafemanagement.adapter.admin.adapter;

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
import com.sinhviencafemanagement.adapter.admin.viewholder.OrderDetailVH;
import com.sinhviencafemanagement.dao.OrderDetailToppingDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.models.OrderDetailTopping;
import com.sinhviencafemanagement.models.Product;
import com.sinhviencafemanagement.models.Topping;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailVH> {

    private final Context context;
    private final List<OrderDetail> details;
    private final ProductDAO productDAO;
    private final OrderDetailToppingDAO odtDAO;
    private final ToppingDAO toppingDAO;

    public OrderDetailAdapter(Context context, List<OrderDetail> details,
                              ProductDAO productDAO, OrderDetailToppingDAO odtDAO, ToppingDAO toppingDAO) {
        this.context = context;
        this.details = details;
        this.productDAO = productDAO;
        this.odtDAO = odtDAO;
        this.toppingDAO = toppingDAO;
    }

    @NonNull
    @Override
    public OrderDetailVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_tracking, parent, false);
        return new OrderDetailVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderDetailVH holder, int position) {
        OrderDetail detail = details.get(position);
        Product product = productDAO.getProductById(detail.getProductId());

        if (product != null) {
            // Clear request Glide cũ để tránh hiển thị nhầm ảnh
            Glide.with(context).clear(holder.ivProduct);
            holder.ivProduct.setImageResource(R.drawable.ic_broken_image);

            // Load ảnh sản phẩm
            int imageResId = product.getImageResId();
            String imagePath = product.getImagePath();

            if (imageResId != 0) {
                Glide.with(context)
                        .load(imageResId)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.ivProduct);
            } else if (imagePath != null && !imagePath.isEmpty()) {
                Glide.with(context)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_broken_image)
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.ivProduct);
            } else {
                holder.ivProduct.setImageResource(R.drawable.ic_broken_image);
            }

            // Lấy topping
            List<OrderDetailTopping> odtList = odtDAO.getToppingsByOrderAndProduct(detail.getOrderId(), detail.getProductId());
            StringBuilder toppingNames = new StringBuilder();
            double toppingPriceTotal = 0;
            for (int i = 0; i < odtList.size(); i++) {
                OrderDetailTopping odt = odtList.get(i);
                Topping topping = toppingDAO.getToppingById(odt.getToppingId());
                if (topping != null) {
                    toppingNames.append(topping.getToppingName());
                    toppingPriceTotal += topping.getPrice() * odt.getQuantity();
                    if (i < odtList.size() - 1) toppingNames.append(", ");
                }
            }

            // Set tên sản phẩm
            holder.tvProductName.setText(product.getProductName() != null ? product.getProductName() : "Không có tên");
            // Set số lượng
            holder.tvQuantity.setText("Số lượng: " + detail.getQuantity());

            holder.tvDescription.setText(toppingNames.length() > 0 ? toppingNames.toString() : "Không có topping");

            // Tổng tiền
            double totalPrice = product.getPrice() * detail.getQuantity() + toppingPriceTotal;
            holder.tvTotalPrice.setText(totalPrice + " vnd");
        }
    }

    @Override
    public int getItemCount() {
        return details.size();
    }
}
