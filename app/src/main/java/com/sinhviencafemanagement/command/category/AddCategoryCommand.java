package com.sinhviencafemanagement.command.category;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.CategoryDAO;
import com.sinhviencafemanagement.models.Category;

import java.util.function.Consumer;

public class AddCategoryCommand implements Command {
    private final CategoryDAO categoryDAO;
    private final Category category;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public AddCategoryCommand(CategoryDAO dao, Category category, Consumer<Boolean> callback) {
        this.categoryDAO = dao;
        this.category = category;
        this.callback = callback;
    }

    // Constructor không callback
    public AddCategoryCommand(CategoryDAO dao, Category category) {
        this(dao, category, null);
    }

    @Override
    public void execute() {
        long id = categoryDAO.addCategory(category);
        if (callback != null) {
            callback.accept(id != -1);
        }
    }

}
