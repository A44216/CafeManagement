package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final SQLiteDatabase db; // Đối tượng SQLite để thao tác dữ liệu

    // Constructor: mở database để đọc/ghi
    public OrderDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    @NonNull
    private static ContentValues getOrderValues(Order order) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_ORDER_USER_ID, order.getUserId()); // user phụ trách
        values.put(CreateDatabase.COLUMN_ORDER_DATE, order.getOrderDate()); // ngày đặt
        values.put(CreateDatabase.COLUMN_ORDER_STATUS, order.getStatus()); // trạng thái
        values.put(CreateDatabase.COLUMN_ORDER_TOTAL, order.getTotal()); // tổng tiền
        values.put(CreateDatabase.COLUMN_ORDER_TABLE_ID, order.getTableId()); // bàn
        return values;
    }

    // Thêm một đơn mới vào bảng orders
    public long addOrder(Order order) {
        if (order == null) {
            Log.e("OrderDAO", "Đơn không hợp lệ");
            return -1;
        }

        try {
            return db.insert(CreateDatabase.TABLE_ORDERS, null, getOrderValues(order));
        } catch (Exception e) {
            Log.e("OrderDAO", "Lỗi khi thêm đơn", e);
            return -1;
        }
    }

    // Cập nhật thông tin đơn theo orderId
    public int updateOrder(Order order) {
        if (order == null) {
            Log.e("OrderDAO", "Đơn không hợp lệ");
            return -1;
        }

        try {
            int rows = db.update(
                    CreateDatabase.TABLE_ORDERS,
                    getOrderValues(order),
                    CreateDatabase.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(order.getOrderId())}
            );
            if (rows == 0) Log.e("OrderDAO", "Không tìm thấy đơn để cập nhật: " + order.getOrderId());
            return rows;
        } catch (Exception e) {
            Log.e("OrderDAO", "Lỗi khi cập nhật đơn", e);
            return -1;
        }
    }

    // Xóa đơn theo orderId
    public int deleteOrder(int orderId) {
        if (orderId <= 0) {
            Log.e("OrderDAO", "Xóa thất bại: orderId không hợp lệ " + orderId);
            return 0;
        }

        try {
            int rows = db.delete(
                    CreateDatabase.TABLE_ORDERS,
                    CreateDatabase.COLUMN_ORDER_ID + " = ?",
                    new String[]{String.valueOf(orderId)}
            );
            if (rows == 0) Log.e("OrderDAO", "Không tìm thấy đơn để xóa: " + orderId);
            return rows;
        } catch (Exception e) {
            Log.e("OrderDAO", "Lỗi khi xóa đơn", e);
            return -1;
        }
    }

    // Lấy đơn theo ID
    public Order getOrderById(int orderId) {
        if (orderId <= 0) return null;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ORDERS,
                null,
                CreateDatabase.COLUMN_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                return new Order(
                        cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_STATUS)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_TOTAL)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_TABLE_ID))
                );
            }
        } catch (Exception e) {
            Log.e("OrderDAO", "Lỗi khi lấy đơn theo ID", e);
        }
        Log.e("OrderDAO", "Không tìm thấy đơn: " + orderId);
        return null;
    }

    // Lấy tất cả đơn, sắp xếp theo ngày giảm dần
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ORDERS,
                null,
                null, null, null, null,
                CreateDatabase.COLUMN_ORDER_DATE + " DESC" // sắp xếp mới nhất trước
        )) {
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_USER_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_STATUS)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_TOTAL)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_TABLE_ID))
                    );
                    list.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("OrderDAO", "Lỗi khi lấy danh sách đơn", e);
        }
        return list;
    }

}
