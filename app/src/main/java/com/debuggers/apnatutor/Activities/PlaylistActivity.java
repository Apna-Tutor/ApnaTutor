package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityPlaylistBinding;
import com.debuggers.apnatutor.Adapters.VideoAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.Locale;

public class PlaylistActivity extends AppCompatActivity {
    ActivityPlaylistBinding binding;
    Course course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.playlistToolbar);
        binding.playlistToolbar.setNavigationOnClickListener(view -> finish());

        course = Parcels.unwrap(getIntent().getParcelableExtra("COURSE"));

        binding.allVideos.setLayoutManager(new LinearLayoutManager(this));
        binding.allVideos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        updateUi();
        binding.playlistRefresher.setOnRefreshListener(this::updateUi);
    }

    private void updateUi() {
        binding.playlistRefresher.setRefreshing(true);
        Glide.with(this).load(course.getThumbnail()).into(binding.courseThumbnail);
        binding.courseName.setText(course.getTitle());
        binding.videosCount.setText(String.format(Locale.getDefault(),"%d videos", course.getVideos().size()));
        binding.followersCount.setText(String.format(Locale.getDefault(),"%d followers", course.getFollowedBy().size()));

        binding.allVideos.setAdapter(new VideoAdapter(course.getVideos(), (video, position) -> {

        }));

        if (course.getAuthor().equals(ME.get_id())) {
            Glide.with(this).load(ME.getAvatar()).placeholder(R.drawable.ic_profile).into(binding.authorDp);
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