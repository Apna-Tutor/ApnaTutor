package com.debuggers.apna_tutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.debuggers.apna_tutor.R;
import com.debuggers.apna_tutor.databinding.ActivityPlayerBinding;

public class ActivityPlayer extends AppCompatActivity {
    ActivityPlayerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}