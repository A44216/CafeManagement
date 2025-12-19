package com.sinhviencafemanagement.activities.home.topping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapters.admin.adapter.ToppingAdminAdapter;
import com.sinhviencafemanagement.dao.ToppingDAO;
import com.sinhviencafemanagement.models.Topping;

import java.util.ArrayList;
import java.util.List;

public class ToppingActivity extends AppCompatActivity {

    private RecyclerView rcvTopping;
    private ImageView ivBack;
    private FloatingActionButton fabAdd;
    private TextInputEditText etSearch;

    private ToppingAdminAdapter adapter;
    private final List<Topping> toppingList = new ArrayList<>();
    private final List<Topping> displayedList = new ArrayList<>();
    private ToppingDAO toppingDAO;

    private String currentKeyword = "";

    private final ActivityResultLauncher<Intent> addLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Topping t = (Topping) result.getData().getSerializableExtra("newTopping");
                            if (t != null) {
                                toppingList.add(0, t);
                                filterTopping(currentKeyword);
                                rcvTopping.scrollToPosition(0);
                            }
                        }
                    });

    private final ActivityResultLauncher<Intent> updateLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Topping updated = (Topping) result.getData().getSerializableExtra("updatedTopping");
                            if (updated == null) return;

                            for (int i = 0; i < toppingList.size(); i++) {
                                if (toppingList.get(i).getToppingId() == updated.getToppingId()) {
                                    toppingList.set(i, updated);
                                    break;
                                }
                            }
                            filterTopping(currentKeyword);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topping);

        initViews();
        toppingDAO = new ToppingDAO(this);

        loadToppings();
        setUpRecyclerView();
        setUpListeners();
    }

    private void initViews() {
        rcvTopping = findViewById(R.id.rcvToppingAdmin);
        ivBack = findViewById(R.id.ivBack);
        fabAdd = findViewById(R.id.fabAddTopping);
        etSearch = findViewById(R.id.etSearchTopping); // Thêm EditText tìm kiếm
    }

    private void setUpRecyclerView() {
        adapter = new ToppingAdminAdapter(displayedList);
        rcvTopping.setLayoutManager(new LinearLayoutManager(this));
        rcvTopping.setAdapter(adapter);

        adapter.setOnToppingActionListener(new ToppingAdminAdapter.OnToppingActionListener() {
            @Override
            public void onEdit(Topping topping) {
                Intent intent = new Intent(ToppingActivity.this, UpdateToppingActivity.class);
                intent.putExtra("topping", topping);
                updateLauncher.launch(intent);
            }

            @Override
            public void onDelete(Topping topping) {
                new MaterialAlertDialogBuilder(ToppingActivity.this)
                        .setTitle("Xóa topping")
                        .setMessage("Bạn chắc chắn muốn xóa \"" + topping.getToppingName() + "\"?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            int index = displayedList.indexOf(topping);
                            if (index >= 0) {
                                new Thread(() -> {
                                    toppingDAO.deleteTopping(topping.getToppingId());
                                    runOnUiThread(() -> {
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
    }

    private void setUpListeners() {
        ivBack.setOnClickListener(v -> finish());

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToppingActivity.class);
            addLauncher.launch(intent);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTopping(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadToppings() {
        new Thread(() -> {
            toppingList.clear();
            toppingList.addAll(toppingDAO.getAllToppings());
            runOnUiThread(() -> {
                displayedList.clear();
                displayedList.addAll(toppingList);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterTopping(String keyword) {
        currentKeyword = keyword.toLowerCase();
        displayedList.clear();

        if (currentKeyword.isEmpty()) {
            displayedList.addAll(toppingList);
        } else {
            for (Topping t : toppingList) {
                if (t.getToppingName().toLowerCase().contains(currentKeyword)) {
                    displayedList.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

}
