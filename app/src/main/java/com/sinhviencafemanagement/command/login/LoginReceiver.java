package com.sinhviencafemanagement.command.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.sinhviencafemanagement.activities.home.AdminHomeActivity;
import com.sinhviencafemanagement.activities.home.UserHomeActivity;
import com.sinhviencafemanagement.dao.SessionDAO;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.database.CreateDatabase;
import com.sinhviencafemanagement.models.User;

public class LoginReceiver {

    private final Context context;
    private final UserDAO userDAO;
    private final SessionDAO sessionDAO;
    private final SharedPreferences prefs;

    public LoginReceiver(Context context,
                         UserDAO userDAO,
                         SessionDAO sessionDAO,
                         SharedPreferences prefs) {
        this.context = context;
        this.userDAO = userDAO;
        this.sessionDAO = sessionDAO;
        this.prefs = prefs;
    }

    public void login(String input, String password, boolean remember) {

        if (!userDAO.checkLogin(input, password)) {
            Toast.makeText(context, "Tên tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDAO.getUserByUsernameOrEmail(input);
        if (user == null) return;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", user.getUserId());

        if (remember) {
            long expiredAt = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000;
            String token = sessionDAO.createSession(user.getUserId(), expiredAt);
            editor.putString("session_token", token);
        }

        editor.putString("saved_username", input);
        editor.apply();

        Intent intent = (user.getRoleId() == CreateDatabase.ROLE_ADMIN)
                ? new Intent(context, AdminHomeActivity.class)
                : new Intent(context, UserHomeActivity.class);

        context.startActivity(intent);
    }
}
