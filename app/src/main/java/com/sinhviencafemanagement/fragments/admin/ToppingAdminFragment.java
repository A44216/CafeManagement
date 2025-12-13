package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.topping.UpdateToppingActivity;
import com.sinhviencafemanagement.adapter.admin.adapter.ToppingAdminAdapter;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.Topping;

import java.util.List;

public class ToppingAdminFragment extends Fragment implements ToppingAdminAdapter.OnToppingActionListener {

    private ToppingDAO toppingDAO;
    private List<Topping> list;
    private ToppingAdminAdapter adapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_topping_admin, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        toppingDAO = new ToppingDAO(requireContext());

        RecyclerView rcv = view.findViewById(R.id.rcvToppingAdmin);
        rcv.setLayoutManager(new LinearLayoutManager(getContext()));

        list = toppingDAO.getAllToppings();
        adapter = new ToppingAdminAdapter(list, this);
        rcv.setAdapter(adapter);
    }

    @Override
    public void onEdit(Topping topping) {
        Intent i = new Intent(requireContext(), UpdateToppingActivity.class);
        i.putExtra("topping", topping);
        startActivity(i);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDelete(Topping topping) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Xóa topping")
                .setMessage("Bạn chắc chắn muốn xóa?")
                .setPositiveButton("Xóa", (d, w) -> {
                    toppingDAO.deleteTopping(topping.getToppingId());
                    list.remove(topping);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}