package com.sinhviencafemanagement.fragments.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.ProductAdminAdapter;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdminFragment extends Fragment {

    // RecyclerView hiển thị danh sách product
    private RecyclerView rvProductAdmin;
    // Adapter kết nối dữ liệu product với RecyclerView
    private ProductAdminAdapter productAdminAdapter;
    // DAO để truy xuất dữ liệu từ SQLite
    private ProductDAO productDAO;

    private List<Product> productList = new ArrayList<>();

    public ProductAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_admin, container, false);
    }
}