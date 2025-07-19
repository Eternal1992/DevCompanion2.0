package com.ethereon.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DebugPagerAdapter extends FragmentStateAdapter {

    private final int tabCount;

    public DebugPagerAdapter(@NonNull Fragment fragment, int tabCount) {
        super(fragment);
        this.tabCount = tabCount;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return LogViewerFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return tabCount;
    }
}