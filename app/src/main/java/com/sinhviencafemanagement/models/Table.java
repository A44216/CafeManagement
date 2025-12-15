package com.sinhviencafemanagement.models;

import androidx.annotation.NonNull;

public class Table {
    private int tableId;          // table_id
    private String name;     // table_name
    private String status;        // status (available, occupied,...)

    // Constructor đầy đủ
    public Table(int tableId, String name, String status) {
        this.tableId = tableId;
        this.name = name;
        this.status = status;
    }

    public Table() {

    }

    // Constructor thêm mới (không cần tableId, SQLite tự sinh)
    public Table(String name, String status) {
        this.name = name;
        this.status = status;
    }

    // Getter và Setter
    public int getTableId() {
        return tableId;
    }
    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "Table{" +
                "tableId=" + tableId +
                ", tableName='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

}
