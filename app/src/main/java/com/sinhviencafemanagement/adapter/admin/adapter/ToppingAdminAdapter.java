package com.sinhviencafemanagement.adapter.admin.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapter.admin.viewholder.ToppingAdminVH;
import com.sinhviencafemanagement.models.Topping;

import java.util.List;

public class ToppingAdminAdapter extends RecyclerView.Adapter<ToppingAdminVH> {

    public interface OnToppingActionListener {
        void onEdit(Topping topping);
        void onDelete(Topping topping);
    }

    private final List<Topping> toppingList;
    private OnToppingActionListener listener;

    public ToppingAdminAdapter(List<Topping> toppingList) {
        this.toppingList = toppingList;
    }

    public void setOnToppingActionListener(OnToppingActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToppingAdminVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topping_admin, parent, false);
        return new ToppingAdminVH(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ToppingAdminVH holder, int position) {
        Topping t = toppingList.get(position);
        holder.tvName.setText(t.getToppingName());
        holder.tvPrice.setText(t.getPrice() + " VND");

        holder.ivEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(t);
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(t);
        });
    }

    @Override
    public int getItemCount() {
        return toppingList.size();
    }
}
