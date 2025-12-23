package com.sinhviencafemanagement.command.login;

import com.sinhviencafemanagement.command.Command;

public class LoginInvoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean executeCommand() {
        if (command == null) return false;
        return command.execute();
    }
}