package com.sinhviencafemanagement.fragments.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.AdminHomeActivity;
import com.sinhviencafemanagement.activities.home.product.UpdateProductActivity;
import com.sinhviencafemanagement.adapter.admin.ProductAdminAdapter;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdminFragment extends Fragment {

    // RecyclerView hiển thị danh sách product
    private RecyclerView rvProductAdmin;
    // Adapter kết nối dữ liệu product với RecyclerView
    private ProductAdminAdapter adapterProductAdmin;
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

    // Khi view đã tạo xong → init RecyclerView và load dữ liệu
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProductAdmin = view.findViewById(R.id.rvProductAdmin);
        rvProductAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo DAO
        productDAO = new ProductDAO(getContext());

        loadProductsAsync();
    }

    private void loadProducts() {
        productList = productDAO.getAllProducts(); // Lấy danh sách từ DB
        adapterProductAdmin = new ProductAdminAdapter(getContext(), productList);
        rvProductAdmin.setAdapter(adapterProductAdmin);

        // Thiết lập callback cho edit/delete
        adapterProductAdmin.setOnProductActionListener(new ProductAdminAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                // Mở Activity update product
                Intent intent = new Intent(getContext(), UpdateProductActivity.class);

                ((AdminHomeActivity) requireActivity()).productLauncher.launch(intent);
            }

            @Override
            public void onDelete(Product product) {
                // Dialog xác nhận xóa
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa \"" + product.getProductName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            int pos = productList.indexOf(product);
                            if (pos >= 0) {
                                new Thread(() -> {
                                    // Xóa DB trên background thread
                                    productDAO.deleteProduct(product.getProductId());
                                    // Cập nhật UI trên main thread
                                    requireActivity().runOnUiThread(() -> {
                                        productList.remove(pos);
                                        adapterProductAdmin.notifyItemRemoved(pos);
                                    });
                                }).start(); // Thông báo RecyclerView rằng 1 item đã bị xóa
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

    }

    // Thêm product mới vào RecyclerView
    public void addProductAdmin(Product newProduct) {
        productList.add(newProduct);
        adapterProductAdmin.notifyItemInserted(productList.size() - 1);
    }

    // Cập nhật product đã edit trong RecyclerView
    public void updateProductAdmin(Product updatedProduct) {
        for (int i = 0; i < productList.size(); i++) {
            if(productList.get(i).getProductId() == updatedProduct.getProductId()) {
                productList.set(i, updatedProduct);
                adapterProductAdmin.notifyItemChanged(i);
                break;
            }
        }
    }

    private void loadProductsAsync() {
        new Thread(() -> {
            // Lấy danh sách từ DB (background)
            List<Product> list = productDAO.getAllProducts();

            // Cập nhật UI (main thread)
            requireActivity().runOnUiThread(() -> {
                productList = list;
                adapterProductAdmin = new ProductAdminAdapter(getContext(), productList);
                rvProductAdmin.setAdapter(adapterProductAdmin);

                adapterProductAdmin.setOnProductActionListener(new ProductAdminAdapter.OnProductActionListener() {
                    @Override
                    public void onEdit(Product product) {
                        Intent intent = new Intent(getContext(), UpdateProductActivity.class);
                        ((AdminHomeActivity) requireActivity()).productLauncher.launch(intent);
                    }

                    @Override
                    public void onDelete(Product product) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc muốn xóa \"" + product.getProductName() + "\" không?")
                                .setPositiveButton("Xóa", (dialog, which) -> {
                                    int pos = productList.indexOf(product);
                                    if (pos >= 0) {
                                        new Thread(() -> {
                                            productDAO.deleteProduct(product.getProductId());
                                            requireActivity().runOnUiThread(() -> {
                                                productList.remove(pos);
                                                adapterProductAdmin.notifyItemRemoved(pos);
                                            });
                                        }).start();
                                    }
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                });
            });
        }).start();
    }


}