package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
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
import com.sinhviencafemanagement.adapters.admin.adapter.ProductAdminAdapter;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdminFragment extends Fragment {

    // Adapter kết nối dữ liệu product với RecyclerView
    private ProductAdminAdapter adapterProductAdmin;
    // DAO để truy xuất dữ liệu từ SQLite
    private ProductDAO productDAO;

    private List<Product> productList = new ArrayList<>();
    private final List<Product> displayedProducts = new ArrayList<>();
    private String currentKeyword = ""; // để giữ keyword khi update list

    public ProductAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo DAO
        productDAO = new ProductDAO(getContext());
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

        // RecyclerView hiển thị danh sách product
        RecyclerView rvProductAdmin = view.findViewById(R.id.rvProductAdmin);
        rvProductAdmin.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter 1 lần, dùng displayedProducts
        adapterProductAdmin = new ProductAdminAdapter(getContext(), displayedProducts);
        rvProductAdmin.setAdapter(adapterProductAdmin);

        // Thiết lập callback cho edit/delete
        adapterProductAdmin.setOnProductActionListener(new ProductAdminAdapter.OnProductActionListener() {
            @Override
            public void onEdit(Product product) {
                Intent intent = new Intent(getContext(), UpdateProductActivity.class);
                intent.putExtra("product", product);
                ((AdminHomeActivity) requireActivity()).productLauncher.launch(intent);
            }

            @Override
            public void onDelete(Product product) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa \"" + product.getProductName() + "\" không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            new Thread(() -> {
                                productDAO.deleteProduct(product.getProductId());

                                requireActivity().runOnUiThread(() -> {
                                    // Xóa khỏi danh sách gốc
                                    productList.removeIf(p -> p.getProductId() == product.getProductId());

                                    // Xóa khỏi danh sách hiển thị
                                    int pos = -1;
                                    for (int i = 0; i < displayedProducts.size(); i++) {
                                        if (displayedProducts.get(i).getProductId() == product.getProductId()) {
                                            pos = i;
                                            break;
                                        }
                                    }
                                    if (pos >= 0) {
                                        displayedProducts.remove(pos);
                                        adapterProductAdmin.notifyItemRemoved(pos);
                                    }
                                });
                            }).start();
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        // Load dữ liệu product từ database
        loadProductsAsync();
    }

    // Thêm product mới vào RecyclerView
    public void addProductAdmin(Product newProduct) {
        productList.add(newProduct);

        if (newProduct.getProductName().toLowerCase().contains(currentKeyword.toLowerCase())) {
            displayedProducts.add(newProduct);
            adapterProductAdmin.notifyItemInserted(displayedProducts.size() - 1);
        }
    }

    // Cập nhật product đã edit trong RecyclerView
    public void updateProductAdmin(Product updatedProduct) {
        // cập nhật productList (danh sách gốc)
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getProductId() == updatedProduct.getProductId()) {
                productList.set(i, updatedProduct);
                break;
            }
        }

        // Kiểm tra sản phẩm có khớp từ khóa tìm kiếm không
        boolean match = updatedProduct.getProductName().toLowerCase()
                .contains(currentKeyword.toLowerCase());

        // Tìm sản phẩm này trong displayedProducts (danh sách đang hiển thị)
        int posInDisplayed = -1;
        for (int i = 0; i < displayedProducts.size(); i++) {
            if (displayedProducts.get(i).getProductId() == updatedProduct.getProductId()) {
                posInDisplayed = i;
                break;
            }
        }

        // Nếu sản phẩm phù hợp từ khóa tìm kiếm
        if (match) {
            // Có trong danh sách hiển thị -> gọi notifyItemChanged để RecyclerView load lại item đó
            if (posInDisplayed >= 0) {
                displayedProducts.set(posInDisplayed, updatedProduct);
                adapterProductAdmin.notifyItemChanged(posInDisplayed);
            }
            // Sản phẩm chưa có trong danh sách hiển (Giờ đổi tên lại khớp từ khóa)
            else {
                displayedProducts.add(updatedProduct);
                adapterProductAdmin.notifyItemInserted(displayedProducts.size() - 1);
            }
        }
        // Nếu sản phẩm sau khi đổi ko khớp từ khóa tìm kiếm
        else {
            if (posInDisplayed >= 0) {
                displayedProducts.remove(posInDisplayed);
                adapterProductAdmin.notifyItemRemoved(posInDisplayed);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadProductsAsync() {
        new Thread(() -> {
            // Lấy danh sách từ DB trên background thread
            productList = productDAO.getAllProducts();

            requireActivity().runOnUiThread(() -> {
                // danh sách hiển thị từ danh sách gốc
                displayedProducts.clear();
                displayedProducts.addAll(productList);
                adapterProductAdmin.notifyDataSetChanged();
            });
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterProduct(String keyword) {
        currentKeyword = keyword;
        displayedProducts.clear();

        if (keyword == null || keyword.trim().isEmpty()) {
            displayedProducts.addAll(productList);
        } else {
            for (Product p : productList) {
                if (p.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                    displayedProducts.add(p);
                }
            }
        }

        adapterProductAdmin.notifyDataSetChanged();
    }

}
