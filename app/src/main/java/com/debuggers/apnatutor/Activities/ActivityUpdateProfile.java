package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.PREFERENCES;
import static com.debuggers.apnatutor.App.QUEUE;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityUpdateProfileBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ActivityUpdateProfile extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.updateProfileToolbar);
        binding.updateProfileToolbar.setNavigationOnClickListener(view -> finish());

        binding.name.setText(ME.getName());
        binding.email.setText(ME.getEmail());

        binding.updateProfileBtn.setOnClickListener(view -> {
            if (binding.name.getText() == null || binding.name.getText().toString().trim().length() < 6) {
                binding.name.setError("Name is required!");
                return;
            }
            if (binding.email.getText() == null || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
                binding.email.setError("Email is required!");
                return;
            }
            if (binding.pass.getText() == null || binding.pass.getText().toString().isEmpty()) {
                binding.pass.setError("Please enter your current password!");
                return;
            }
            if (!binding.pass.getText().toString().equals(ME.getPassword())) {
                binding.pass.setError("Your current password doesn't match!");
                return;
            }

            binding.updateProfileBtn.setEnabled(false);
            QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s", API.USER_UPDATE, ME.get_id()), null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                if (PREFERENCES.contains("EMAIL")) PREFERENCES.edit().putString("EMAIL", ME.getEmail()).apply();
                binding.updateProfileBtn.setEnabled(true);
            }, error -> {
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                binding.updateProfileBtn.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("name", binding.name.getText().toString().trim());
                        object.put("email", binding.email.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

    }
}