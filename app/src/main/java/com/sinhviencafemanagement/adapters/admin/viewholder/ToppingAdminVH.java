package com.sinhviencafemanagement.adapters.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

public class ToppingAdminVH extends RecyclerView.ViewHolder {

    public TextView tvName, tvPrice;
    public ImageView ivEdit, ivDelete;

    public ToppingAdminVH(View itemView) {
        super(itemView);
        tvName = itemView.findViewById(R.id.tvToppingNameAdmin);
        tvPrice = itemView.findViewById(R.id.tvToppingPriceAdmin);
        ivEdit = itemView.findViewById(R.id.ivEditToppingAdmin);
        ivDelete = itemView.findViewById(R.id.ivDeleteToppingAdmin);
    }
}
