package com.sinhviencafemanagement.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.database.DatabaseManager;
import com.sinhviencafemanagement.models.User;

import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final SQLiteDatabase db; // Đối tượng SQLiteDatabase để DAO thao tác dữ liệu
    RoleDAO roleDAO;
    public UserDAO(Context context) {
        db = DatabaseManager.getDatabase(context);
        roleDAO = new RoleDAO(context);
    }

    // Normalize user (trim - toLowerCase)
    private void normalizeUser(User user) {
        if (user == null) return;
        user.setFullName(trimOrEmpty(user.getFullName()));
        user.setDisplayName(trimOrEmpty(user.getDisplayName()));
        user.setUsername(trimOrEmpty(user.getUsername()).toLowerCase());
        user.setPassword(trimOrEmpty(user.getPassword()));
        user.setEmail(trimOrEmpty(user.getEmail()).toLowerCase());

        if (user.getPhone() != null) user.setPhone(user.getPhone().trim());
        if (user.getGender() != null) user.setGender(user.getGender().trim().toLowerCase());
        if (user.getBirthdate() != null) user.setBirthdate(user.getBirthdate().trim());
    }

    private String trimOrEmpty(String s) {
        return (s == null) ? "" : s.trim();
    }

    // Tạo ContentValues từ đối tượng User để insert/update DB
    @NonNull
    private static ContentValues getContentValues(User user) {
        ContentValues values = new ContentValues();
        values.put(CreateDatabase.COLUMN_USER_NAME, user.getFullName());
        values.put(CreateDatabase.COLUMN_USER_DISPLAY_NAME, user.getDisplayName());
        values.put(CreateDatabase.COLUMN_USER_USERNAME, user.getUsername());
        values.put(CreateDatabase.COLUMN_USER_PASSWORD, user.getPassword());
        values.put(CreateDatabase.COLUMN_USER_EMAIL, user.getEmail());
        values.put(CreateDatabase.COLUMN_USER_PHONE, user.getPhone());
        values.put(CreateDatabase.COLUMN_USER_GENDER, user.getGender());
        values.put(CreateDatabase.COLUMN_USER_BIRTHDATE, user.getBirthdate());
        values.put(CreateDatabase.COLUMN_USER_ROLE_ID, user.getRoleId());
        return values;
    }

    // Lấy giá trị int từ Cursor theo tên cột.
    private int getColumnInt(Cursor cursor, String columnName) {
        try {
            int index = cursor.getColumnIndexOrThrow(columnName);
            return cursor.isNull(index) ? 0 : cursor.getInt(index);
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi lấy cột Int: " + columnName, e);
            return 0;
        }
    }

    // Lấy giá trị String từ Cursor theo tên cột.
    private String getColumnString(Cursor cursor, String columnName) {
        try {
            int index = cursor.getColumnIndexOrThrow(columnName);
            return cursor.isNull(index) ? "" : cursor.getString(index);
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi lấy cột String: " + columnName, e);
            return "";
        }
    }

    // Chuyển 1 dòng dữ liệu từ Cursor sang đối tượng User.
    private User cursorToUser(Cursor cursor) {
        if (cursor == null) return null;

        return new User(
                getColumnInt(cursor, CreateDatabase.COLUMN_USER_ID),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_NAME),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_DISPLAY_NAME),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_USERNAME),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_PASSWORD),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_EMAIL),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_PHONE),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_GENDER),
                getColumnString(cursor, CreateDatabase.COLUMN_USER_BIRTHDATE),
                getColumnInt(cursor, CreateDatabase.COLUMN_USER_ROLE_ID)
        );
    }

    // Truy vấn User theo điều kiện
    private List<User> queryUsers(String selection, String[] selectionArgs) {
        List<User> userList = new ArrayList<>();
        try (Cursor cursor = db.query(CreateDatabase.TABLE_USERS, null, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                do {
                    userList.add(cursorToUser(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi query user", e);
        }
        return userList;
    }

    // Kiểm tra email tồn tại trong DB
    public boolean emailExists(String email) {
        String em = trimOrEmpty(email).toLowerCase();
        if (em.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_EMAIL + " = ?",
                new String[]{em},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra email", e);
            return false;
        }
    }

    // Kiểm tra xem username đã tồn tại hay chưa
    public boolean usernameExists(String username) {
        String uname = trimOrEmpty(username).toLowerCase();
        if (uname.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_USERNAME + " = ?",
                new String[]{uname},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra username", e);
            return false;
        }
    }

    // Kiểm tra username trùng với người khác
    private boolean usernameExistsForOther(int userId, String username) {
        String uname = trimOrEmpty(username).toLowerCase();
        if (uname.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_USERNAME + " = ? AND " + CreateDatabase.COLUMN_USER_ID + " != ?",
                new String[]{uname, String.valueOf(userId)},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra username cho user khác", e);
            return false;
        }
    }

    // Kiểm tra email trùng với người khác
    private boolean emailExistsForOther(int userId, String email) {
        String em = trimOrEmpty(email).toLowerCase();
        if (em.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_EMAIL + " = ? AND " + CreateDatabase.COLUMN_USER_ID + " != ?",
                new String[]{em, String.valueOf(userId)},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra email cho user khác", e);
            return false;
        }
    }

    // Kiểm tra displayName có bị trùng với người dùng khác
    private boolean displayNameExistsForOther(int userId, String displayName) {
        String displayNameNormalized = trimOrEmpty(displayName);
        if (displayNameNormalized.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_DISPLAY_NAME + " = ? AND " + CreateDatabase.COLUMN_USER_ID + " != ?",
                new String[]{displayNameNormalized, String.valueOf(userId)},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra displayNameNormalized cho user khác", e);
            return false;
        }
    }

    // Kiểm tra displayName tồn tại hay chưa
    public boolean displayNameExists(String displayName) {
        String displayNameNormalized = trimOrEmpty(displayName);
        if (displayNameNormalized.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_ID},
                CreateDatabase.COLUMN_USER_DISPLAY_NAME + " = ?",
                new String[]{displayNameNormalized},
                null, null, null)) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra displayNameNormalized", e);
            return false;
        }
    }

    // Hàm mã hóa mật khẩu bằng bcrypt
    public String hashPassword(String password) {
        password = trimOrEmpty(password);
        if (password.isEmpty()) {
            Log.e("UserDAO", "Password null hoặc rỗng!");
            return null; // Tránh throw, trả về null cho DAO xử lý
        }
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt(12));
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi hash mật khẩu", e);
            return null;
        }
        }

    // Hàm kiểm tra password nhập vào với hash trong DB
    public boolean checkPassword(String password, String hashed) {
        password = trimOrEmpty(password);
        if (hashed == null || hashed.isEmpty()) return false;
        try {
            return BCrypt.checkpw(password, hashed);
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra mật khẩu", e);
            return false;
        }
    }

    // Đặt lại mật khẩu
    public boolean resetPassword(int userId, String newPassword) {
        newPassword = trimOrEmpty(newPassword);
        if (newPassword.isEmpty()) return false;

        try {
            String hashed = hashPassword(newPassword);
            if (hashed == null) return false;

            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_USER_PASSWORD, hashed);

            int updated = db.update(
                    CreateDatabase.TABLE_USERS,
                    values,
                    CreateDatabase.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );
            return updated > 0;
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi reset mật khẩu userId=" + userId, e);
            return false;
        }
    }

    // Lấy User theo username hoặc id
    public User getUserByUsernameOrEmail(String input) {
        String normalized = trimOrEmpty(input).toLowerCase();
        if (normalized.isEmpty()) return null;

        List<User> list = queryUsers(
                CreateDatabase.COLUMN_USER_USERNAME + " = ? OR " + CreateDatabase.COLUMN_USER_EMAIL + " = ?",
                new String[]{normalized, normalized}
        );

        return list.isEmpty() ? null : list.get(0);
    }

    // Kiểm tra đăng nhập với username/email và password
    public boolean checkLogin(String input, String password) {
        input = trimOrEmpty(input).toLowerCase();
        password = trimOrEmpty(password);

        if (input.isEmpty() || password.isEmpty()) return false;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                new String[]{CreateDatabase.COLUMN_USER_PASSWORD},
                CreateDatabase.COLUMN_USER_USERNAME + " = ? OR " + CreateDatabase.COLUMN_USER_EMAIL + " = ?",
                new String[]{input, input},
                null, null, null
        )) {
            if (cursor.moveToFirst()) {
                String hashedPassword = getColumnString(cursor, CreateDatabase.COLUMN_USER_PASSWORD);
                return checkPassword(password, hashedPassword);
            }
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi kiểm tra đăng nhập", e);
        }

        return false;
    }

    // Đổi mật khẩu, phải nhập mật khẩu cũ
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        oldPassword = trimOrEmpty(oldPassword);
        newPassword = trimOrEmpty(newPassword);

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            Log.e("UserDAO", "Old hoặc new password không hợp lệ");
            return false;
        }

        try {
            User currentUser = getUserById(userId);
            if (currentUser == null) {
                Log.e("UserDAO", "User không tồn tại, userId=" + userId);
                return false;
            }

            // Kiểm tra password cũ
            if (!checkPassword(oldPassword, currentUser.getPassword())) {
                Log.e("UserDAO", "Password cũ không đúng cho userId=" + userId);
                return false;
            }

            // Hash mật khẩu mới
            String hashedPassword = hashPassword(newPassword);
            if (hashedPassword == null) {
                Log.e("UserDAO", "Hash mật khẩu mới thất bại");
                return false;
            }

            // Update DB
            ContentValues values = new ContentValues();
            values.put(CreateDatabase.COLUMN_USER_PASSWORD, hashedPassword);
            int updated = db.update(
                    CreateDatabase.TABLE_USERS,
                    values,
                    CreateDatabase.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

            return updated > 0;

        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi đổi mật khẩu cho userId=" + userId, e);
            return false;
        }
    }

    // Hàm kiểm tra dữ liệu User hợp lệ và chuẩn hóa
    private boolean validateAndNormalizeUser(User user) {
        if (user == null) return false;

        // Chuẩn hóa các trường
        String username = trimOrEmpty(user.getUsername()).toLowerCase();
        String email = trimOrEmpty(user.getEmail()).toLowerCase();
        String displayName = trimOrEmpty(user.getDisplayName());
        String fullName = trimOrEmpty(user.getFullName());
        String password = trimOrEmpty(user.getPassword());

        // Kiểm tra các trường bắt buộc
        if (username.isEmpty() || email.isEmpty() || displayName.isEmpty() || password.isEmpty()) {
            Log.e("UserDAO", "Dữ liệu user không hợp lệ");
            return false;
        }

        // Kiểm tra định dạng email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.e("UserDAO", "Email không hợp lệ: " + email);
            return false;
        }

        // Kiểm tra username hợp lệ (chỉ gồm chữ và số, ít nhất 3 ký tự)
        if (!username.matches("[a-zA-Z0-9]{3,}")) {
            Log.e("UserDAO", "Username không hợp lệ: " + username);
            return false;
        }

        // Gán lại giá trị chuẩn hóa vào user để dùng luôn
        user.setUsername(username);
        user.setEmail(email);
        user.setDisplayName(displayName);
        user.setFullName(fullName);

        return true;
    }

    // Hàm thêm người dùng mới vào bảng
    public long addUser(User user) {
        if (user == null) {
            Log.e("UserDAO", "User là null");
            return -1;
        }

        // Validate và chuẩn hóa
        if (!validateAndNormalizeUser(user)) return -1;

        // Kiểm tra trùng username/email/displayName
        if (usernameExists(user.getUsername())) {
            Log.e("UserDAO", "Username đã tồn tại: " + user.getUsername());
            return 0;
        }
        if (emailExists(user.getEmail())) {
            Log.e("UserDAO", "Email đã tồn tại: " + user.getEmail());
            return 0;
        }
        if (displayNameExists(user.getDisplayName())) {
            Log.e("UserDAO", "DisplayName đã tồn tại: " + user.getDisplayName());
            return 0;
        }

        // Kiểm tra roleId tồn tại
        if (!roleDAO.roleExists(user.getRoleId())) {
            Log.e("UserDAO", "RoleId không tồn tại: " + user.getRoleId());
            return -1;
        }

        // Hash mật khẩu
        String hashedPassword = hashPassword(user.getPassword());
        if (hashedPassword == null) {
            Log.e("UserDAO", "Hash mật khẩu thất bại");
            return -1;
        }
        user.setPassword(hashedPassword);

        // Tạo ContentValues và insert
        ContentValues values = getContentValues(user);
        try {
            long newId = db.insert(CreateDatabase.TABLE_USERS, null, values);
            if (newId == -1) {
                Log.e("UserDAO", "Insert user thất bại");
            }
            return newId;
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi insert user", e);
            return -1;
        }
    }

    // Cập nhật thông tin người dùng
    public int updateUserInfo(User user) {
        if (user == null) return -1;

        try {
            // Lấy user hiện tại
            User currentUser = getUserById(user.getUserId());
            if (currentUser == null) return 0;

            // Validate và chuẩn hóa dữ liệu mới
            if (!validateAndNormalizeUser(user)) return -1;

            // Kiểm tra trùng username/email với user khác
            if (usernameExistsForOther(user.getUserId(), user.getUsername())) {
                Log.e("UserDAO", "Username đã tồn tại cho user khác: " + user.getUsername());
                return 0;
            }

            if (emailExistsForOther(user.getUserId(), user.getEmail())) {
                Log.e("UserDAO", "Email đã tồn tại cho user khác: " + user.getEmail());
                return 0;
            }

            // Kiểm tra roleId tồn tại
            if (!roleDAO.roleExists(user.getRoleId())) {
                Log.e("UserDAO", "RoleId không tồn tại: " + user.getRoleId());
                return -1;
            }

            // Hash password nếu người dùng nhập password mới và khác password hiện tại
            String newPassword = trimOrEmpty(user.getPassword());
            if (!newPassword.isEmpty() && !checkPassword(newPassword, currentUser.getPassword())) {
                String hashedPassword = hashPassword(newPassword);
                if (hashedPassword == null) return -1;
                user.setPassword(hashedPassword);
            } else {
                user.setPassword(currentUser.getPassword());
            }

            ContentValues values = getContentValues(user);
            return db.update(CreateDatabase.TABLE_USERS, values,
                    CreateDatabase.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(user.getUserId())});

        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi cập nhật user", e);
            return -1;
        }
    }

    // Xóa người dùng theo userId (Admin)
    public int deleteUserById(int userId) {
        if (userId <= 0) {
            Log.e("UserDAO", "UserId không hợp lệ: " + userId);
            return -1;
        }

        try {
            // Kiểm tra user có tồn tại không
            User user = getUserById(userId);
            if (user == null) {
                Log.i("UserDAO", "User không tồn tại: " + userId);
                return 0;
            }
            // Thực hiện xóa
            return db.delete(
                    CreateDatabase.TABLE_USERS,
                    CreateDatabase.COLUMN_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)}
            );

        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi xóa user", e);
            return -1;
        }
    }

    // Lấy người dùng theo userId
    public User getUserById(int userId) {
        if (userId <= 0) return null;

        try (Cursor cursor = db.query(
                CreateDatabase.TABLE_USERS,
                null,
                CreateDatabase.COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)},
                null, null, null
        )) {
            return cursor.moveToFirst() ? cursorToUser(cursor) : null;
        } catch (Exception e) {
            Log.e("UserDAO", "Lỗi khi lấy user theo ID", e);
            return null;
        }
    }

    // Lấy danh sách tất cả người dùng (Admin)
    public List<User> getAllUsers() {
        return queryUsers(null, null);
    }

    // Tìm kiếm người dùng theo tên
    public List<User> getUsersByName(String name) {
        String search = trimOrEmpty(name);
        if (search.isEmpty()) return new ArrayList<>();

        // Tìm kiếm không phân biệt hoa thường
        return queryUsers("UPPER(" + CreateDatabase.COLUMN_USER_NAME + ") LIKE ?",
                new String[]{"%" + search.toUpperCase() + "%"});
    }

    // Lấy danh sách nhân viên theo quyền (Admin dùng)
    public List<User> getUserByRoleId(int roleId) {
        if (roleId <= 0) {
            Log.e("UserDAO", "RoleId không hợp lệ: " + roleId);
            return new ArrayList<>();
        }

        if (!roleDAO.roleExists(roleId)) {
            Log.i("UserDAO", "RoleId không tồn tại: " + roleId);
            return new ArrayList<>();
        }

        return queryUsers(CreateDatabase.COLUMN_USER_ROLE_ID + " = ?",
                new String[]{String.valueOf(roleId)});
    }


}
