package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Table;

import java.util.ArrayList;
import java.util.List;

public class TableDAO {
    private final SQLiteDatabase db; // Đối tượng thao tác với SQLite

    // Constructor mở database
    public TableDAO(Context context) {
        db = DatabaseManager.getDatabase(context);

    }

    // Lấy tất cả bàn
    public List<Table> getAllTables() {
        return queryTables(null, null);
    }

    // Kiểm tra trùng tên bàn
    public boolean tableExistsByName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) return false;

        boolean exists = false;
        String normalized = tableName.trim();

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_TABLES,
                new String[]{CreateDatabase.COLUMN_TABLE_ID},
                "LOWER(" + CreateDatabase.COLUMN_TABLE_NAME + ") = ?",
                new String[]{tableName.trim().toLowerCase()},
                null, null, null
        )) {
            exists = cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi kiểm tra bàn tồn tại theo tên", e);
        }
        return exists;
    }

    // Thêm bàn mới
    public long addTable(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            Log.e("TableDAO", "Tên bàn không hợp lệ");
            return -1;
        }

        String normalizedName = tableName.trim();
        if (tableExistsByName(normalizedName)) {
            Log.e("TableDAO", "Bàn đã tồn tại: " + normalizedName);
            return 0;
        }

        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_TABLE_NAME, normalizedName);
            values.put(CreateDatabase.COLUMN_TABLE_STATUS, CreateDatabase.TABLE_STATUS_AVAILABLE);
            return db.insert(CreateDatabase.TABLE_TABLES, null, values);
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi thêm bàn", e);
            return -1;
        }
    }

    // Xóa bàn
    public int deleteTable(int tableId) {
        if (tableId <= 0) {
            Log.e("TableDAO", "ID bàn không hợp lệ: " + tableId);
            return -1;
        }

        Table current = getTableById(tableId);
        if (current == null) {
            Log.e("TableDAO", "Bàn không tồn tại: " + tableId);
            return -1;
        }

        int result = -1;
        try {
            result = db.delete(
                    CreateDatabase.TABLE_TABLES,
                    CreateDatabase.COLUMN_TABLE_ID + " = ?",
                    new String[]{String.valueOf(tableId)}
            );
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi xóa bàn", e);
        }
        return result;
    }

    // Cập nhật tên bàn hoặc trạng thái
    public int updateTable(int tableId, String newName, String newStatus) {
        if (tableId <= 0) {
            Log.e("TableDAO", "ID bàn không hợp lệ: " + tableId);
            return -1;
        }

        Table current = getTableById(tableId);
        if (current == null) {
            Log.e("TableDAO", "Bàn không tồn tại: " + tableId);
            return -1;
        }

        ContentValues values = new ContentValues();

        // Chuẩn hóa tên bàn
        if (newName != null && !newName.trim().isEmpty()) {
            String normalizedName = newName.trim();
            if (tableExistsByName(normalizedName) && !current.getName().equalsIgnoreCase(normalizedName)) {
                Log.e("TableDAO", "Tên bàn đã tồn tại: " + normalizedName);
                return 0;
            }
            values.put(CreateDatabase.COLUMN_TABLE_NAME, normalizedName);
        }

        // Chuẩn hóa trạng thái
        if (newStatus != null) {
            String normalizedStatus = normalizeStatus(newStatus);
            values.put(CreateDatabase.COLUMN_TABLE_STATUS, normalizedStatus);
        }

        if (values.size() == 0) return 0; // Không có gì để cập nhật

        int result = -1;
        try {
            result = db.update(
                    CreateDatabase.TABLE_TABLES,
                    values,
                    CreateDatabase.COLUMN_TABLE_ID + " = ?",
                    new String[]{String.valueOf(tableId)}
            );
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi cập nhật bàn", e);
        }
        return result;
    }

    public boolean tableExists(int tableId) {
        return getTableById(tableId) != null;
    }

    // Lấy danh sách bàn theo trạng thái
    public List<Table> getTablesByStatus(String status) {
        if (status == null) return getAllTables();
        return queryTables(CreateDatabase.COLUMN_TABLE_STATUS + " = ?", new String[]{normalizeStatus(status)});
    }

    // Lấy String từ cursor
    private String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndexOrThrow(columnName));
    }

    // Lấy int từ cursor
    private int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndexOrThrow(columnName));
    }

    // Chuyển cursor sang Table
    private Table cursorToTable(Cursor cursor) {
        return new Table(
                getColumnInt(cursor, CreateDatabase.COLUMN_TABLE_ID),
                getColumnString(cursor, CreateDatabase.COLUMN_TABLE_NAME),
                getColumnString(cursor, CreateDatabase.COLUMN_TABLE_STATUS)
        );
    }

    // Lấy Table theo ID hoặc trả về null nếu không tồn tại
    public Table getTableById(int tableId) {
        if (tableId <= 0) return null;
        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_TABLES,
                null,
                CreateDatabase.COLUMN_TABLE_ID + " = ?",
                new String[]{String.valueOf(tableId)},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                return cursorToTable(cursor);
            }
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi lấy bàn theo ID", e);
        }
        return null;
    }

    // Truy suất bảng tables theo điều kiện
    private List<Table> queryTables(String selection, String[] selectionArgs) {
        List<Table> tableList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_TABLES, null, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    tableList.add(cursorToTable(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("TableDAO", "Lỗi khi query bàn", e);
        }
        return tableList;
    }

    private String normalizeStatus(String status) {
        if (status == null) return CreateDatabase.TABLE_STATUS_AVAILABLE;
        status = status.trim().toLowerCase();
        if (CreateDatabase.TABLE_STATUS_AVAILABLE.equals(status) || CreateDatabase.TABLE_STATUS_OCCUPIED.equals(status)) {
            return status;
        }
        return CreateDatabase.TABLE_STATUS_AVAILABLE; // mặc định nếu không hợp lệ
    }

}
