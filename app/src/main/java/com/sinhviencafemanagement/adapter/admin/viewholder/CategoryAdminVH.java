package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

// ViewHolder giữ các view trong item RecyclerView
public class CategoryAdminVH extends RecyclerView.ViewHolder {
    public TextView tvCategoryName;
    public ImageView ivEditCategory, ivDeleteCategory;

    // Constructor nhận vào view của từng item trong RecyclerView
    public CategoryAdminVH(@NonNull View itemView) {
        super(itemView);

        tvCategoryName = itemView.findViewById(R.id.tvCategoryNameAdmin);
        ivEditCategory = itemView.findViewById(R.id.ivEditCategoryAdmin);
        ivDeleteCategory = itemView.findViewById(R.id.ivDeleteCategoryAdmin);

    }
}
