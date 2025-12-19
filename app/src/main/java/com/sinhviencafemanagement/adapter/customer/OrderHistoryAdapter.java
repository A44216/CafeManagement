package com.sinhviencafemanagement.adapter.customer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.order.TrackOrderActivity;
import com.sinhviencafemanagement.dao.OrderDetailDAO;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.Order;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OrderDetailDAO orderDetailDAO;
    private ProductDAO productDAO;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.orderDetailDAO = new OrderDetailDAO(context);
        this.productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_history_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        holder.tvOrderId.setText("Mã đơn: " + order.getOrderId());
        holder.tvPrice.setText(formatter.format(order.getTotalPrice()) + "vnd");

        // 1. Xử lý hiển thị Ảnh và Tên sản phẩm tóm tắt
        List<OrderDetail> details = orderDetailDAO.getDetailsByOrderId(order.getOrderId());

        if (details != null && !details.isEmpty()) {
            holder.tvQuantity.setText("(" + details.size() + " món)");

            // Lấy thông tin sản phẩm đầu tiên để hiển thị đại diện
            int firstProductId = details.get(0).getProductId();
            Product product = productDAO.getProductById(firstProductId);

            if (product != null) {
                // Hiển thị tên sản phẩm đại diện và "..." nếu có nhiều món
                String summary = product.getProductName();
                if (details.size() > 1) {
                    summary += ", " + (details.size() - 1) + " món khác...";
                }
                holder.tvDescription.setText(summary);

                // Xử lý hiển thị ảnh
                if (product.getImageResId() != 0) {
                    // Ưu tiên ảnh từ Resource drawable (dữ liệu mặc định)
                    holder.imgProduct.setImageResource(product.getImageResId());
                } else if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                    // Nếu là ảnh từ Gallery (admin thêm)
                    holder.imgProduct.setImageURI(Uri.parse(product.getImagePath()));
                } else {
                    // Ảnh mặc định nếu không có dữ liệu
                    holder.imgProduct.setImageResource(R.drawable.ic_launcher_background);
                }
            }
        }

        // 2. Xử lý logic hiển thị nút "Theo dõi đơn hàng"
        // Chỉ hiện ở Tab Đang xử lý (pending)
        if (CreateDatabase.ORDER_STATUS_COMPLETED.equals(order.getStatus())) {
            holder.btnTrackOrder.setVisibility(View.GONE);
        } else {
            holder.btnTrackOrder.setVisibility(View.VISIBLE);
        }

        // Sự kiện click chuyển sang màn hình theo dõi
        holder.btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackOrderActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvOrderId, tvPrice, tvDescription, tvQuantity;
        LinearLayout btnTrackOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
        }
    }
}