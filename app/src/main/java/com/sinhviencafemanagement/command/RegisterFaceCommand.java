package com.sinhviencafemanagement.command;

public class RegisterFaceCommand implements Command {
    private final FaceRegisterReceiver receiver;

    public RegisterFaceCommand(FaceRegisterReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.registerFace();
    }
}
