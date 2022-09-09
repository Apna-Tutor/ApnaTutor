package com.debuggers.apnatutor.Fragments;

import static com.debuggers.apnatutor.App.QUEUE;

import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.QuizAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.Quiz;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.FragmentUploadBinding;
import com.debuggers.apnatutor.databinding.FragmentVideoUploadBinding;
import com.debuggers.apnatutor.databinding.QuizQuestionDialogBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoUploadFragment extends Fragment {
    FragmentVideoUploadBinding binding;
    List<Quiz> quizzes;

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if (result != null) {
            Cursor cursor = requireContext().getContentResolver().query(result, null, null, null, null);
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            String memeType = requireContext().getContentResolver().getType(result);
            if (Pattern.matches("image/\\w*",memeType)) {
                binding.thumbnail.setImageURI(result);
                binding.thumbnailName.setText(cursor.getString(nameIndex));
            } else if (Pattern.matches("video/\\w*",memeType)){
                binding.video.setVideoURI(result);
                binding.vdoName.setText(cursor.getString(nameIndex));
                binding.video.setOnPreparedListener(mediaPlayer -> {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                });
            }
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoUploadBinding.inflate(inflater, container, false);
        quizzes = new ArrayList<>();
        fetchCourses();

        binding.thumbnail.setOnClickListener(view -> {
            launcher.launch("image/*");
        });

        binding.video.setOnClickListener(view -> {
            launcher.launch("video/*");
        });

        binding.quizes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.quizes.setAdapter(new QuizAdapter(quizzes));
        binding.addQuiz.setOnClickListener(view -> showQuizDialog());

        return binding.getRoot();
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

        dialogBinding.cancel.setOnClickListener(view -> dialog.dismiss());

        dialogBinding.submit.setOnClickListener(view -> {
            if (dialogBinding.question.getText().toString().trim().isEmpty()) {
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
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, API.COURSES_UPLOADED, null, response -> {
            List<Course> courses = new Gson().fromJson(response.toString(), new TypeToken<List<Course>>(){}.getType());
            List<String> courseNames = courses.stream().map(Course::getTitle).collect(Collectors.toList());
            courseNames.add(0, "Select course");
            binding.selectCourse.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_activated_1, courseNames));
            binding.fetchCourseProgress.setVisibility(View.GONE);
        }, error -> {
            Toast.makeText(requireContext(), API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            binding.fetchCourseProgress.setVisibility(View.GONE);
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}