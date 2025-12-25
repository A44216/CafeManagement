package com.sinhviencafemanagement.command;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import com.sinhviencafemanagement.activities.FaceCaptureActivity;

public class FaceRegisterReceiver {
    private final Context context;
    private final ActivityResultLauncher<Intent> launcher;

    public FaceRegisterReceiver(Context context, ActivityResultLauncher<Intent> launcher) {
        this.context = context;
        this.launcher = launcher;
    }

    public void registerFace() {
        Intent intent = new Intent(context, FaceCaptureActivity.class);
        launcher.launch(intent);
    }
}
