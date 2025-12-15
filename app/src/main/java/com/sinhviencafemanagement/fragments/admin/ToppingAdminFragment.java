package com.sinhviencafemanagement.fragments.admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.activities.home.topping.UpdateToppingActivity;
import com.sinhviencafemanagement.adapter.admin.adapter.ToppingAdminAdapter;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.Topping;

import java.util.ArrayList;
import java.util.List;

public class ToppingAdminFragment extends Fragment {

    private ToppingDAO toppingDAO;
    private ToppingAdminAdapter adapter;
    private List<Topping> toppingList = new ArrayList<>();
    private final List<Topping> displayedList = new ArrayList<>();
    private String currentKeyword = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toppingDAO = new ToppingDAO(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_topping_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rcvToppingAdmin);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ToppingAdminAdapter(displayedList);
        rv.setAdapter(adapter);

        // Callback edit/delete
        adapter.setOnToppingActionListener(new ToppingAdminAdapter.OnToppingActionListener() {
            @Override
            public void onEdit(Topping topping) {
                Intent i = new Intent(requireContext(), UpdateToppingActivity.class);
                i.putExtra("topping", topping);
                updateLauncher.launch(i);
            }

            @Override
            public void onDelete(Topping topping) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xóa topping")
                        .setMessage("Bạn chắc chắn muốn xóa \"" + topping.getToppingName() + "\"?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            int index = displayedList.indexOf(topping);
                            if (index >= 0) {
                                new Thread(() -> {
                                    toppingDAO.deleteTopping(topping.getToppingId());
                                    requireActivity().runOnUiThread(() -> {
                                        toppingList.remove(topping);
                                        displayedList.remove(index);
                                        adapter.notifyItemRemoved(index);
                                    });
                                }).start();
                            }
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });

        loadToppings();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadToppings() {
        new Thread(() -> {
            toppingList = toppingDAO.getAllToppings();
            requireActivity().runOnUiThread(() -> {
                displayedList.clear();
                displayedList.addAll(toppingList);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterTopping(String keyword) {
        currentKeyword = keyword;
        displayedList.clear();
        if (keyword.isEmpty()) {
            displayedList.addAll(toppingList);
        } else {
            for (Topping t : toppingList) {
                if (t.getToppingName().toLowerCase().contains(keyword.toLowerCase())) {
                    displayedList.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void addTopping(Topping t) {
        toppingList.add(t);
        if (t.getToppingName().toLowerCase().contains(currentKeyword.toLowerCase())) {
            displayedList.add(t);
            adapter.notifyItemInserted(displayedList.size() - 1);
        }
    }

    public void updateTopping(Topping t) {
        for (int i = 0; i < toppingList.size(); i++) {
            if (toppingList.get(i).getToppingId() == t.getToppingId()) {
                toppingList.set(i, t);
                break;
            }
        }

        int posInDisplayed = -1;
        for (int i = 0; i < displayedList.size(); i++) {
            if (displayedList.get(i).getToppingId() == t.getToppingId()) {
                posInDisplayed = i;
                break;
            }
        }

        boolean match = t.getToppingName().toLowerCase().contains(currentKeyword.toLowerCase());

        if (match) {
            if (posInDisplayed >= 0) {
                displayedList.set(posInDisplayed, t);
                adapter.notifyItemChanged(posInDisplayed);
            } else {
                displayedList.add(t);
                adapter.notifyItemInserted(displayedList.size() - 1);
            }
        } else {
            if (posInDisplayed >= 0) {
                displayedList.remove(posInDisplayed);
                adapter.notifyItemRemoved(posInDisplayed);
            }
        }
    }

    private final ActivityResultLauncher<Intent> updateLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            Topping updated = (Topping) result.getData().getSerializableExtra("updatedTopping");
                            if (updated != null) updateTopping(updated);
                        }
                    });
}
