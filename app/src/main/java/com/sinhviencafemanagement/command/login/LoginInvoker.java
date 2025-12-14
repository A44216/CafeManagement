package com.sinhviencafemanagement.command.login;

import com.sinhviencafemanagement.command.Command;

public class LoginInvoker {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void login() {
        if (command != null) {
            command.execute();
        }
    }
}
