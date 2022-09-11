package com.debuggers.apnatutor.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityLeaderboardBinding;

public class ActivityLeaderboard extends AppCompatActivity {
    ActivityLeaderboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.leaderboardToolbar);

        binding.leaderboardToolbar.setNavigationOnClickListener(view -> finish());
    }
}