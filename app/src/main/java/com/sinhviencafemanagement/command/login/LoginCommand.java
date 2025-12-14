package com.sinhviencafemanagement.command.login;

import com.sinhviencafemanagement.command.Command;

public class LoginCommand implements Command {

    private final LoginReceiver receiver;
    private final String input;
    private final String password;
    private final boolean remember;

    public LoginCommand(LoginReceiver receiver,
                        String input,
                        String password,
                        boolean remember) {
        this.receiver = receiver;
        this.input = input;
        this.password = password;
        this.remember = remember;
    }

    @Override
    public void execute() {
        receiver.login(input, password, remember);
    }
}
