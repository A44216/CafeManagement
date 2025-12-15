package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.Session;

import java.util.UUID;

public class SessionDAO {
    private final String TAG = "SessionDAO";
    // Đối tượng SQLiteDatabase để DAO thao tác dữ liệu
    private final SQLiteDatabase db;

    public SessionDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
    }


    // Tạo token mới cho user
    public String createSession(int userId, long expiredAt) {
        if (userId <= 0) {
            Log.e(TAG, "ID người dùng không hợp lệ: " + userId);
            return null;
        }

        if (expiredAt <= System.currentTimeMillis()) {
            Log.e(TAG, "Thời gian hết hạn không hợp lệ: " + expiredAt);
            return null;
        }

        String token = UUID.randomUUID().toString();
        try {
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_SESSION_USER_ID, userId);
            values.put(CreateDatabase.COLUMN_SESSION_TOKEN, token);
            values.put(CreateDatabase.COLUMN_SESSION_EXPIRED_AT, expiredAt);

            long result = db.insert(CreateDatabase.TABLE_SESSIONS, null, values);
            if (result == -1) {
                Log.e(TAG, "Tạo session thất bại cho ID người dùng: " + userId);
                return null;
            }
            return token;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi tạo session", e);
            return null;
        }
    }

    // Lấy session theo token
    public Session getSessionByToken(String token) {
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Token rỗng hoặc null");
            return null;
        }

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_SESSIONS,
                null,
                CreateDatabase.COLUMN_SESSION_TOKEN + " = ?",
                new String[]{token},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                Session session = cursorToSession(cursor);
                if (isTokenExpired(session.getExpiredAt())) {
                    deleteSession(token);
                    return null;
                }
                return session;
            }
        } catch (Exception e) {
            Log.i(TAG, "Token đã hết hạn, xóa session");
        }
        return null;
    }

    // Lấy session theo userId (nếu muốn auto-login theo user)
    public Session getSessionByUserId(int userId) {
        if (userId <= 0) {
            Log.e(TAG, "ID người dùng không hợp lệ: " + userId);
            return null;
        }

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_SESSIONS,
                null,
                CreateDatabase.COLUMN_SESSION_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                Session session = cursorToSession(cursor);
                if (isTokenExpired(session.getExpiredAt())) {
                    deleteSessionsByUserId(userId);
                    Log.i(TAG, "Session của người dùng đã hết hạn, xóa tất cả");
                    return null;
                }
                return session;
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy session theo ID người dùng", e);
        }
        return null;
    }

    // Xóa 1 session theo token (logout)
    public void deleteSession(String token) {
        if (token == null || token.isEmpty()) {
            Log.e(TAG, "Không thể xóa session, token rỗng hoặc null");
            return;
        }
        try {
            db.delete(
                    CreateDatabase.TABLE_SESSIONS,
                    CreateDatabase.COLUMN_SESSION_TOKEN + " = ?",
                    new String[]{token}
            );

            Log.i(TAG, "Đã xóa session với token: " + token);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa session", e);
        }
    }

    // Xóa toàn bộ session của 1 user (logout all devices)
    public void deleteSessionsByUserId(int userId) {
        if (userId <= 0) {
            Log.e(TAG, "ID người dùng không hợp lệ: " + userId);
            return;
        }

        try {
            db.delete(
                    CreateDatabase.TABLE_SESSIONS,
                    CreateDatabase.COLUMN_SESSION_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            Log.i(TAG, "Đã xóa tất cả session của người dùng ID: " + userId);

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi xóa session của người dùng", e);
        }
    }

    // Kiểm tra token có hết hạn không
    public boolean isTokenExpired(long expiredAt) {
        return System.currentTimeMillis() > expiredAt;
    }

    // Dọn tất cả session hết hạn
    public void cleanExpiredSessions() {
        try {
            long now = System.currentTimeMillis();
            db.delete(
                    CreateDatabase.TABLE_SESSIONS,
                    CreateDatabase.COLUMN_SESSION_EXPIRED_AT + " < ?",
                    new String[]{String.valueOf(now)}
            );

            Log.i(TAG, "Đã dọn tất cả session hết hạn");

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi dọn session hết hạn", e);
        }
    }

    // Chuyển Cursor sang Session object
    private Session cursorToSession(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_SESSION_USER_ID));
        String token = cursor.getString(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_SESSION_TOKEN));
        long expiredAt = cursor.getLong(cursor.getColumnIndexOrThrow(CreateDatabase.COLUMN_SESSION_EXPIRED_AT));

        return new Session(userId, token, expiredAt);
    }

}
