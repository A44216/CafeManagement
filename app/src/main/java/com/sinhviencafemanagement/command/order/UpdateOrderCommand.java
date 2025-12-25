package com.sinhviencafemanagement.command.order;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.models.Order;

import java.util.function.Consumer;

public class UpdateOrderCommand implements Command{
    private final OrderDAO orderDAO;
    private final Order order;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public UpdateOrderCommand(OrderDAO dao, Order order, Consumer<Boolean> callback) {
        this.orderDAO = dao;
        this.order = order;
        this.callback = callback;
    }

    // Constructor không callback
    public UpdateOrderCommand(OrderDAO dao, Order order) {
        this(dao, order, null);
    }

    @Override
    public void execute() {
        int rows = orderDAO.updateOrder(order); // cập nhật DB
        if (callback != null)
            callback.accept(rows > 0); // true nếu có thay đổi
    }

}
