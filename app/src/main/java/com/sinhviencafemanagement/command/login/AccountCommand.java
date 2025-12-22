package com.sinhviencafemanagement.command.login;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.UserDAO;

public class AccountCommand implements Command {
    private final String input; // username hoặc email
    private final String password;
    private final UserDAO userDAO;

    public AccountCommand(String input, String password, UserDAO userDAO) {
        this.input = input;
        this.password = password;
        this.userDAO = userDAO;
    }

    @Override
    public boolean execute() {
        return userDAO.checkLogin(input, password);
    }
}

