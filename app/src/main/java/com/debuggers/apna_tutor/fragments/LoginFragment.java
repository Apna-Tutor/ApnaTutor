package com.debuggers.apna_tutor.Fragments;

import static com.debuggers.apna_tutor.App.ME;
import static com.debuggers.apna_tutor.App.PREFERENCES;
import static com.debuggers.apna_tutor.App.QUEUE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apna_tutor.Activities.MainStudentsActivity;
import com.debuggers.apna_tutor.Activities.MainTeacherActivity;
import com.debuggers.apna_tutor.Helpers.API;
import com.debuggers.apna_tutor.Models.User;
import com.debuggers.apna_tutor.databinding.FragmentLoginBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class LoginFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    FragmentLoginBinding binding;

    public LoginFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.login.setOnClickListener(v -> {
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString().trim()).matches()) {
                binding.email.setError("A valid email is required!");
                return;
            }
            if (binding.pass.getText().toString().trim().length() < 6) {
                binding.pass.setError("Enter a valid password of minimum length 6!");
                return;
            }

            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.LOGIN, null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                if (binding.remember.isChecked()) {
                    PREFERENCES.edit().putString("EMAIL", ME.getEmail()).putString("PASSWORD", ME.getPassword()).apply();
                }
                if (ME.getType().equals(User.TEACHER)) {
                    startActivity(new Intent(requireActivity(), MainTeacherActivity.class));
                } else {
                    startActivity(new Intent(requireActivity(), MainStudentsActivity.class));
                }
                requireActivity().finish();
            }, error -> {
                Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email", binding.email.getText().toString().trim());
                        body.put("password", binding.pass.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        });

        return binding.getRoot();
    }
}