package com.sinhviencafemanagement.adapter.admin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.ToppingAdminVH;
import com.sinhviencafemanagement.models.Topping;

import java.util.List;

public class ToppingAdminAdapter
        extends RecyclerView.Adapter<ToppingAdminVH> {

    public interface OnToppingActionListener {
        void onEdit(Topping topping);
        void onDelete(Topping topping);
    }

    private final List<Topping> list;
    private final OnToppingActionListener listener;

    public ToppingAdminAdapter(List<Topping> list,
                               OnToppingActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToppingAdminVH onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        return new ToppingAdminVH(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_topping_admin, parent, false)
        );
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(
            @NonNull ToppingAdminVH holder, int position) {

        Topping topping = list.get(position);

        holder.tvName.setText(topping.getToppingName());
        holder.tvPrice.setText(topping.getPrice() + " VND");

        holder.ivEdit.setOnClickListener(v -> listener.onEdit(topping));
        holder.ivDelete.setOnClickListener(v -> listener.onDelete(topping));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}