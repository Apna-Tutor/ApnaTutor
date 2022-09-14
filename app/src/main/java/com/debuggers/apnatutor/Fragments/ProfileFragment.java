package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.NOTIFICATION_CHANNEL_ID;
import static com.debuggers.apnatutor.App.PREFERENCES;
import static com.debuggers.apnatutor.App.QUEUE;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.Activities.ActivitySettings;
import com.debuggers.apnatutor.Activities.MainActivity;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Helpers.MultipartUploadRequest;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentProfileBinding;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            try {
                upload(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);


        binding.buttonChangeAvatar.setOnClickListener(view -> launcher.launch("image/*"));

        binding.settingsBtn.setOnClickListener(v-> startActivity(new Intent(requireContext(), ActivitySettings.class)));
        binding.faqsBtn.setOnClickListener(v-> {

        });
        binding.logoutBtn.setOnClickListener(v-> {
            PREFERENCES.edit().remove("EMAIL").remove("PASSWORD").apply();
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finishAffinity();
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.done).setVisible(false);
    }

    private void upload(Uri thumbnail) throws IOException {
        Cursor cursor = requireContext().getContentResolver().query(thumbnail, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        MultipartUploadRequest.DataPart avatarData = new MultipartUploadRequest.DataPart(
                cursor.getString(nameIndex),
                requireContext().getContentResolver().openInputStream(thumbnail),
                requireContext().getContentResolver().getType(thumbnail));
        cursor.close();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        final NotificationCompat.Builder progressNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Uploading image...")
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setOnlyAlertOnce(true);
        notificationManager.notify(1, progressNotification.build());

        QUEUE.add(new MultipartUploadRequest(Request.Method.PUT, API.UPLOAD_AVATAR, url -> {
            progressNotification.setContentText("Updating profile...");
            notificationManager.notify(1, progressNotification.build());

            QUEUE.add(new JsonObjectRequest(Request.Method.PATCH, String.format("%s?user=%s", API.USER_UPDATE, ME.get_id()), null, response -> {
                ME = new Gson().fromJson(response.toString(), User.class);
                try {
                    Glide.with(requireContext()).load(ME.getAvatar()).placeholder(R.drawable.ic_profile).into(binding.userDP);
                } catch (Exception e) {e.printStackTrace();}
                notificationManager.cancel(1);
                NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Profile picture updated successfully!");
                notificationManager.notify(2, completeNotification.build());
            }, error -> {
                notificationManager.cancel(1);
                NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Could not add update profile picture!")
                        .setContentText(API.parseVolleyError(error));
                notificationManager.notify(2, completeNotification.build());
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("avatar", url);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        }, error -> {
            notificationManager.cancel(1);
            NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Could not add new video!")
                    .setContentText(API.parseVolleyError(error));
            notificationManager.notify(2, completeNotification.build());
        }) {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> body = new HashMap<>();
                body.put("avatar", avatarData);
                return body;
            }
        }).setRetryPolicy(new DefaultRetryPolicy());
    }

    private void updateUi() {
        Glide.with(requireContext()).load(ME.getAvatar()).placeholder(R.drawable.ic_profile).into(binding.userDP);
        binding.userName.setText(ME.getName());
        binding.userEmail.setText(ME.getEmail());
        binding.userType.setText(ME.getType());
    }
}