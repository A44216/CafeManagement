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
    private CategoryDAO categoryDAO;       // DAO truy xuất database
    private CategoryAdminAdapter adapterCategoryAdmin;  // Adapter kết nối dữ liệu với RecyclerView

    private List<Category> categoryList = new ArrayList<>();       // danh sách gốc
    private final List<Category> displayedCategories = new ArrayList<>(); // danh sách hiển thị, dùng cho RecyclerView
    private String currentKeyword = ""; // để giữ keyword khi update list

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

        // RecyclerView hiển thị danh sách
        RecyclerView rvCategoryAdmin = view.findViewById(R.id.rvCategoryAdmin);
        rvCategoryAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter 1 lần, dùng displayedCategory
        adapterCategoryAdmin = new CategoryAdminAdapter(getContext(), displayedCategories);
        rvCategoryAdmin.setAdapter(adapterCategoryAdmin);

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

        loadCategoriesAdmin(); // Load dữ liệu từ DB
    }

    // Load dữ liệu từ database và set Adapter
    @SuppressLint("NotifyDataSetChanged")
    public void loadCategoriesAdmin() {
        new Thread(() -> {
            // Lấy dữ liệu category từ database ở background thread
            categoryList = categoryDAO.getAllCategories();

            requireActivity().runOnUiThread(() -> {
                // Cập nhật RecyclerView trên main thread
                displayedCategories.clear();           // Xóa dữ liệu cũ
                displayedCategories.addAll(categoryList); // Thêm dữ liệu mới
                adapterCategoryAdmin.notifyDataSetChanged(); // Reload RecyclerView
            });
        }).start();
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
        // Cập nhật lại danh sách gốc (categoryList)
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getCategoryId() == updatedCategory.getCategoryId()) {
                categoryList.set(i, updatedCategory);
                break;
            }
        }
        // Kiểm tra xem category vừa update có còn phù hợp với filter đang áp dụng hay không
        boolean match = updatedCategory.getCategoryName().toLowerCase()
                .contains(currentKeyword.toLowerCase());

        // Tìm vị trí của category này trong danh sách đang hiển thị (displayedCategories)
        int posInDisplayed = -1;
        for (int i = 0; i < displayedCategories.size(); i++) {
            if (displayedCategories.get(i).getCategoryId() == updatedCategory.getCategoryId()) {
                posInDisplayed = i;
                break;
            }
        }

        // Nếu category phù hợp từ khóa tìm kiếm
        if (match) {
            // Nếu có trong danh sách hiển thị -> gọi notifyItemChanged để RecyclerView load lại item đó
            if (posInDisplayed >= 0) {
                displayedCategories.set(posInDisplayed, updatedCategory);
                adapterCategoryAdmin.notifyItemChanged(posInDisplayed);
            }
            // Nếu danh mục chưa có trong danh sách hiển (Giờ đổi tên lại khớp từ khóa)
            else {
                displayedCategories.add(updatedCategory);
                adapterCategoryAdmin.notifyItemInserted(displayedCategories.size() - 1);
            }
        }
        // Nếu sản phẩm sau khi đổi ko khớp từ khóa tìm kiếm
        else {
            if (posInDisplayed >= 0) {
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