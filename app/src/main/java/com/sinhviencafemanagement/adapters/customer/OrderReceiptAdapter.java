package com.sinhviencafemanagement.adapters.customer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.OrderDetail;
import com.sinhviencafemanagement.models.Product;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderReceiptAdapter extends RecyclerView.Adapter<OrderReceiptAdapter.ViewHolder> {

    private Context context;
    private List<OrderDetail> orderDetailList;
    private ProductDAO productDAO;

    public OrderReceiptAdapter(Context context, List<OrderDetail> orderDetailList) {
        this.context = context;
        this.orderDetailList = orderDetailList;
        this.productDAO = new ProductDAO(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetail detail = orderDetailList.get(position);

        // Lấy thông tin sản phẩm từ ProductDAO
        Product product = productDAO.getProductById(detail.getProductId());
        Object imageSource = product.getImageForCart();

        if (imageSource instanceof String) {
            // Trường hợp 1: Nguồn ảnh là đường dẫn file (String)
            File imgFile = new File((String) imageSource);
            if (imgFile.exists()) {
                // Giải mã file thành Bitmap và hiển thị
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgProduct.setImageBitmap(myBitmap);
            }
        } else if (imageSource instanceof Integer) {
            // Trường hợp 2: Nguồn ảnh là ID tài nguyên (Integer)
            holder.imgProduct.setImageResource((Integer) imageSource);
        }

        if (product != null) {
            holder.tvProductName.setText(product.getProductName());

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            holder.tvProductPrice.setText(formatter.format(product.getPrice()) + "đ");
            holder.tvProductQuantity.setText("x" + detail.getQuantity());

            // Nếu bạn muốn hiển thị Topping, bạn có thể lấy từ OrderDetailToppingDAO tại đây
            holder.tvProductOptions.setText("Đã xác nhận");
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailList != null ? orderDetailList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvProductOptions, tvProductQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductReceipt);
            tvProductName = itemView.findViewById(R.id.tvProductNameReceipt);
            tvProductPrice = itemView.findViewById(R.id.tvProductPriceReceipt);
            tvProductOptions = itemView.findViewById(R.id.tvProductOptionsReceipt);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantityReceipt);
        }
    }
}