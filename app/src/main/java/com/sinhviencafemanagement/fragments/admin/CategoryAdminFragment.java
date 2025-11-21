package com.sinhviencafemanagement.fragments.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.AdminHomeActivity;
import com.sinhviencafemanagement.activities.home.category.UpdateCategoryActivity;
import com.sinhviencafemanagement.adapter.admin.CategoryAdminAdapter;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import java.util.List;

// Fragment hiển thị danh sách Category cho Admin
public class CategoryAdminFragment extends Fragment {
    private RecyclerView rvCategoryAdmin; // RecyclerView hiển thị danh sách
    private CategoryDAO categoryDAO;       // DAO truy xuất database
    private CategoryAdminAdapter adapter;  // Adapter kết nối dữ liệu với RecyclerView

    private List<Category> categoryList;

    public CategoryAdminFragment() {

    }

    // Gọi khi Fragment được tạo (trước khi tạo view)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo DAO
        categoryDAO = new CategoryDAO(getContext());

    }

    // Tạo giao diện (inflate layout) của Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment_category_admin.xml thành View và trả về
        return inflater.inflate(R.layout.fragment_category_admin, container, false);
    }

    // View đã tạo xong → thao tác UI tại đây
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCategoryAdmin = view.findViewById(R.id.rvCategoryAdmin);
        rvCategoryAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        loadCategories(); // Load dữ liệu từ DB
    }

    // Load dữ liệu từ database và set Adapter
    public void loadCategories() {
        categoryList = categoryDAO.getAllCategories(); // Lấy danh sách từ DB

        adapter = new CategoryAdminAdapter(getContext(), categoryList); // Tạo Adapter
        rvCategoryAdmin.setAdapter(adapter); // Gán Adapter cho RecyclerView

        // Thiết lập callback cho edit/delete
        adapter.setOnCategoryActionListener(new CategoryAdminAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                // Mở Activity update category
                Intent intent = new Intent(getContext(), UpdateCategoryActivity.class);
                intent.putExtra("category_id", category.getCategoryId());
                intent.putExtra("category_name", category.getCategoryName());
                ((AdminHomeActivity) requireActivity()).categoryLauncher.launch(intent);
            }

            @Override
            public void onDelete(Category category) {
                // Dialog xác nhận xóa
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa danh mục \"" + category.getCategoryName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            int pos = categoryList.indexOf(category);
                            if (pos >= 0) {
                                new Thread(() -> {
                                    // Xóa DB trên background thread
                                    categoryDAO.deleteCategory(category.getCategoryId());
                                    // Cập nhật UI trên main thread
                                    requireActivity().runOnUiThread(() -> {
                                        categoryList.remove(pos);
                                        adapter.notifyItemRemoved(pos);
                                    });
                                }).start(); // Thông báo RecyclerView rằng 1 item đã bị xóa
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
    }

    // Thêm category mới vào RecyclerView
    public void addCategory(Category category) {
        categoryList.add(category);
        adapter.notifyItemInserted(categoryList.size() - 1);
    }

    // Cập nhật category đã edit trong RecyclerView
    public void updateCategory(Category updatedCategory) {
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getCategoryId() == updatedCategory.getCategoryId()) {
                categoryList.set(i, updatedCategory);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

}