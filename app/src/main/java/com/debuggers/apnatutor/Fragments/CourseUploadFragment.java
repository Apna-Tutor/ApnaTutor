package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.NOTIFICATION_CHANNEL_ID;
import static com.debuggers.apnatutor.App.QUEUE;

import android.app.Notification;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Helpers.MultipartUploadRequest;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentCourseUploadBinding;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CourseUploadFragment extends Fragment {
    FragmentCourseUploadBinding binding;
    Uri thumbnail;
    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            thumbnail = result;
            Cursor cursor = requireContext().getContentResolver().query(thumbnail, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            binding.thumbnail.setImageURI(thumbnail);
            binding.thumbnailName.setText(cursor.getString(nameIndex));
            cursor.close();
        }
    });

    public CourseUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCourseUploadBinding.inflate(inflater, container, false);

        binding.thumbnail.setOnClickListener(view -> {
            launcher.launch("image/*");
        });

        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (binding.courseName.getText().toString().trim().isEmpty()) {
                binding.courseName.setError("Valid course name is required!");
                Toast.makeText(requireContext(), "Valid course name is required!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (thumbnail == null) {
                Toast.makeText(requireContext(), "Please select thumbnail!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.description.getText().toString().trim().isEmpty()) {
                binding.description.setError("Please add a description!");
                Toast.makeText(requireContext(), "Please add a description!", Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                upload();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void upload() throws IOException {
        MultipartUploadRequest.DataPart thumbnailData = new MultipartUploadRequest.DataPart(
                binding.thumbnailName.getText().toString().trim(),
                requireContext().getContentResolver().openInputStream(thumbnail),
                requireContext().getContentResolver().getType(thumbnail));

        Course newCourse = new Course(binding.courseName.getText().toString().trim(), binding.description.toString().trim(), null);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        final NotificationCompat.Builder progressNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Uploading image...")
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setOnlyAlertOnce(true);
        notificationManager.notify(1, progressNotification.build());

        QUEUE.add(new MultipartUploadRequest(Request.Method.PUT, API.UPLOAD_THUMBNAIL, url -> {
            progressNotification.setContentText("Creating new course...");
            notificationManager.notify(1, progressNotification.build());

            newCourse.setThumbnail(url);
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, API.COURSE_ADD, null, response -> {
                notificationManager.cancel(1);
                NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("New course added successfully!");
                notificationManager.notify(2, completeNotification.build());
            }, error -> {
                notificationManager.cancel(1);
                NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Could not add new course!")
                        .setContentText(API.parseVolleyError(error));
                notificationManager.notify(2, completeNotification.build());
            }) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(newCourse).getBytes(StandardCharsets.UTF_8);
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
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> body = new HashMap<>();
                body.put("thumbnail", thumbnailData);
                return body;
            }
        }).setRetryPolicy(new DefaultRetryPolicy());

        thumbnail = null;
        binding.courseName.setText(null);
        binding.thumbnail.setImageURI(null);
        binding.thumbnailName.setText(null);
        binding.description.setText(null);
    }
}