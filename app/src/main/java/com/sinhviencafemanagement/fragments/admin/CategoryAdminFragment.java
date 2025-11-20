package com.sinhviencafemanagement.fragments.admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.CategoryAdminAdapter;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdminFragment extends Fragment {
    // RecyclerView hiển thị danh sách category
    private RecyclerView rvCategoryAdmin;
    // Adapter kết nối dữ liệu category với RecyclerView
    private CategoryAdminAdapter categoryAdminAdapter;
    // DAO để truy xuất dữ liệu từ SQLite
    private CategoryDAO categoryDAO;
    // Danh sách Category
    private List<Category> categoryList = new ArrayList<>();

    public CategoryAdminFragment() {

    }

    // Gọi khi Fragment được tạo (trước khi tạo view)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Gọi để tạo giao diện của Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout fragment_category_admin.xml thành View
        View view = inflater.inflate(R.layout.fragment_category_admin, container, false);
        // Lấy RecyclerView từ layout
        rvCategoryAdmin = view.findViewById(R.id.rvCategoryAdmin);

        // Khởi tạo DAO
        categoryDAO = new CategoryDAO(getContext());

        // Load danh sách category từ SQLite và gán vào RecyclerView
        loadCategories();
        // Trả về View đã inflate để hiển thị
        return view;
    }

    // Hàm load danh sách category từ database và hiển thị lên RecyclerView
    public void loadCategories() {
        categoryList = categoryDAO.getAllCategories();
        // Tạo Adapter với danh sách category vừa lấy
        categoryAdminAdapter = new CategoryAdminAdapter(getContext(), categoryList);
        // Set LayoutManager cho RecyclerView (dạng danh sách dọc)
        rvCategoryAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        // Gán Adapter cho RecyclerView
        rvCategoryAdmin.setAdapter(categoryAdminAdapter);

    }

}