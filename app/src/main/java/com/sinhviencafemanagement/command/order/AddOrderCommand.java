package com.sinhviencafemanagement.command.order;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.OrderDAO;
import com.sinhviencafemanagement.models.Order;

import java.util.function.Consumer;


public class AddOrderCommand implements Command{
    private final OrderDAO orderDAO;
    private final Order order;
    private final Consumer<Boolean> callback; // callback nhận true/false

    // Constructor có callback
    public AddOrderCommand(OrderDAO dao, Order order, Consumer<Boolean> callback) {
        this.orderDAO = dao;
        this.order = order;
        this.callback = callback;
    }

    // Constructor không callback, dùng khi không cần thông báo UI
    public AddOrderCommand(OrderDAO dao, Order order) {
        this(dao, order, null);
    }

    @Override
    public void execute() {
        long id = orderDAO.addOrder(order); // thêm vào DB, trả về id
        if (callback != null) callback.accept(id != -1); // true nếu thành công
    }

}
