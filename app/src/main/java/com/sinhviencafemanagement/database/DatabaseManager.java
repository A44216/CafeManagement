package com.sinhviencafemanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
    private static CreateDatabase dbHelper;
    private static SQLiteDatabase database;

    // Lấy instance database
    public static synchronized SQLiteDatabase getDatabase(Context context) {
        if (dbHelper == null) {
            dbHelper = new CreateDatabase(context.getApplicationContext());
        }
        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }

    // Đóng database khi app kết thúc
    public static synchronized void closeDatabase() {
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
