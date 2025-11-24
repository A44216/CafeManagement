package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

// Fragment hiển thị danh sách Category cho Admin
public class CategoryAdminFragment extends Fragment {
    private RecyclerView rvCategoryAdmin; // RecyclerView hiển thị danh sách
    private CategoryDAO categoryDAO;       // DAO truy xuất database
    private CategoryAdminAdapter adapterCategoryAdmin;  // Adapter kết nối dữ liệu với RecyclerView

    private List<Category> categoryList;       // danh sách gốc
    private List<Category> displayedCategories; // danh sách hiển thị, dùng cho RecyclerView
    private String currentKeyword = "";

    public CategoryAdminFragment() { }

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

        loadCategoriesAdmin(); // Load dữ liệu từ DB
    }

    // Load dữ liệu từ database và set Adapter
    public void loadCategoriesAdmin() {
        categoryList = categoryDAO.getAllCategories(); // Lấy danh sách từ DB
        displayedCategories = new ArrayList<>(categoryList);    // danh sách hiển thị

        adapterCategoryAdmin = new CategoryAdminAdapter(getContext(), displayedCategories); // Tạo Adapter
        rvCategoryAdmin.setAdapter(adapterCategoryAdmin); // Gán Adapter cho RecyclerView

        // Thiết lập callback cho edit/delete
        adapterCategoryAdmin.setOnCategoryActionListener(new CategoryAdminAdapter.OnCategoryActionListener() {
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
                            int pos = displayedCategories.indexOf(category);
                            if (pos >= 0) {
                                new Thread(() -> {
                                    // Xóa DB trên background thread
                                    categoryDAO.deleteCategory(category.getCategoryId());
                                    // Cập nhật UI trên main thread
                                    requireActivity().runOnUiThread(() -> {
                                        categoryList.remove(category);       // xóa khỏi danh sách gốc
                                        displayedCategories.remove(pos);     // xóa khỏi danh sách hiển thị
                                        adapterCategoryAdmin.notifyItemRemoved(pos);
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
    public void addCategoryAdmin(Category newCategory) {
        categoryList.add(newCategory);
        if (newCategory.getCategoryName().toLowerCase().contains(currentKeyword.toLowerCase())) {
            displayedCategories.add(newCategory);
            adapterCategoryAdmin.notifyItemInserted(displayedCategories.size() - 1);
        }
    }

    // Cập nhật category đã edit trong RecyclerView
    public void updateCategoryAdmin(Category updatedCategory) {
        // Cập nhật danh sách gốc
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getCategoryId() == updatedCategory.getCategoryId()) {
                categoryList.set(i, updatedCategory);
                break;
            }
        }
        // Kiểm tra xem item có match filter hiện tại không
        boolean match = updatedCategory.getCategoryName().toLowerCase()
                .contains(currentKeyword.toLowerCase());

        int posInDisplayed = -1;
        for (int i = 0; i < displayedCategories.size(); i++) {
            if (displayedCategories.get(i).getCategoryId() == updatedCategory.getCategoryId()) {
                posInDisplayed = i;
                break;
            }
        }

        if (match) {
            if (posInDisplayed >= 0) {
                // Update trực tiếp
                displayedCategories.set(posInDisplayed, updatedCategory);
                adapterCategoryAdmin.notifyItemChanged(posInDisplayed);
            } else {
                // Thêm mới vì trước đó không match filter
                displayedCategories.add(updatedCategory);
                adapterCategoryAdmin.notifyItemInserted(displayedCategories.size() - 1);
            }
        } else {
            if (posInDisplayed >= 0) {
                // Xóa khỏi displayed vì không match filter nữa
                displayedCategories.remove(posInDisplayed);
                adapterCategoryAdmin.notifyItemRemoved(posInDisplayed);
            }
        }

    }

    // Lọc danh sách Category theo từ khóa tìm kiếm
    @SuppressLint("NotifyDataSetChanged")
    public void filterCategory(String keyword) {
        currentKeyword = keyword; // lưu keyword hiện tại
        displayedCategories.clear();
        if (keyword.isEmpty()) {
            displayedCategories.addAll(categoryList); // categoryList là danh sách gốc
        } else {
            for (Category c : categoryList) {
                if (c.getCategoryName().toLowerCase().contains(keyword.toLowerCase())) {
                    displayedCategories.add(c);
                }
            }
        }
        adapterCategoryAdmin.notifyDataSetChanged();
    }

}