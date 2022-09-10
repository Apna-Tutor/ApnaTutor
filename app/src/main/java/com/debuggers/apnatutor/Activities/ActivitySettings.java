package com.debuggers.apnatutor.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivitySettingsBinding;

public class ActivitySettings extends AppCompatActivity {
    ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        binding.updateProfile.setOnClickListener(view -> {
            startActivity(new Intent(ActivitySettings.this,ActivityUpdateProfile.class));
        });

        binding.changePassword.setOnClickListener(view -> {
            startActivity(new Intent(ActivitySettings.this,ActivityChangePassword.class));
        });

        // Creator Mode Switch
    }
}