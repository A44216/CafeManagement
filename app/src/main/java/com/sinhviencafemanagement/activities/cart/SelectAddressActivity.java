package com.sinhviencafemanagement.activities.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sinhviencafemanagement.R;
import com.sinhviencafemanagement.adapters.customer.AddressAdapter;
import com.sinhviencafemanagement.dao.AddressDAO;
import com.sinhviencafemanagement.models.Address;
import com.sinhviencafemanagement.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class SelectAddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressSelectedListener {

    private RecyclerView rcvAddresses;
    private AddressAdapter addressAdapter;
    private List<Address> addressList = new ArrayList<>();
    private AddressDAO addressDAO;
    private SessionManager sessionManager;
    private TextView tvEmpty;

    public static final String EXTRA_SELECTED_ADDRESS_ID = "EXTRA_SELECTED_ADDRESS_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);

        initView();
        initData();
        setupRecyclerView();
        setEventListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses(); // Tải lại danh sách địa chỉ mỗi khi quay lại màn hình này
    }

    private void initView() {
        rcvAddresses = findViewById(R.id.rcvAddresses);
        tvEmpty = findViewById(R.id.tvEmpty);
    }private void initData() {
        addressDAO = new AddressDAO(this);
        sessionManager = new SessionManager(this);
    }

    private void setupRecyclerView() {
        addressAdapter = new AddressAdapter(this, addressList, this, true); // true: bật chế độ chọn
        rcvAddresses.setLayoutManager(new LinearLayoutManager(this));
        rcvAddresses.setAdapter(addressAdapter);
    }

    private void setEventListeners() {
        ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> finish());

        Button btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(v ->
                startActivity(new Intent(this, AddAddressActivity.class))
        );
    }

    private void loadAddresses() {
        int userId = sessionManager.getUserId();
        if (userId != -1) {
            List<Address> addresses = addressDAO.getAddressesByUserId(userId);
            addressList.clear();
            addressList.addAll(addresses);
            if (addressList.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rcvAddresses.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rcvAddresses.setVisibility(View.VISIBLE);
            }

            addressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddressSelected(Address address) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SELECTED_ADDRESS_ID, address.getAddressId());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
















