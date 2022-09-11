package com.debuggers.apnatutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityQuizBinding;

public class ActivityQuiz extends AppCompatActivity {
    ActivityQuizBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.quizToolbar);

        binding.quizToolbar.setNavigationOnClickListener(view -> finish());
    }
}