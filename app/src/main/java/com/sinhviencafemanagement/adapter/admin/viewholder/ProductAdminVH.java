package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

public class ProductAdminVH extends RecyclerView.ViewHolder {
    public ImageView imgProduct, ivEdit, ivDelete;

    public TextView tvProductName, tvProductPrice, tvProductCategory, tvProductStatus;


    public ProductAdminVH(@NonNull View itemView) {
        super(itemView);

        imgProduct = itemView.findViewById(R.id.imgProductAdmin);
        ivEdit = itemView.findViewById(R.id.ivEditProductAdmin);
        ivDelete = itemView.findViewById(R.id.ivDeleteProductAdmin);

        tvProductName = itemView.findViewById(R.id.tvProductNameAdmin);
        tvProductPrice = itemView.findViewById(R.id.tvProductPriceAdmin);
        tvProductCategory = itemView.findViewById(R.id.tvProductCategoryAdmin);
        tvProductStatus = itemView.findViewById(R.id.tvProductStatusAdmin);

    }
}
