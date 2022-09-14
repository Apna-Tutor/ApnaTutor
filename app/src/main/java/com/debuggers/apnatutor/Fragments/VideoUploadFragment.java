package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.NOTIFICATION_CHANNEL_ID;
import static com.debuggers.apnatutor.App.QUEUE;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.QuizAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Helpers.MultipartUploadRequest;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.Quiz;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentVideoUploadBinding;
import com.debuggers.apnatutor.databinding.QuizQuestionDialogBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoUploadFragment extends Fragment {
    FragmentVideoUploadBinding binding;
    List<Quiz> quizzes;
    List<Course> courses;
    Uri thumbnail, video;
    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            Cursor cursor = requireContext().getContentResolver().query(result, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String memeType = requireContext().getContentResolver().getType(result);
            if (Pattern.matches("image/\\w*",memeType)) {
                thumbnail = result;
                binding.thumbnail.setImageURI(result);
                binding.thumbnailName.setText(cursor.getString(nameIndex));
            } else if (Pattern.matches("video/\\w*",memeType)){
                video = result;
                binding.video.setVideoURI(result);
                binding.vdoName.setText(cursor.getString(nameIndex));
                binding.video.setOnPreparedListener(mediaPlayer -> {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0, 0);
                    mediaPlayer.start();
                });
            }
            cursor.close();
        }
    });

    public VideoUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoUploadBinding.inflate(inflater, container, false);
        quizzes = new ArrayList<>();
        courses = new ArrayList<>();

        fetchCourses();
        binding.refreshBtn.setOnClickListener(view -> fetchCourses());

        binding.thumbnail.setOnClickListener(view -> launcher.launch("image/*"));

        binding.video.setOnClickListener(view -> launcher.launch("video/*"));

        binding.quizes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.quizes.setAdapter(new QuizAdapter(quizzes));
        binding.addQuiz.setOnClickListener(view -> showQuizDialog());
        binding.description.setOnTouchListener((view, motionEvent) -> {
            if(binding.description.hasFocus()){
                view.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_SCROLL) {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
                }
            }
            return false;
        });

        return binding.getRoot();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.done) {
            if (binding.selectCourse.getSelectedItemPosition() == 0) {
                ((TextView) binding.selectCourse.getSelectedView()).setError("Please select a course!");
                Toast.makeText(requireContext(), "Please select a course!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.videoTitle.getText() == null || binding.videoTitle.getText().toString().trim().isEmpty()) {
                binding.videoTitle.setError("Valid video title is required!");
                Toast.makeText(requireContext(), "Valid video title is required!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (thumbnail == null) {
                Toast.makeText(requireContext(), "Please select a thumbnail!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (video == null) {
                Toast.makeText(requireContext(), "Please select a video!", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (binding.description.getText() == null || binding.description.getText().toString().trim().isEmpty()) {
                binding.description.setError("Valid course name is required!");
                return false;
            }
            try {
                upload(courses.get(binding.selectCourse.getSelectedItemPosition()-1).get_id());
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQuizDialog() {
        QuizQuestionDialogBinding dialogBinding = QuizQuestionDialogBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
        dialog.show();

        List<String> options = new ArrayList<>();
        options.add("Select correct answer");
        options.add(dialogBinding.optionA.getText().toString());
        options.add(dialogBinding.optionB.getText().toString());
        options.add(dialogBinding.optionC.getText().toString());
        options.add(dialogBinding.optionD.getText().toString());

        dialogBinding.optionA.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options.set(1, charSequence.toString());
                ((ArrayAdapter) dialogBinding.answer.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialogBinding.optionB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options.set(2, charSequence.toString());
                ((ArrayAdapter) dialogBinding.answer.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialogBinding.optionC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options.set(3, charSequence.toString());
                ((ArrayAdapter) dialogBinding.answer.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        dialogBinding.optionD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                options.set(4, charSequence.toString());
                ((ArrayAdapter) dialogBinding.answer.getAdapter()).notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialogBinding.answer.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_activated_1, options));

        dialogBinding.cancelNewQuiz.setOnClickListener(view -> dialog.dismiss());

        dialogBinding.submitNewQuiz.setOnClickListener(view -> {
            if (dialogBinding.question.getText() == null || dialogBinding.question.getText().toString().trim().isEmpty()) {
                dialogBinding.question.setError("A valid question is required!");
                return;
            }
            if (dialogBinding.optionA.getText().toString().trim().isEmpty()) {
                dialogBinding.optionA.setError("Please add option A!");
                return;
            }
            if (dialogBinding.optionB.getText().toString().trim().isEmpty()) {
                dialogBinding.optionB.setError("Please add option B!");
                return;
            }
            if (dialogBinding.optionC.getText().toString().trim().isEmpty()) {
                dialogBinding.optionC.setError("Please add option C!");
                return;
            }
            if (dialogBinding.optionD.getText().toString().trim().isEmpty()) {
                dialogBinding.optionD.setError("Please add option D!");
                return;
            }
            if (dialogBinding.answer.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), "Please select a valid answer!", Toast.LENGTH_SHORT).show();
                return;
            }

            quizzes.add(new Quiz(dialogBinding.question.getText().toString().trim(), options.subList(1, options.size()), ((ArrayAdapter<String>)dialogBinding.answer.getAdapter()).getItem(dialogBinding.answer.getSelectedItemPosition())));
            Objects.requireNonNull(binding.quizes.getAdapter()).notifyItemInserted(quizzes.size()-1);
            dialog.dismiss();
        });
    }

    private void fetchCourses() {
        binding.fetchCourseProgress.setVisibility(View.VISIBLE);
        binding.refreshBtn.setVisibility(View.GONE);
        courses.clear();
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, String.format("%s?author=%s", API.COURSES_UPLOADED, ME.get_id()), null, response -> {
            courses = new Gson().fromJson(response.toString(), new TypeToken<List<Course>>(){}.getType());
            List<String> courseNames = courses.stream().map(Course::getTitle).collect(Collectors.toList());
            courseNames.add(0, "Select course");
            binding.selectCourse.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_activated_1, courseNames));
            binding.fetchCourseProgress.setVisibility(View.GONE);
            binding.refreshBtn.setVisibility(View.VISIBLE);
        }, error -> {
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.fetchCourseProgress.setVisibility(View.GONE);
            binding.refreshBtn.setVisibility(View.VISIBLE);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void upload(String courseId) throws IOException {
        MultipartUploadRequest.DataPart thumbnailData = new MultipartUploadRequest.DataPart(
                binding.thumbnailName.getText().toString().trim(),
                requireContext().getContentResolver().openInputStream(thumbnail),
                requireContext().getContentResolver().getType(thumbnail));

        MultipartUploadRequest.DataPart videoData = new MultipartUploadRequest.DataPart(
                binding.vdoName.getText().toString().trim(),
                requireContext().getContentResolver().openInputStream(video),
                requireContext().getContentResolver().getType(video));

        Video newVideo = new Video(Objects.requireNonNull(binding.videoTitle.getText()).toString().trim(), Objects.requireNonNull(binding.description.getText()).toString().trim(), null, null, quizzes);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        final NotificationCompat.Builder progressNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Creating new video")
                .setContentText("Uploading image...")
                .setOngoing(true)
                .setProgress(100, 0, true)
                .setOnlyAlertOnce(true);
        notificationManager.notify(1, progressNotification.build());

        QUEUE.add(new MultipartUploadRequest(Request.Method.PUT, API.UPLOAD_THUMBNAIL, thumbnailUrl -> {
            progressNotification.setContentText("Uploading video...");
            notificationManager.notify(1, progressNotification.build());

            newVideo.setThumbnail(thumbnailUrl);
            QUEUE.add(new MultipartUploadRequest(Request.Method.PUT, API.UPLOAD_VIDEO, videoUrl -> {
                progressNotification.setContentText("Adding video to course...");
                notificationManager.notify(1, progressNotification.build());

                newVideo.setVideoUrl(videoUrl);
                QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s", API.VIDEO_ADD, courseId), null, response -> {
                    notificationManager.cancel(1);
                    NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("New video added successfully!");
                    notificationManager.notify(2, completeNotification.build());
                }, error -> {
                    notificationManager.cancel(1);
                    NotificationCompat.Builder completeNotification = new NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Could not add new video!")
                            .setContentText(API.parseVolleyError(error));
                    notificationManager.notify(2, completeNotification.build());
                }) {
                    @Override
                    public byte[] getBody() {
                        return new Gson().toJson(newVideo).getBytes(StandardCharsets.UTF_8);
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
                    body.put("video", videoData);
                    return body;
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
                body.put("thumbnail", thumbnailData);
                return body;
            }
        }).setRetryPolicy(new DefaultRetryPolicy());

        thumbnail = null; video = null; quizzes.clear();
        if (binding.quizes.getAdapter() != null) binding.quizes.getAdapter().notifyDataSetChanged();
        binding.selectCourse.setSelection(0);
        binding.videoTitle.setText(null);
        binding.thumbnail.setImageURI(null);
        binding.video.setVideoURI(null);
        binding.video.setVisibility(View.GONE);
        binding.video.setVisibility(View.VISIBLE);
        binding.thumbnailName.setText(null);
        binding.vdoName.setText(null);
        binding.description.setText(null);
    }
}