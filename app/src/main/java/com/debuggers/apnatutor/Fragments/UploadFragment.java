package com.debuggers.apnatutor.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.debuggers.apnatutor.Adapters.PagerAdapter;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentUploadBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class UploadFragment extends Fragment {
    FragmentUploadBinding binding;

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUploadBinding.inflate(inflater, container, false);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new CourseUploadFragment());
        fragments.add(new VideoUploadFragment());

        binding.uploadTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.uploadViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.uploadViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.uploadTab.selectTab(binding.uploadTab.getTabAt(position));
            }
        });
        binding.uploadViewpager.setAdapter(new PagerAdapter(getChildFragmentManager(), getLifecycle(), fragments));

        return binding.getRoot();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(false);
    }
}