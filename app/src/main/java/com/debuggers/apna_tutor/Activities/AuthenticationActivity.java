package com.debuggers.apna_tutor.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.debuggers.apna_tutor.Adapters.PagerAdapter;
import com.debuggers.apna_tutor.Fragments.LoginFragment;
import com.debuggers.apna_tutor.Fragments.SignupFragment;
import com.debuggers.apna_tutor.databinding.ActivityAuthenticationBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AuthenticationActivity extends AppCompatActivity {
    ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new LoginFragment());
        fragments.add(new SignupFragment());

        binding.authenticationTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.authenticationViewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.authenticationViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.authenticationTab.selectTab(binding.authenticationTab.getTabAt(position));
            }
        });

        binding.authenticationViewpager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getLifecycle(), fragments));
    }
}