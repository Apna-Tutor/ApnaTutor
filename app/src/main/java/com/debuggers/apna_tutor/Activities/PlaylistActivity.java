package com.debuggers.apna_tutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.debuggers.apna_tutor.databinding.ActivityPlaylistBinding;

public class PlaylistActivity extends AppCompatActivity {
    ActivityPlaylistBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}