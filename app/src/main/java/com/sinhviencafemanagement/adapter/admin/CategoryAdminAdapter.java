package com.sinhviencafemanagement.adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.CategoryAdminVH;
import com.sinhviencafemanagement.models.Category;

import java.util.List;

// Adapter RecyclerView hiển thị danh sách Category
public class CategoryAdminAdapter extends RecyclerView.Adapter<CategoryAdminVH> {

    private final List<Category> categoryList; // Danh sách Category
    private final Context context; // Context để inflate layout

    // Interface callback cho Edit/Delete
    public interface OnCategoryActionListener {
        void onEdit(Category category);
        void onDelete(Category category);
    }

    private OnCategoryActionListener listener; // Biến listener

    // Setter listener
    public void setOnCategoryActionListener(OnCategoryActionListener listener) {
        this.listener = listener;
    }

    public CategoryAdminAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }


    // Tạo ViewHolder mới khi RecyclerView cần hiển thị 1 item
    @NonNull
    @Override
    public CategoryAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_category_admin.xml thành View
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_admin, parent, false);
        // Tạo ViewHolder và trả về
        return new CategoryAdminVH(view);
    }

    // Bind dữ liệu của category vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CategoryAdminVH holder, int position) {
        // Lấy dữ liệu category tại vị trí position
        Category category = categoryList.get(position);
        // Gán tên category vào TextView trong ViewHolder
        holder.tvCategoryName.setText(category.getCategoryName());

        // Click Edit
        holder.ivEditCategory.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(category);
        });

        // Click Delete
        holder.ivDeleteCategory.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(category);
        });
    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
