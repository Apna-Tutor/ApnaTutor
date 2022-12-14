package com.debuggers.apnatutor.Activities;

import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.debuggers.apnatutor.Adapters.PagerAdapter;
import com.debuggers.apnatutor.Fragments.ProfileFragment;
import com.debuggers.apnatutor.Fragments.TeacherHomeFragment;
import com.debuggers.apnatutor.Fragments.UploadFragment;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityTeacherMainBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainTeacherActivity extends AppCompatActivity {
    ActivityTeacherMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);// down 3 option

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new TeacherHomeFragment());
        fragments.add(new UploadFragment());
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
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onPrepareOptionsMenu(menu);
    }
}