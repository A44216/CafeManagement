package com.sinhviencafemanagement.command.order;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.OrderDAO;

import java.util.function.Consumer;

public class DeleteOrderCommand implements Command {
    private final OrderDAO orderDAO;
    private final int orderId;
    private final Consumer<Boolean> callback; // callback nhận true/false


    // Constructor có callback
    public DeleteOrderCommand(OrderDAO dao, int orderId, Consumer<Boolean> callback) {
        this.orderDAO = dao;
        this.orderId = orderId;
        this.callback = callback;
    }

    // Constructor không callback
    public DeleteOrderCommand(OrderDAO dao, int orderId) {
        this(dao, orderId, null);
    }

    @Override
    public void execute() {
        int rows = orderDAO.deleteOrder(orderId);
        if (callback != null) {
            callback.accept(rows > 0);
        }
    }

}
