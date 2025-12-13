package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

public class OrderDetailVH extends RecyclerView.ViewHolder {
    public ImageView ivProduct;
    public TextView tvProductName, tvQuantity, tvDescription, tvTotalPrice;

    public OrderDetailVH(@NonNull View itemView) {
        super(itemView);
        ivProduct = itemView.findViewById(R.id.ivProduct);
        tvProductName = itemView.findViewById(R.id.tvProductName);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
    }
}
