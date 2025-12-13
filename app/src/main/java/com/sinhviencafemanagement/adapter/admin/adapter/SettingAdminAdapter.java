package com.sinhviencafemanagement.adapter.admin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.SettingAdminVH;

import java.util.List;

public class SettingAdminAdapter extends RecyclerView.Adapter<SettingAdminVH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<String> titles;
    private final OnItemClickListener listener;

    public SettingAdminAdapter(List<String> titles, OnItemClickListener listener) {
        this.titles = titles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_setting_admin, parent, false);
        return new SettingAdminVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingAdminVH holder, int position) {
        holder.tvTitle.setText(titles.get(position));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
