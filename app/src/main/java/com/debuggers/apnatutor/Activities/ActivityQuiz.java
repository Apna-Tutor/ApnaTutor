package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.QUEUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.QuizAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Comment;
import com.debuggers.apnatutor.Models.Quiz;
import com.debuggers.apnatutor.Models.Rank;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityQuizBinding;
import com.debuggers.apnatutor.databinding.ProgressDialogBinding;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ActivityQuiz extends AppCompatActivity {
    ActivityQuizBinding binding;
    Map<Quiz, String> selected;
    Video video;
    boolean submitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.quizToolbar);
        video = Parcels.unwrap(getIntent().getParcelableExtra("VIDEO"));
        submitted = false;

        Log.d("TAG", "onCreate: "+video.getQuiz());

        binding.quizToolbar.setNavigationOnClickListener(view -> finish());
        binding.quizToolbar.setTitle(video.getTitle());

        selected = new HashMap<>();

        binding.quizRv.setLayoutManager(new LinearLayoutManager(this));
        binding.quizRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.quizRv.setAdapter(new QuizAdapter(video.getQuiz(), selected, submitted));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu, menu);
        menu.findItem(R.id.submitQuiz).setVisible(!submitted);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.submitQuiz) submitQuiz();
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void submitQuiz() {
        ProgressDialogBinding dialogBinding = ProgressDialogBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);

        dialogBinding.progressDialogTitle.setText("Getting result");
        dialogBinding.progressDialogText.setText("Please wait while fetch your result...");

        double score = 0;
        for (Quiz quiz : video.getQuiz()) {
            if (selected.containsKey(quiz) && quiz.getAnswer().equals(selected.get(quiz))) score++;
        }

        Rank rank = new Rank(score/video.getQuiz().size() * 100);
        QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?video=%s", API.VIDEO_ADD_RANK, video.get_id()), null, response -> {
            dialogBinding.progressDialogProgressBar.setVisibility(View.GONE);
            dialogBinding.progressDialogTitle.setText("Quiz submitted!");
            dialogBinding.progressDialogText.setText(String.format(Locale.getDefault(), "Your accuracy was %f%%", rank.getPercentage()));
            dialogBinding.progressDialogButtonsArea.setVisibility(View.VISIBLE);
            dialogBinding.okBtn.setVisibility(View.VISIBLE);
            dialogBinding.okBtn.setOnClickListener(view -> dialog.dismiss());
            setResult(RESULT_OK, new Intent().putExtra("RANK", Parcels.wrap(rank)));
        }, error -> {
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }) {
            @Override
            public byte[] getBody() {
                return new Gson().toJson(rank).getBytes(StandardCharsets.UTF_8);
            }
        }).setRetryPolicy(new DefaultRetryPolicy());

        submitted = true;
        invalidateMenu();
        binding.quizRv.setAdapter(new QuizAdapter(video.getQuiz(), selected, submitted));

        dialog.show();
    }
}