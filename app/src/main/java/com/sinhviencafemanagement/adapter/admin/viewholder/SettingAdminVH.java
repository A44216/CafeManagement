package com.sinhviencafemanagement.adapter.admin.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;

public class SettingAdminVH extends RecyclerView.ViewHolder {

    public TextView tvTitle;

    public SettingAdminVH(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tvTitle);
    }
}
