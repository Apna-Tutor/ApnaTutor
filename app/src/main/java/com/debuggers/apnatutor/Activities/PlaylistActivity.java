package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.Adapters.VideoAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityPlaylistBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PlaylistActivity extends AppCompatActivity {
    ActivityPlaylistBinding binding;
    String courseId;
    List<Video> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.playlistToolbar);
        binding.playlistToolbar.setNavigationOnClickListener(view -> finish());

        courseId = getIntent().getStringExtra("COURSE");
        videos = new ArrayList<>();

        binding.allVideos.setLayoutManager(new LinearLayoutManager(this));
        binding.allVideos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.allVideos.setAdapter(new VideoAdapter(videos, null, (video, position) -> startActivity(new Intent(this, ActivityPlayer.class).putExtra("VIDEO", video.get_id()).putExtra("COURSE", courseId))));

        updateUi();
        binding.playlistRefresher.setOnRefreshListener(this::updateUi);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.playlist_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (binding.allVideos.getAdapter() != null)
                    ((VideoAdapter) binding.allVideos.getAdapter()).getFilter().filter(newText);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUi() {
        binding.playlistRefresher.setRefreshing(true);
        final int[] count = new int[]{0};

        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?course=%s", API.COURSE_BY_ID, courseId), null, courseRes -> {
            Course course = new Gson().fromJson(courseRes.toString(), Course.class);
            if (course.getAuthor().equals(ME.get_id())) {
                Glide.with(this).load(ME.getAvatar()).placeholder(R.drawable.ic_profile).into(binding.authorDp);
                binding.authorName.setText(ME.getName());
                if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
                else count[0]++;
            } else {
                QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, course.getAuthor()), null, userRes -> {
                    User author = new Gson().fromJson(userRes.toString(), User.class);
                    Glide.with(this).load(author.getAvatar()).into(binding.authorDp);
                    binding.authorName.setText(author.getName());
                    if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
                    else count[0]++;
                }, error -> {
                    if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
                    else count[0]++;
                    Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                })).setRetryPolicy(new DefaultRetryPolicy());
            }

            binding.courseName.setText(course.getTitle());
            binding.videosCount.setText(String.format(Locale.getDefault(), "%d videos", course.getVideos().size()));
            binding.followersCount.setText(String.format(Locale.getDefault(), "%d followers", course.getFollowedBy().size()));
            binding.courseDescription.setText(course.getDescription());

            binding.courseDescription.setOnClickListener(view -> {
                if (binding.courseDescription.getMaxLines() == 5) binding.courseDescription.setMaxLines(Integer.MAX_VALUE);
                else binding.courseDescription.setMaxLines(5);
            });

            Log.d("TAG", "updateUi: "+binding.courseDescription.getLineCount());
            Log.d("TAG", "updateUi: "+binding.courseDescription.getMaxLines());

        }, error -> {
            if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
            else count[0]++;
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());

        QUEUE.add(new JsonArrayRequest(Request.Method.GET, String.format("%s?course=%s", API.VIDEOS_ALL, courseId), null, videosRes -> {
            videos.clear();
            videos.addAll(new Gson().fromJson(videosRes.toString(), new TypeToken<List<Video>>() {
            }.getType()));
            Objects.requireNonNull(binding.allVideos.getAdapter()).notifyDataSetChanged();
            if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
            else count[0]++;
        }, error -> {
            if (count[0] == 1) binding.playlistRefresher.setRefreshing(false);
            else count[0]++;
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}