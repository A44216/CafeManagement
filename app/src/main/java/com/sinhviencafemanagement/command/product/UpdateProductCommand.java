package com.sinhviencafemanagement.command.product;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;

import java.util.function.Consumer;

public class UpdateProductCommand implements Command{
    private final ProductDAO productDAO;
    private final Product product;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public UpdateProductCommand(ProductDAO dao, Product product, Consumer<Boolean> callback) {
        this.productDAO = dao;
        this.product = product;
        this.callback = callback;
    }

    // Constructor không callback, dùng nếu không cần thông báo UI
    public UpdateProductCommand(ProductDAO dao, Product product) {
        this(dao, product, null);
    }

    @Override
    public void execute() {
        int result = productDAO.updateProduct(product);
        if (callback != null) {
            callback.accept(result > 0); // true nếu cập nhật thành công
        }
    }

}
