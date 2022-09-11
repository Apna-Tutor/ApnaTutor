package com.debuggers.apnatutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityNotesBinding;

public class ActivityNotes extends AppCompatActivity {
    ActivityNotesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.notesToolbar);

        binding.notesToolbar.setNavigationOnClickListener(view -> finish());




    }
}