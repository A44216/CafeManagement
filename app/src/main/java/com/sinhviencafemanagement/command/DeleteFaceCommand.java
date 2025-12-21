package com.sinhviencafemanagement.command;

public class DeleteFaceCommand implements Command {
    private final FaceDeleteReceiver receiver;

    public DeleteFaceCommand(FaceDeleteReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.deleteFace();
    }
}
