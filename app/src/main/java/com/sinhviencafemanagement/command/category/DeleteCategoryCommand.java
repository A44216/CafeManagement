package com.sinhviencafemanagement.command.category;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.CategoryDAO;

import java.util.function.Consumer;

public class DeleteCategoryCommand implements Command{
    private final CategoryDAO categoryDAO;
    private final int categoryId;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public DeleteCategoryCommand(CategoryDAO dao, int categoryId, Consumer<Boolean> callback) {
        this.categoryDAO = dao;
        this.categoryId = categoryId;
        this.callback = callback;
    }

    // Constructor không callback
    public DeleteCategoryCommand(CategoryDAO dao, int categoryId) {
        this(dao, categoryId, null);
    }

    @Override
    public void execute() {
        int rows = categoryDAO.deleteCategory(categoryId); // trả về số dòng bị xóa
        if (callback != null) {
            callback.accept(rows > 0); // true nếu xóa thành công
        }
    }

}
