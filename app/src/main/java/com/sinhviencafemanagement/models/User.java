package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class User {
    private int userId;        // user_id
    private String fullName;   // full_name
    private String displayName; // display_name
    private String username;   // username
    private String password;   // password
    private String email;      // email
    private String phone;      // phone
    private String gender;     // gender
    private String birthdate;  // birthdate
    private int roleId;        // role_id

    // Constructor đầy đủ
    public User(int userId, String fullName, String displayName, String username, String password,
                String email, String phone, String gender, String birthdate, int roleId) {
        this.userId = userId;
        this.fullName = fullName;
        this.displayName = displayName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.roleId = roleId;
    }

    public User() {

    }

    // Constructor để thêm người dùng mới (không cần userId, tự sinh trong DB)
    public User(String fullName, String displayName, String username, String password,
                String email, String phone, String gender, String birthdate, int roleId) {

        this.fullName = fullName;
        this.displayName = displayName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.birthdate = birthdate;
        this.roleId = roleId;
    }

    // Getter và Setter
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", displayName='" + displayName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", birthdate='" + birthdate + '\'' +
                ", roleId=" + roleId +
                '}';
    }

}
