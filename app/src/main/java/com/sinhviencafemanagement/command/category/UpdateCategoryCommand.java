package com.sinhviencafemanagement.command.category;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import java.util.function.Consumer;

public class UpdateCategoryCommand implements Command{
    private final CategoryDAO categoryDAO;
    private final Category category;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public UpdateCategoryCommand(CategoryDAO dao, Category category, Consumer<Boolean> callback) {
        this.categoryDAO = dao;
        this.category = category;
        this.callback = callback;
    }

    // Constructor không callback
    public UpdateCategoryCommand(CategoryDAO dao, Category category) {
        this(dao, category, null);
    }

    @Override
    public void execute() {
        int rows = categoryDAO.updateCategory(category);
        if (callback != null) {
            callback.accept(rows > 0);
        }
    }

}
