package com.sinhviencafemanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Lớp CreateDatabase dùng để tạo và quản lý cơ sở dữ liệu SQLite cho ứng dụng quản lý quán cafe
public class CreateDatabase extends SQLiteOpenHelper {

    // Bảng người dùng
    public static final String TABLE_USERS = "users";  // Người dùng/khách hàng hoặc nhân viên phụ trách
    public static final String COLUMN_USER_ID = "user_id";   // Mã nhân viên
    public static final String COLUMN_USER_NAME = "full_name"; // Họ và tên
    public static final String COLUMN_USER_DISPLAY_NAME = "display_name"; // tên hiển thị
    public static final String COLUMN_USER_USERNAME = "username"; // Tên đăng nhập
    public static final String COLUMN_USER_PASSWORD = "password"; // Mật khẩu
    public static final String COLUMN_USER_EMAIL = "email"; // Email
    public static final String COLUMN_USER_PHONE = "phone"; // Số điện thoại
    public static final String COLUMN_USER_GENDER = "gender"; // Giới tính
    public static final String COLUMN_USER_BIRTHDATE = "birthdate"; // Ngày sinh
    public static final String COLUMN_USER_ROLE_ID = "role_id"; // Mã quyền

    // Bảng quyền
    public static final String TABLE_ROLES = "roles"; // Bảng quyền
    public static final String COLUMN_ROLE_ID = "role_id"; // Mã quyền (1=Admin, 2=Staff, 3=Customer)
    public static final String COLUMN_ROLE_NAME = "role_name"; // Tên quyền
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_STAFF = 2;
    public static final int ROLE_CUSTOMER = 3;

    // Bảng món
    public static final String TABLE_PRODUCTS = "products"; // Bảng món
    public static final String COLUMN_PRODUCT_ID = "product_id"; // Mã món
    public static final String COLUMN_PRODUCT_NAME = "product_name"; // Tên món
    public static final String COLUMN_PRODUCT_PRICE = "price"; // Giá tiền
    public static final String COLUMN_PRODUCT_STATUS = "status"; // Tình trạng
    public static final String COLUMN_PRODUCT_IMAGE = "image"; // Hình ảnh
    public static final String COLUMN_PRODUCT_CATEGORY_ID = "category_id"; // Mã loại món
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description"; // Mô tả sản phẩm
    // Trạng thái sản phẩm
    public static final String PRODUCT_STATUS_AVAILABLE = "available";
    public static final String PRODUCT_STATUS_UNAVAILABLE = "unavailable";

    // Bảng loại món
    public static final String TABLE_CATEGORIES = "categories"; // Bảng loại món
    public static final String COLUMN_CATEGORY_ID = "category_id"; // Mã loại
    public static final String COLUMN_CATEGORY_NAME = "category_name"; // Tên loại
    // Bảng bàn
    public static final String TABLE_TABLES = "tables"; // Bảng bàn
    public static final String COLUMN_TABLE_ID = "table_id"; // Mã bàn
    public static final String COLUMN_TABLE_NAME = "table_name"; // Tên bàn
    public static final String COLUMN_TABLE_STATUS = "status"; // Tình trạng bàn
    // Trạng thái bàn
    public static final String TABLE_STATUS_AVAILABLE = "available";
    public static final String TABLE_STATUS_OCCUPIED = "occupied";

    // Bảng đơn đặt
    public static final String TABLE_ORDERS = "orders"; // Bảng đơn đặt
    public static final String COLUMN_ORDER_ID = "order_id"; // Mã đơn đặt
    public static final String COLUMN_ORDER_USER_ID = "user_id"; // “Người dùng phụ trách”
    public static final String COLUMN_ORDER_DATE = "order_date"; // Ngày đặt
    public static final String COLUMN_ORDER_STATUS = "status"; // Tình trạng đơn
    public static final String COLUMN_ORDER_TOTAL = "total"; // Tổng tiền
    public static final String COLUMN_ORDER_TABLE_ID = "table_id"; // Mã bàn
    // Trạng thái đơn hàng
    public static final String ORDER_STATUS_PENDING = "pending";
    public static final String ORDER_STATUS_COMPLETED = "completed";

    // Bảng chi tiết đơn đặt
    public static final String TABLE_ORDER_DETAILS = "order_details"; // Bảng chi tiết đơn
    public static final String COLUMN_ORDER_DETAIL_ORDER_ID = "order_id"; // Mã đơn
    public static final String COLUMN_ORDER_DETAIL_PRODUCT_ID = "product_id"; // Mã món
    public static final String COLUMN_ORDER_DETAIL_QUANTITY = "quantity"; // Số lượng

    // Bảng sessions
    public static final String TABLE_SESSIONS = "sessions";
    public static final String COLUMN_SESSION_ID = "session_id";
    public static final String COLUMN_SESSION_USER_ID = "user_id";
    public static final String COLUMN_SESSION_TOKEN = "token";
    public static final String COLUMN_SESSION_CREATED_AT = "created_at";
    public static final String COLUMN_SESSION_EXPIRED_AT = "expired_at";

