package com.debuggers.apnatutor.Activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.debuggers.apnatutor.Adapters.PagerAdapter;
import com.debuggers.apnatutor.Fragments.StudentHomeFragment;
import com.debuggers.apnatutor.Fragments.LibraryFragment;
import com.debuggers.apnatutor.Fragments.ProfileFragment;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityStudentsMainBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainStudentsActivity extends AppCompatActivity {
    ActivityStudentsMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentsMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);// down 3 option

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new StudentHomeFragment());
        fragments.add(new LibraryFragment());
        fragments.add(new ProfileFragment());

        binding.mainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.mainViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.mainViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.mainTabs.selectTab(binding.mainTabs.getTabAt(position));
            }
        });
        binding.mainViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
        binding.mainViewpager.setOffscreenPageLimit(fragments.size());
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onPrepareOptionsMenu(menu);
    }
}