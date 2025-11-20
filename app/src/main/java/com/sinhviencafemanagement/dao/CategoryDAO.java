package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final SQLiteDatabase db; // Đối tượng SQLite để thao tác dữ liệu

    // Constructor mở database
    public CategoryDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    // Lấy tất cả category
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_CATEGORIES,
                null,
                null, null,
                null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_CATEGORY_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_CATEGORY_NAME))
                    );
                    categoryList.add(category);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("CategoryDAO", "Lỗi khi lấy danh sách category", e);
        }

        return categoryList;
    }

    // Thêm category mới
    public long addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            Log.e("CategoryDAO", "Tên category không hợp lệ");
            return -1;
        }

        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_CATEGORY_NAME, name.trim());
            result = db.insert(CreateDatabase.TABLE_CATEGORIES, null, values);
        } catch (Exception e) {
            Log.e("CategoryDAO", "Lỗi khi thêm category", e);
        }

        return result;
    }

    public long addCategory(Category category) {
        if (category == null) return -1;
        return addCategory(category.getCategoryName());
    }

    // Cập nhật category
    public int updateCategory(int categoryId, String newName) {
        if (categoryId <= 0) {
            Log.e("CategoryDAO", "categoryId không hợp lệ: " + categoryId);
            return 0;
        }

        ContentValues values = new ContentValues();
        if (newName != null && !newName.trim().isEmpty()) {
            values.put(CreateDatabase.COLUMN_CATEGORY_NAME, newName.trim());
        }

        if (values.size() == 0) return 0;

        int result = 0;
        try {
            result = db.update(
                    CreateDatabase.TABLE_CATEGORIES,
                    values,
                    CreateDatabase.COLUMN_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)}
            );
        } catch (Exception e) {
            Log.e("CategoryDAO", "Lỗi khi cập nhật category", e);
        }

        return result;
    }

    public int updateCategory(Category category) {
        if (category == null) return 0;
        return updateCategory(category.getCategoryId(), category.getCategoryName());
    }

    // Xóa category
    public int deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            Log.e("CategoryDAO", "categoryId không hợp lệ: " + categoryId);
            return 0;
        }

        int result = 0;

        try {
            result = db.delete(
                    CreateDatabase.TABLE_CATEGORIES,
                    CreateDatabase.COLUMN_CATEGORY_ID + " = ?",
                    new String[]{String.valueOf(categoryId)}
            );
        } catch (Exception e) {
            Log.e("CategoryDAO", "Lỗi khi xóa category", e);
        }

        return result;
    }

}
