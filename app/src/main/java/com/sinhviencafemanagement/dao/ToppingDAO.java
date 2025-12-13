package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Topping;

import java.util.ArrayList;
import java.util.List;

public class ToppingDAO {
    private final SQLiteDatabase db;

    public ToppingDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    // Lấy tất cả topping
    public List<Topping> getAllToppings() {
        List<Topping> list = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_TOPPINGS, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Topping topping = new Topping(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_PRICE))
                    );
                    list.add(topping);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi lấy danh sách topping", e);
        }
        return list;
    }

    // Thêm topping
    public long addTopping(Topping topping) {
        if (topping == null) return -1;
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_TOPPING_NAME, topping.getToppingName());
        values.put(CreateDatabase.COLUMN_TOPPING_PRICE, topping.getPrice());
        long result = -1;
        try {
            result = db.insert(CreateDatabase.TABLE_TOPPINGS, null, values);
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi thêm topping", e);
        }
        return result;
    }

    // Kiểm tra topping đã tồn tại chưa
    public boolean toppingExists(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_TOPPINGS,
                new String[]{CreateDatabase.COLUMN_TOPPING_ID},
                CreateDatabase.COLUMN_TOPPING_NAME + " = ?",
                new String[]{name.trim()},
                null, null, null)) {
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi kiểm tra topping", e);
            return false;
        }
    }

    // Cập nhật topping
    public int updateTopping(Topping topping) {
        if (topping == null || topping.getToppingId() <= 0) return 0;
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_TOPPING_NAME, topping.getToppingName());
        values.put(CreateDatabase.COLUMN_TOPPING_PRICE, topping.getPrice());
        int result = 0;
        try {
            result = db.update(CreateDatabase.TABLE_TOPPINGS, values,
                    CreateDatabase.COLUMN_TOPPING_ID + " = ?",
                    new String[]{String.valueOf(topping.getToppingId())});
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi cập nhật topping", e);
        }
        return result;
    }

    // Xóa topping
    public void deleteTopping(int toppingId) {
        if (toppingId <= 0) return;
        int result = 0;
        try {
            result = db.delete(CreateDatabase.TABLE_TOPPINGS,
                    CreateDatabase.COLUMN_TOPPING_ID + " = ?",
                    new String[]{String.valueOf(toppingId)});
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi xóa topping", e);
        }
    }

    // Lấy topping theo ID
    public Topping getToppingById(int id) {
        if (id <= 0) return null;
        Topping topping = null;
        try (Cursor cursor = db.query(CreateDatabase.TABLE_TOPPINGS, null,
                CreateDatabase.COLUMN_TOPPING_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (cursor.moveToFirst()) {
                topping = new Topping(
                        cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_TOPPING_PRICE))
                );
            }
        } catch (Exception e) {
            Log.e("ToppingDAO", "Lỗi khi lấy topping theo id", e);
        }
        return topping;
    }
}
