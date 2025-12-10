package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailDAO {
    private final SQLiteDatabase db; // Đối tượng SQLite để thao tác dữ liệu

    // Constructor mở DB
    public OrderDetailDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }

    // Thêm chi tiết đơn
    public long addOrderDetail(OrderDetail detail) {
        if (detail == null) {
            Log.e("OrderDetailDAO", "Chi tiết đơn không hợp lệ");
            return -1;
        }

        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_ORDER_DETAIL_ORDER_ID, detail.getOrderId());
            values.put(CreateDatabase.COLUMN_ORDER_DETAIL_PRODUCT_ID, detail.getProductId());
            values.put(CreateDatabase.COLUMN_ORDER_DETAIL_QUANTITY, detail.getQuantity());

            return db.insert(CreateDatabase.TABLE_ORDER_DETAILS, null, values);
        } catch (Exception e) {
            Log.e("OrderDetailDAO", "Lỗi khi thêm chi tiết đơn", e);
            return -1;
        }
    }

    // Cập nhật số lượng chi tiết đơn
    public int updateOrderDetail(OrderDetail detail) {
        if (detail == null) {
            Log.e("OrderDetailDAO", "Chi tiết đơn không hợp lệ");
            return -1;
        }

        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_ORDER_DETAIL_QUANTITY, detail.getQuantity());

            int updated = db.update(
                    CreateDatabase.TABLE_ORDER_DETAILS,
                    values,
                    CreateDatabase.COLUMN_ORDER_DETAIL_ORDER_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ORDER_DETAIL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(detail.getOrderId()), String.valueOf(detail.getProductId())}
            );

            if (updated == 0) Log.e("OrderDetailDAO", "Chi tiết đơn không tồn tại: orderId=" +
                    detail.getOrderId() + ", productId=" + detail.getProductId());

            return updated;
        } catch (Exception e) {
            Log.e("OrderDetailDAO", "Lỗi khi cập nhật chi tiết đơn", e);
            return -1;
        }
    }

    // Xóa chi tiết đơn theo orderId và productId
    public int deleteDetail(int orderId, int productId) {
        if (orderId <= 0 || productId <= 0) {
            Log.e("OrderDetailDAO", "Xóa chi tiết đơn thất bại: orderId hoặc productId không hợp lệ");
            return -1;
        }

        try {
            int deleted = db.delete(
                    CreateDatabase.TABLE_ORDER_DETAILS,
                    CreateDatabase.COLUMN_ORDER_DETAIL_ORDER_ID + " = ? AND " +
                            CreateDatabase.COLUMN_ORDER_DETAIL_PRODUCT_ID + " = ?",
                    new String[]{String.valueOf(orderId), String.valueOf(productId)}
            );

            if (deleted == 0) Log.e("OrderDetailDAO", "Chi tiết đơn không tồn tại: orderId=" + orderId +
                    ", productId=" + productId);

            return deleted;
        } catch (Exception e) {
            Log.e("OrderDetailDAO", "Lỗi khi xóa chi tiết đơn", e);
            return -1;
        }
    }

    // Lấy danh sách chi tiết đơn theo orderId
    public List<OrderDetail> getDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        if (orderId <= 0) return list;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_ORDER_DETAILS,
                null,
                CreateDatabase.COLUMN_ORDER_DETAIL_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                do {
                    OrderDetail detail = new OrderDetail(
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_DETAIL_ORDER_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_DETAIL_PRODUCT_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ORDER_DETAIL_QUANTITY))
                    );
                    list.add(detail);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("OrderDetailDAO", "Lỗi khi lấy chi tiết đơn theo orderId", e);
        }
        return list;
    }

}
