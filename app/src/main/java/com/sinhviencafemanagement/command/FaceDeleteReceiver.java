package com.sinhviencafemanagement.command;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.sinhviencafemanagement.activities.FaceCaptureActivity;

public class FaceDeleteReceiver {
    private final Context context;
    private final ActivityResultLauncher<Intent> launcher;
    private final Runnable setDeleteModeAction;

    public FaceDeleteReceiver(Context context, ActivityResultLauncher<Intent> launcher, Runnable setDeleteModeAction) {
        this.context = context;
        this.launcher = launcher;
        this.setDeleteModeAction = setDeleteModeAction;
    }

    public void deleteFace() {
        // Đánh dấu là đang xóa
        if (setDeleteModeAction != null) {
            setDeleteModeAction.run();
        }
        Intent intent = new Intent(context, FaceCaptureActivity.class);
        launcher.launch(intent);
    }
}
