package com.debuggers.apnatutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityPlayerBinding;

public class ActivityPlayer extends AppCompatActivity {
    ActivityPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}