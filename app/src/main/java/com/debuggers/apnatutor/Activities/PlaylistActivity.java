package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.databinding.ActivityPlaylistBinding;
import com.debuggers.apnatutor.Adapters.VideoAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.google.gson.Gson;

public class PlaylistActivity extends AppCompatActivity {
    ActivityPlaylistBinding binding;
    Course course = new Course();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updateUi();
        binding.playlistRefresher.setOnRefreshListener(this::updateUi);
    }

    private void updateUi() {
        binding.playlistRefresher.setRefreshing(true);
        Glide.with(this).load(course.getThumbnail()).into(binding.courseThumbnail);
        binding.courseName.setText(course.getTitle());
        binding.videosCount.setText(String.valueOf(course.getVideos().size()));
        binding.followersCount.setText(String.valueOf(course.getFollowedBy().size()));

        binding.allVideos.setLayoutManager(new LinearLayoutManager(this));
        binding.allVideos.setAdapter(new VideoAdapter(course.getVideos(), (video, position) -> {

        }));

        if (course.getAuthor().equals(ME.get_id())) {
            Glide.with(this).load(ME.getAvatar()).into(binding.authorDp);
            binding.authorName.setText(ME.getName());
            binding.playlistRefresher.setRefreshing(false);
        } else {
            QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, course.getAuthor()), null, response -> {
                User author = new Gson().fromJson(response.toString(), User.class);
                Glide.with(this).load(author.getAvatar()).into(binding.authorDp);
                binding.authorName.setText(author.getName());
                binding.playlistRefresher.setRefreshing(false);
            }, error -> {
                binding.playlistRefresher.setRefreshing(false);
                Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            })).setRetryPolicy(new DefaultRetryPolicy());
        }
    }
}