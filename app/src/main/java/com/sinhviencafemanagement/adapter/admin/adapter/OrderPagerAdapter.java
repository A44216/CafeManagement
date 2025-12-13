package com.sinhviencafemanagement.adapter.admin.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sinhviencafemanagement.fragments.admin.CompletedOrderFragment;
import com.sinhviencafemanagement.fragments.admin.PendingOrderFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new PendingOrderFragment();
        else return new CompletedOrderFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // Pending + Completed
    }
}
