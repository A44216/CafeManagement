package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.OrderDetailTopping;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailToppingDAO {
    private final SQLiteDatabase db;

    public OrderDetailToppingDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    // Lấy topping theo order + product
    public List<OrderDetailTopping> getToppingsByOrderAndProduct(int orderId, int productId) {
        List<OrderDetailTopping> list = new ArrayList<>();
        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ORDER_DETAIL_TOPPINGS,
                null,
                CreateDatabase.COLUMN_ODT_ORDER_ID + " = ? AND " + CreateDatabase.COLUMN_ODT_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(orderId), String.valueOf(productId)},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                do {
                    OrderDetailTopping odt = new OrderDetailTopping(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ODT_ORDER_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ODT_PRODUCT_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ODT_TOPPING_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ODT_QUANTITY))
                    );
                    list.add(odt);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("OrderDetailToppingDAO", "Lỗi khi lấy danh sách order detail topping", e);
        }
        return list;
    }

    // Thêm OrderDetailTopping
    public long addOrderDetailTopping(OrderDetailTopping odt) {
        if (odt == null) return -1;
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_ODT_ORDER_ID, odt.getOrderId());
        values.put(CreateDatabase.COLUMN_ODT_PRODUCT_ID, odt.getProductId());
        values.put(CreateDatabase.COLUMN_ODT_TOPPING_ID, odt.getToppingId());
        values.put(CreateDatabase.COLUMN_ODT_QUANTITY, odt.getQuantity());
        long result = -1;
        try {
            result = db.insert(CreateDatabase.TABLE_ORDER_DETAIL_TOPPINGS, null, values);
        } catch (Exception e) {
            Log.e("OrderDetailToppingDAO", "Lỗi khi thêm order detail topping", e);
        }
        return result;
    }

    // Cập nhật quantity
    public int updateQuantity(OrderDetailTopping odt) {
        if (odt == null) return 0;
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_ODT_QUANTITY, odt.getQuantity());
        int result = 0;
        try {
            result = db.update(
                    CreateDatabase.TABLE_ORDER_DETAIL_TOPPINGS,
                    values,
                    CreateDatabase.COLUMN_ODT_ORDER_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ODT_PRODUCT_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ODT_TOPPING_ID + " = ?",
                    new String[]{String.valueOf(odt.getOrderId()), String.valueOf(odt.getProductId()), String.valueOf(odt.getToppingId())}
            );
        } catch (Exception e) {
            Log.e("OrderDetailToppingDAO", "Lỗi khi cập nhật quantity", e);
        }
        return result;
    }

    // Xóa order detail topping
    public int deleteOrderDetailTopping(OrderDetailTopping odt) {
        if (odt == null) return 0;
        int result = 0;
        try {
            result = db.delete(
                    CreateDatabase.TABLE_ORDER_DETAIL_TOPPINGS,
                    CreateDatabase.COLUMN_ODT_ORDER_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ODT_PRODUCT_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ODT_TOPPING_ID + " = ?",
                    new String[]{String.valueOf(odt.getOrderId()), String.valueOf(odt.getProductId()), String.valueOf(odt.getToppingId())}
            );
        } catch (Exception e) {
            Log.e("OrderDetailToppingDAO", "Lỗi khi xóa order detail topping", e);
        }
        return result;
    }
}
