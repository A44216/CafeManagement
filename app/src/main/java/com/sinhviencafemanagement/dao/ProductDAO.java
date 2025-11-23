package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private SQLiteDatabase db; // Đối tượng SQLite để thao tác dữ liệu

    // Constructor mở database
    public ProductDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_PRODUCTS,
                null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                productList.add(cursorToProduct(cursor));
            }
        } catch (Exception e) {
            Log.e("ProductDAO", "Error getting all products", e);
        }
        return productList;
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int productId) {
        Product product = null;
        try (Cursor cursor = db.query(CreateDatabase.TABLE_PRODUCTS,
                null,
                CreateDatabase.COLUMN_PRODUCT_ID + "=?",
                new String[]{String.valueOf(productId)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                product = cursorToProduct(cursor);
            }
        } catch (Exception e) {
            Log.e("ProductDAO", "Error getting product by ID", e);
        }
        return product;
    }

    @NonNull
    private ContentValues getProductValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_PRODUCT_NAME, product.getProductName());
        values.put(CreateDatabase.COLUMN_PRODUCT_PRICE, product.getPrice());
        values.put(CreateDatabase.COLUMN_PRODUCT_STATUS, product.getStatus());
        values.put(CreateDatabase.COLUMN_PRODUCT_IMAGE_RES_ID, product.getImageResId());
        values.put(CreateDatabase.COLUMN_PRODUCT_IMAGE_PATH, product.getImagePath());
        values.put(CreateDatabase.COLUMN_PRODUCT_CATEGORY_ID, product.getCategoryId());
        values.put(CreateDatabase.COLUMN_PRODUCT_DESCRIPTION, product.getDescription());
        return values;
    }

    // Thêm sản phẩm mới
    public long addProduct(Product product) {
        if (product == null) {
            Log.e("ProductDAO", "Sản phẩm không hợp lệ");
            return -1;
        }

        try {
            return db.insert(CreateDatabase.TABLE_PRODUCTS, null, getProductValues(product));
        } catch (Exception e) {
            Log.e("ProductDAO", "Lỗi khi thêm sản phẩm", e);
            return -1;
        }
    }

    // Cập nhật sản phẩm
    public int updateProduct(Product product) {
        if (product == null) {
            Log.e("ProductDAO", "Sản phẩm không hợp lệ");
            return -1;
        }

        try {
            return db.update(
                    CreateDatabase.TABLE_PRODUCTS,
                    getProductValues(product),
                    CreateDatabase.COLUMN_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(product.getProductId())}
            );
        } catch (Exception e) {
            Log.e("ProductDAO", "Lỗi khi cập nhật sản phẩm", e);
            return -1;
        }

    }

    // Xóa sản phẩm theo ID
    public int deleteProduct(int productId) {
        try {
            return db.delete(
                    CreateDatabase.TABLE_PRODUCTS,
                    CreateDatabase.COLUMN_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(productId)}
            );
        } catch (Exception e) {
            Log.e("ProductDAO", "Lỗi khi xóa sản phẩm", e);
            return -1;
        }
    }

    // Lấy danh sách sản phẩm theo category
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> productList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_PRODUCTS,
                null,
                CreateDatabase.COLUMN_PRODUCT_CATEGORY_ID + "=?",
                new String[]{String.valueOf(categoryId)},
                null, null, null)) {
            while (cursor.moveToNext()) {
                productList.add(cursorToProduct(cursor));
            }
        } catch (Exception e) {
            Log.e("ProductDAO", "Error getting products by category", e);
        }
        return productList;
    }

    // Tìm sản phẩm theo tên
    public List<Product> searchProductsByName(String name) {
        List<Product> productList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_PRODUCTS,
                null,
                CreateDatabase.COLUMN_PRODUCT_NAME + " LIKE ?",
                new String[]{"%" + name + "%"},
                null, null, null)) {
            while (cursor.moveToNext()) {
                productList.add(cursorToProduct(cursor));
            }
        } catch (Exception e) {
            Log.e("ProductDAO", "Error searching products by name", e);
        }
        return productList;
    }

    private Product cursorToProduct(Cursor cursor) {
        return new Product(
                cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_NAME)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_STATUS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_IMAGE_RES_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_IMAGE_PATH)),
                cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_PRODUCT_DESCRIPTION))
        );
    }

    public boolean nameExists(String name) {
        boolean exists = false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_PRODUCTS,
                new String[]{CreateDatabase.COLUMN_PRODUCT_ID},
                CreateDatabase.COLUMN_PRODUCT_NAME + " = ?",
                new String[]{name},
                null, null, null
        )) {
            exists = cursor.moveToFirst(); // Có bản ghi nghĩa là tồn tại
        } catch (Exception e) {
            Log.e("ProductDAO", "Lỗi khi kiểm tra tên", e);
        }

        return exists;
    }

}
