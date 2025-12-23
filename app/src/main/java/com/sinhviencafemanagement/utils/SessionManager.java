package com.sinhviencafemanagement.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.Session;
import com.sinhviencafemanagement.models.User;

public class SessionManager {

    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_SESSION_TOKEN = "session_token";
    private final String TAG = "SessionManager";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final SessionDAO sessionDAO;
    private final Context context;

    public SessionManager(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
        this.sessionDAO = new SessionDAO(this.context);
    }

    /**
     * Lưu token vào SharedPreferences sau khi đăng nhập thành công
     * @param token Token của session
     */
    public void saveSession(String token) {
        editor.putString(KEY_SESSION_TOKEN, token);
        editor.apply();
        Log.i(TAG, "Đã lưu session token vào SharedPreferences.");
    }

    /**
     * Lấy token từ SharedPreferences
     * @return Session token hoặc null nếu không có
     */
    public String getSessionToken() {
        return prefs.getString(KEY_SESSION_TOKEN, null);
    }

    /**
     * Lấy thông tin session hiện tại từ DB
     * @return Session object hoặc null nếu không có session hợp lệ
     */
    public Session getCurrentSession() {
        String token = getSessionToken();
        if (token != null) {
            return sessionDAO.getSessionByToken(token);
        }
        return null;
    }

    /**
     * Lấy ID của người dùng đang đăng nhập
     * @return User ID hoặc -1 nếu không có ai đăng nhập
     */
    public int getUserId() {
        Session session = getCurrentSession();
        if (session != null) {
            return session.getUserId();
        }
        return -1; // hoặc một giá trị mặc định khác báo hiệu chưa đăng nhập
    }

    public String getUserFullName() {
        int userId = getUserId();
        if (userId != -1) {
            UserDAO userDAO = new UserDAO(context);
            User user = userDAO.getUserById(userId); // Bạn cần đảm bảo UserDAO có hàm này
            if (user != null) return user.getFullName();
        }
        return "Khách hàng";
    }

    public String getUserPhone() {
        int userId = getUserId();
        if (userId != -1) {
            UserDAO userDAO = new UserDAO(context);
            User user = userDAO.getUserById(userId);
            if (user != null) return user.getPhone();
        }
        return "Chưa cập nhật";
    }

    /**
     * Kiểm tra người dùng đã đăng nhập hay chưa
     * @return true nếu đã đăng nhập, false nếu chưa
     */
    public boolean isLoggedIn() {
        return getSessionToken() != null && getCurrentSession() != null;
    }

    /**
     * Đăng xuất người dùng
     */
    public void logout() {
        String token = getSessionToken();
        if (token != null) {
            sessionDAO.logoutUser(token); // logoutUser đã bao gồm xóa SharedPreferences
            Log.i(TAG, "Người dùng đã đăng xuất.");
        }
    }
}
