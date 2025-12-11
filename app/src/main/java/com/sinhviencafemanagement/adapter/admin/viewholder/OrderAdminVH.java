package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

public class OrderAdminVH extends RecyclerView.ViewHolder {
    public ImageView imgProduct;
    public TextView tvEmail, tvOrderId, tvTotalPrice, tvDescription, btnOrderTracking;

    // Constructor nhận vào view của từng item trong RecyclerView
    public OrderAdminVH(@NonNull View itemView) {
        super(itemView);

        imgProduct = itemView.findViewById(R.id.imgProduct);
        tvEmail = itemView.findViewById(R.id.tvEmail);
        tvOrderId = itemView.findViewById(R.id.tvOrderId);
        tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        tvDescription = itemView.findViewById(R.id.tvDescription);
        btnOrderTracking = itemView.findViewById(R.id.btnOrderTracking);

    }


}
