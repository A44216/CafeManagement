package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Role {
    private int roleId;         // role_id
    @NonNull
    private String roleName;    // role_name

    // Constructor đầy đủ
    public Role(int roleId, @NonNull String roleName) {
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Role() {

    }

    // Constructor thêm mới (không cần roleId, SQLite tự sinh)
    public Role(@NonNull String roleName) {
        this.roleName = roleName;
    }

    // Getter và Setter
    public int getRoleId() {
        return roleId;
    }
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @NonNull
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(@NonNull String roleName) {
        this.roleName = roleName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                '}';
    }

}
