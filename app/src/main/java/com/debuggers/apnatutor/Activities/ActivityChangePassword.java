package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.PREFERENCES;
import static com.debuggers.apnatutor.App.QUEUE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityChangePasswordBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ActivityChangePassword extends AppCompatActivity {
    ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.updatePasswordToolbar);
        binding.updatePasswordToolbar.setNavigationOnClickListener(view -> finish());

        binding.updatePassBtn.setOnClickListener(view -> {
            if (binding.oldPass.getText() == null || binding.oldPass.getText().toString().isEmpty()) {
                binding.oldPass.setError("Please enter your current password!");
                return;
            }
            if (!binding.oldPass.getText().toString().equals(ME.getPassword())) {
                binding.oldPass.setError("Your current password doesn't match!");
                return;
            }
            if (binding.newPass1.getText() == null || binding.newPass1.getText().toString().trim().length() < 6) {
                binding.newPass1.setError("Enter a valid password of minimum length 6!");
                return;
            }
            if (binding.newPass2.getText() == null || !binding.newPass2.getText().toString().trim().equals(binding.newPass1.getText().toString().trim())) {
                binding.newPass2.setError("Passwords doesn't match!");
                return;
            }

            binding.updatePassBtn.setEnabled(false);
            QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s", API.USER_UPDATE, ME.get_id()), null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                if (PREFERENCES.contains("PASSWORD")) PREFERENCES.edit().putString("PASSWORD", ME.getPassword()).apply();
                binding.updatePassBtn.setEnabled(true);
            }, error -> {
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                binding.updatePassBtn.setEnabled(true);
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("password", binding.newPass1.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });
    }
}