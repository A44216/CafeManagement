package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    private final SQLiteDatabase db; // Đối tượng thao tác với SQLite

    // Hàm khởi tạo DAO
    public RoleDAO(Context context) {
        db = DatabaseManager.getDatabase(context);

    }

    // Thêm quyền mới
    public long addRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) return -1;
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_ROLE_NAME, roleName.trim());
            result = db.insert(CreateDatabase.TABLE_ROLES, null, values);
        } catch (Exception e) {
            Log.e("RoleDAO", "Lỗi khi thêm role", e);
        }
        return result; // Trả về -1 nếu thất bại
    }

    // Cập nhật tên quyền theo id
    public int updateRole(int roleId, String newRoleName) {
        if (newRoleName == null || newRoleName.trim().isEmpty()) return 0;
        int result = 0;
        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_ROLE_NAME, newRoleName.trim());
            result = db.update(CreateDatabase.TABLE_ROLES,
                    values,
                    CreateDatabase.COLUMN_ROLE_ID + " = ?",
                    new String[]{String.valueOf(roleId)});
        } catch (Exception e) {
            Log.e("RoleDAO", "Lỗi khi cập nhật role", e);
        }
        return result;
    }

    // Hàm lấy tất cả role
    public List<Role> getAllRoles() {
        return queryRoles(null, null);
    }

    // Lấy quyền theo ID với try-catch để tránh lỗi database
    public Role getRoleById(int roleId) {
        List<Role> list = queryRoles(CreateDatabase.COLUMN_ROLE_ID + " = ?", new String[]{String.valueOf(roleId)});
        return list.isEmpty() ? null : list.get(0);
    }

    // Xoa quyền theo id
    public int deleteRole(int roleId) {
        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_ROLE_ID + " = ?",
                new String[]{String.valueOf(roleId)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                Log.e("RoleDAO", "Không thể xóa role này, còn người dùng đang sử dụng roleId: " + roleId);
                return 0;
            }

            int deleted = db.delete(
                    CreateDatabase.TABLE_ROLES,
                    CreateDatabase.COLUMN_ROLE_ID + " = ?",
                    new String[]{String.valueOf(roleId)});
            if (deleted == 0) Log.e("RoleDAO", "Không tìm thấy role để xóa: " + roleId);
            return deleted;

        } catch (Exception e) {
            Log.e("RoleDAO", "Lỗi khi xóa role", e);
            return -1;
        }
    }

    // Kiểm tra role có tồn tại hay không
    public boolean roleExists(int roleId) {
        if (roleId <= 0) return false;
        return getRoleById(roleId) != null;
    }

    // Chuyển cursor sang role
    private Role cursorToRole(Cursor cursor) {
        return new Role(
                cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ROLE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_ROLE_NAME))
        );
    }

    // Truy vấn bảng Role theo điều kiện
    private List<Role> queryRoles(String selection, String[] selectionArgs) {
        List<Role> roleList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_ROLES, null, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    roleList.add(cursorToRole(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("RoleDAO", "Lỗi khi query role", e);
        }
        return roleList;
    }

}
