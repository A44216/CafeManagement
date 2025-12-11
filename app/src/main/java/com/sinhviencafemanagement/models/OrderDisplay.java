package com.sinhviencafemanagement.models;

// Lớp này dùng để hiển thị đơn hàng trong danh sách admin
public class OrderDisplay {
    private int orderId;
    private int userId;
    private String orderDate;
    private String status;
    private double totalPrice;
    private int tableId;
    private String userEmail;       // email khách
    private String productDescription; // mô tả sản phẩm
    private String imagePath;       // path ảnh sản phẩm đầu tiên
    private int imageResId;         // resource id ảnh nếu có

    public OrderDisplay(int orderId, int userId, String orderDate, String status,
                        double totalPrice, int tableId, String userEmail,
                        String productDescription, String imagePath, int imageResId) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.tableId = tableId;
        this.userEmail = userEmail;
        this.productDescription = productDescription;
        this.imagePath = imagePath;
        this.imageResId = imageResId;
    }

    // --- Getter và Setter ---
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getProductDescription() { return productDescription; }
    public void setProductDescription(String productDescription) { this.productDescription = productDescription; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
