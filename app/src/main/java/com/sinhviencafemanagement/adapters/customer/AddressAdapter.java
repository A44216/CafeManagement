package com.sinhviencafemanagement.adapters.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final Context context;
    private final List<Address> addressList;
    private final OnAddressSelectedListener listener;
    private final boolean isSelectionMode;

    public interface OnAddressSelectedListener {
        void onAddressSelected(Address address);
    }

    public AddressAdapter(Context context, List<Address> addressList, OnAddressSelectedListener listener, boolean isSelectionMode) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
        this.isSelectionMode = isSelectionMode;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);

        holder.tvFullName.setVisibility(View.GONE);
        holder.tvPhoneNumber.setVisibility(View.GONE);
        holder.tvFullAddress.setText(address.getAddress());

        if (isSelectionMode) {
            holder.radioSelectAddress.setVisibility(View.VISIBLE);
            holder.imgEditAddress.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddressSelected(address);
                }
            });
        } else {
            // Chế độ xem/quản lý địa chỉ
            holder.radioSelectAddress.setVisibility(View.GONE);
            holder.imgEditAddress.setVisibility(View.VISIBLE);

            // TODO: Xử lý sự kiện click cho nút sửa địa chỉ
            holder.imgEditAddress.setOnClickListener(v -> {
            });
        }
    }

    @Override
    public int getItemCount() {
        return addressList != null ? addressList.size() : 0;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder vẫn giữ các tham chiếu, dù một số có thể bị ẩn đi
        TextView tvFullName, tvPhoneNumber, tvFullAddress;
        ImageView imgEditAddress;
        RadioButton radioSelectAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvFullAddress = itemView.findViewById(R.id.tvFullAddress);
            imgEditAddress = itemView.findViewById(R.id.imgEditAddress);
            radioSelectAddress = itemView.findViewById(R.id.radioSelectAddress);
        }
    }
}
