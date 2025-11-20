package com.sinhviencafemanagement.adapter.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.CategoryAdminVH;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import java.util.List;

public class CategoryAdminAdapter extends RecyclerView.Adapter<CategoryAdminVH> {

    private final List<Category> categoryList; // Danh sách Category
    private final Context context; // Context để inflate layout và dùng DAO

    public CategoryAdminAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    // Tạo ViewHolder mới khi RecyclerView cần hiển thị 1 item
    @NonNull
    @Override
    public CategoryAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_category_admin.xml thành View
        View view = LayoutInflater.from(context).inflate(R.layout.item_category_admin, parent, false);

        // Tạo ViewHolder và trả về
        return new CategoryAdminVH(view);
    }

    // Bind dữ liệu của category vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull CategoryAdminVH holder, int position) {
        // Lấy dữ liệu category tại vị trí position
        Category category = categoryList.get(position);
        // Gán tên category vào TextView trong ViewHolder
        holder.tvNameCategory.setText(category.getCategoryName());

        // Xử lý sự kiện Edit
        holder.ivEditCategory.setOnClickListener(v -> {

        });

        // Xử lý sự kiện Delete
        holder.ivDeleteCategory.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa danh mục \"" + category.getCategoryName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Thực hiện xóa
                    int result = new CategoryDAO(context).deleteCategory(category.getCategoryId());
                    if (result > 0) {
                        int pos = holder.getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            categoryList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        });

    }

    // Trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        return categoryList.size();
    }


}
