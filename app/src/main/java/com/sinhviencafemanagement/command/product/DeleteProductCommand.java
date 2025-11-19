package com.sinhviencafemanagement.command.product;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.ProductDAO;

import java.util.function.Consumer;

public class DeleteProductCommand implements Command {
    private final ProductDAO productDAO;
    private final int productId;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public DeleteProductCommand(ProductDAO dao, int productId, Consumer<Boolean> callback) {
        this.productDAO = dao;
        this.productId = productId;
        this.callback = callback;
    }

    // Constructor không callback
    public DeleteProductCommand(ProductDAO dao, int productId) {
        this(dao, productId, null);
    }

    @Override
    public void execute() {
        int result = productDAO.deleteProduct(productId); // số dòng bị xóa
        if (callback != null) {
            callback.accept(result > 0); // true nếu xóa thành công
        }
    }

}
