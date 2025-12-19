package com.sinhviencafemanagement.adapters.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.models.Topping;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ToppingAdapter extends RecyclerView.Adapter<ToppingAdapter.ToppingViewHolder> {

    private final List<Topping> toppingList;
    private final Context context;
    private final OnToppingCheckedChangeListener listener;

    public interface OnToppingCheckedChangeListener {
        void onToppingChanged();
    }

    public ToppingAdapter(Context context, List<Topping> toppingList, OnToppingCheckedChangeListener listener) {
        this.context = context;
        this.toppingList = toppingList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topping, parent, false);
        return new ToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingViewHolder holder, int position) {
        Topping currentTopping = toppingList.get(position);

        holder.tvToppingName.setText(currentTopping.getToppingName());

        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        holder.tvToppingPrice.setText("+" + format.format(currentTopping.getPrice()) + "đ");

        // Gỡ listener cũ để tránh gọi lại khi gán giá trị
        holder.cbTopping.setOnCheckedChangeListener(null);
        holder.cbTopping.setChecked(currentTopping.isChecked());

        // Gán lại listener mới
        holder.cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {currentTopping.setChecked(isChecked);
            if (listener != null) {
                listener.onToppingChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return toppingList.size();
    }

    public static class ToppingViewHolder extends RecyclerView.ViewHolder {
        TextView tvToppingName, tvToppingPrice;
        CheckBox cbTopping;

        public ToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvToppingName = itemView.findViewById(R.id.tvToppingName);
            tvToppingPrice = itemView.findViewById(R.id.tvToppingPrice);
            cbTopping = itemView.findViewById(R.id.cbTopping);
        }
    }
}











