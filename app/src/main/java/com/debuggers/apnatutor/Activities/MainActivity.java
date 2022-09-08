package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.PREFERENCES;
import static com.debuggers.apnatutor.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.databinding.ActivityMainBinding;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (PREFERENCES.contains("EMAIL") && PREFERENCES.contains("PASSWORD")) {
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                if (ME.getType().equals(User.TEACHER)) {
                    startActivity(new Intent(this, MainTeacherActivity.class));
                } else {
                    startActivity(new Intent(this, MainStudentsActivity.class));
                }
                finish();
            }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show()) {
                @Override
                public byte[] getBody() {
                    Map<String, String> body = new HashMap<>();
                    body.put("email", PREFERENCES.getString("EMAIL", ""));
                    body.put("password", PREFERENCES.getString("PASSWORD", ""));
                    return new JSONObject(body).toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        } else {
            new Handler().postDelayed(() -> startActivity(new Intent(this, AuthenticationActivity.class)), 2000);
        }
    }
}

// Quiz -- done
// Video Layout -- done
// Leaderboard -- done
// Teacher upload -- Pritam design
//Library UI 