    // Constructor của lớp CreateDatabase, dùng để tạo cơ sở dữ liệu "OrderDrink" phiên bản 1
    public CreateDatabase(Context context) {
        super(context, "CafeManagement", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Bảng tables
        String tblTables = "CREATE TABLE " + TABLE_TABLES + " (" +
                COLUMN_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TABLE_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_TABLE_STATUS + " TEXT DEFAULT '" + TABLE_STATUS_AVAILABLE + "');";

        // Bảng roles
        String tblRoles = "CREATE TABLE " + TABLE_ROLES + " ("
                + COLUMN_ROLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_ROLE_NAME + " TEXT NOT NULL UNIQUE);";

        // Bảng users
        String tblUsers = "CREATE TABLE " + TABLE_USERS + "( " +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  +
                COLUMN_USER_NAME + " TEXT NOT NULL , " +
                COLUMN_USER_DISPLAY_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_USERNAME + " TEXT NOT NULL UNIQUE CHECK(length(" + COLUMN_USER_USERNAME + ") >= 7 AND length(" + COLUMN_USER_USERNAME + ") <= 25), " +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL, " +                COLUMN_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COLUMN_USER_PHONE + " TEXT CHECK(length(" + COLUMN_USER_PHONE + ") = 10), " +
                COLUMN_USER_GENDER + " TEXT, " +
                COLUMN_USER_BIRTHDATE + " TEXT, " +
                COLUMN_USER_ROLE_ID + " INTEGER NOT NULL DEFAULT " + ROLE_CUSTOMER + ", " +
                "FOREIGN KEY(" + COLUMN_USER_ROLE_ID + ") REFERENCES " + TABLE_ROLES + "(" + COLUMN_ROLE_ID + "));";

        // Bảng categories
        String tblCategories = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT NOT NULL UNIQUE" + ");";

        // Bảng products
        String tblProducts = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PRODUCT_PRICE + " REAL NOT NULL, " +
                COLUMN_PRODUCT_STATUS + " TEXT DEFAULT '" + PRODUCT_STATUS_AVAILABLE + "', " +
                COLUMN_PRODUCT_IMAGE + " TEXT, " +
                COLUMN_PRODUCT_CATEGORY_ID + " INTEGER, " +
                COLUMN_PRODUCT_DESCRIPTION + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "));";

        // Bảng orders
        String tblOrders = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_USER_ID + " INTEGER, " +
                COLUMN_ORDER_DATE + " TEXT, " +
                COLUMN_ORDER_STATUS + " TEXT DEFAULT '" + ORDER_STATUS_PENDING + "', " +
                COLUMN_ORDER_TOTAL + " REAL DEFAULT 0, " +
                COLUMN_ORDER_TABLE_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ORDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_TABLE_ID + ") REFERENCES " + TABLE_TABLES + "(" + COLUMN_TABLE_ID + "));";

        // Bảng order_details
        String tblOrderDetails = "CREATE TABLE " + TABLE_ORDER_DETAILS + " (" +
                COLUMN_ORDER_DETAIL_ORDER_ID + " INTEGER, " +
                COLUMN_ORDER_DETAIL_PRODUCT_ID + " INTEGER, " +
                COLUMN_ORDER_DETAIL_QUANTITY + " INTEGER NOT NULL, " +
                "PRIMARY KEY(" + COLUMN_ORDER_DETAIL_ORDER_ID + ", " + COLUMN_ORDER_DETAIL_PRODUCT_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_DETAIL_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_DETAIL_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_PRODUCT_ID + "));";

        // Bảng sessions (lưu token đăng nhập)
        String tblSessions = "CREATE TABLE " + TABLE_SESSIONS + " (" +
                COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SESSION_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_SESSION_TOKEN + " TEXT NOT NULL UNIQUE, " +
                COLUMN_SESSION_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_SESSION_EXPIRED_AT + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_SESSION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                ");";

        // Thực thi các câu lệnh tạo bảng
        db.execSQL(tblTables);
        db.execSQL(tblRoles);
        db.execSQL(tblUsers);
        db.execSQL(tblCategories);
        db.execSQL(tblProducts);
        db.execSQL(tblOrders);
        db.execSQL(tblOrderDetails);
        db.execSQL(tblSessions);

        // Chèn role mặc định (sử dụng đúng tên bảng và cột)
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (" + ROLE_ADMIN + ", 'admin')");
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (" + ROLE_STAFF + ", 'staff')");
        db.execSQL("INSERT INTO " + TABLE_ROLES + " (" + COLUMN_ROLE_ID + ", " + COLUMN_ROLE_NAME + ") VALUES (" + ROLE_CUSTOMER + ", 'customer')");

    }

    // Xử lý khi nâng cấp phiên bản database (thêm, sửa hoặc xóa bảng, cột)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng theo đúng thứ tự khóa ngoại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TABLES);
        // Tạo lại bảng
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // bật khóa ngoại trong SQLite
    }

}
