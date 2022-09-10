package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.PREFERENCES;
import static com.debuggers.apnatutor.App.QUEUE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.databinding.ActivitySettingsBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ActivitySettings extends AppCompatActivity {
    ActivitySettingsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.settingsToolbar);

        binding.settingsToolbar.setNavigationOnClickListener(view -> finish());

        binding.createrMode.setChecked(ME.getType().equals(User.TEACHER));

        binding.updateProfile.setOnClickListener(view -> {
            startActivity(new Intent(this,ActivityUpdateProfile.class));
        });

        binding.changePassword.setOnClickListener(view -> {
            startActivity(new Intent(this,ActivityChangePassword.class));
        });

        // Creator Mode Switch
        binding.createrMode.setOnCheckedChangeListener((compoundButton, b) -> {
            compoundButton.setEnabled(false);
            String type;
            if (b) {
                type = User.TEACHER;
            } else {
                type = User.STUDENT;
            }
            if (!type.equals(ME.getType())) {
                QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s", API.USER_UPDATE, ME.get_id()), null, response -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finishAffinity();
                }, error -> {
                    compoundButton.setEnabled(true);
                    compoundButton.setChecked(!b);
                    Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    public byte[] getBody() {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("type", type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return object.toString().getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }
        });
    }
}