package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

// ViewHolder dùng cho RecyclerView để hiển thị từng item Category trong giao diện admin
public class CategoryAdminVH extends RecyclerView.ViewHolder {
    public TextView tvNameCategory;
    public ImageView ivEditCategory, ivDeleteCategory;

    // Constructor nhận vào view của từng item trong RecyclerView
    public CategoryAdminVH(@NonNull View itemView) {
        super(itemView);

        tvNameCategory = itemView.findViewById(R.id.tvNameCategoryAdmin);
        ivEditCategory = itemView.findViewById(R.id.ivEditCategoryAdmin);
        ivDeleteCategory = itemView.findViewById(R.id.ivDeleteCategoryAdmin);

    }
}
