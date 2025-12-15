package com.sinhviencafemanagement.activities.home.order;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.UserHomeActivity;

public class TrackOrderActivity extends AppCompatActivity {
    private ImageView imgBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {navigateToHome();});
    }

    private void navigateToHome() {
        Intent intent = new Intent(TrackOrderActivity.this, UserHomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
