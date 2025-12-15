package com.sinhviencafemanagement.command.product;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.ProductDAO;
import com.sinhviencafemanagement.models.Product;

import java.util.function.Consumer;

public class AddProductCommand implements Command {
    private final ProductDAO productDAO;
    private final Product product;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public AddProductCommand(ProductDAO dao, Product product, Consumer<Boolean> callback) {
        this.productDAO = dao;
        this.product = product;
        this.callback = callback;
    }

    // Constructor không callback
    public AddProductCommand(ProductDAO dao, Product product) {
        this(dao, product, null);
    }

    @Override
    public void execute() {
        long id = productDAO.addProduct(product); // Thêm product, trả về id mới
        if (callback != null) {
            callback.accept(id != -1); // true nếu thêm thành công, false nếu thất bại
        }
    }


}
