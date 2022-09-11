package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.VideoAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityPlayerBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityPlayer extends AppCompatActivity {
    ActivityPlayerBinding binding;
    ExoPlayer player;
    boolean fullscreen;
    Video video;
    Course course;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.playerToolbar);
        binding.playerToolbar.setNavigationOnClickListener(view -> finish());

        fullscreen = false;
        video = Parcels.unwrap(getIntent().getParcelableExtra("VIDEO"));
        course = Parcels.unwrap(getIntent().getParcelableExtra("COURSE"));

        binding.videoTitle.setText(video.getTitle());
        binding.dateOfUpload.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(video.getDate())));
        binding.viewsCount.setText(String.format(Locale.getDefault(), "%d views", video.getViewedBy().size()));
        binding.likeBtn.setImageResource((video.getLikedBy().contains(ME.get_id())) ? R.drawable.ic_like_filled : R.drawable.ic_like_outlined);
        binding.videoDesciption.setText(video.getDescription());
        binding.videosRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.videosRv.setAdapter(new VideoAdapter(course.getVideos(), (video1, position) -> {

        }));

        Log.d("TAG", "onCreate: "+course.getVideos());

        if (course.getAuthor().equals(ME.get_id())) {
            binding.authorName.setText(ME.getName());
        } else {
            QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, course.getAuthor()), null, response -> {
                User author = new Gson().fromJson(response.toString(), User.class);
                binding.authorName.setText(author.getName());
            }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
        }

        player = new ExoPlayer.Builder(this).build();
        binding.video.setPlayer(player);
        player.addMediaItem(MediaItem.fromUri(video.getVideoUrl()));
        player.prepare();

        binding.video.findViewById(R.id.play_pause).setOnClickListener(view -> {
            if (player.isPlaying()) player.pause();
            else if (player.getPlaybackState() == Player.STATE_ENDED) {
                player.seekTo(0);
                player.setPlayWhenReady(true);
            }
            else player.setPlayWhenReady(true);
        });
        binding.video.findViewById(R.id.forward).setOnClickListener(view -> player.seekTo(player.getCurrentPosition() + 10000));
        binding.video.findViewById(R.id.rewind).setOnClickListener(view -> player.seekTo(player.getCurrentPosition() - 10000));
        binding.video.findViewById(R.id.exo_fullscreen).setOnClickListener(view -> {
            if (fullscreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen));
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit));
            }
            fullscreen = !fullscreen;
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                ImageButton btn = binding.video.findViewById(R.id.play_pause);
                ProgressBar progressBar = binding.video.findViewById(R.id.exoProgress);

                if (playbackState == Player.STATE_BUFFERING) {
                    btn.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED) {
                    btn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Log.d("TAG", "onIsPlayingChanged: "+isPlaying);
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                ImageButton btn = binding.video.findViewById(R.id.play_pause);
                if (isPlaying) btn.setImageResource(R.drawable.ic_pause);
                else {
                    if (player.getPlaybackState() == Player.STATE_ENDED) btn.setImageResource(R.drawable.ic_refresh);
                    else btn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        if (!video.getViewedBy().contains(ME.get_id())) {
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s&video=%s", API.ADD_VIDEO_VIEW, course.get_id(), video.get_id()), null, response -> {
                Course updatedCourse = new Gson().fromJson(response.toString(), Course.class);
                video.getViewedBy().add(ME.get_id());
            }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show()) {
                @Override
                public byte[] getBody() {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("user", ME.get_id());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return object.toString().getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
        }

        binding.likeBtn.setOnClickListener(view -> {
            binding.likeBtn.setEnabled(false);
            if (video.getLikedBy().contains(ME.get_id())) {
                QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s&video=%s", API.REMOVE_VIDEO_LIKE, course.get_id(), video.get_id()), null, response -> {
                    Course updatedCourse = new Gson().fromJson(response.toString(), Course.class);
                    video.getLikedBy().remove(ME.get_id());
                    binding.likeBtn.setEnabled(true);
                    binding.likeBtn.setImageResource(R.drawable.ic_like_outlined);
                }, error -> {
                    binding.likeBtn.setEnabled(true);
                    Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                }) {
                    @Override
                    public byte[] getBody() {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("user", ME.get_id());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return object.toString().getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            } else {
                QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?course=%s&video=%s", API.ADD_VIDEO_LIKE, course.get_id(), video.get_id()), null, response -> {
                    Course updatedCourse = new Gson().fromJson(response.toString(), Course.class);
                    video.getLikedBy().add(ME.get_id());
                    binding.likeBtn.setEnabled(true);
                    binding.likeBtn.setImageResource(R.drawable.ic_like_filled);
                }, error -> {
                    Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
                    binding.likeBtn.setEnabled(true);
                }) {
                    @Override
                    public byte[] getBody() {
                        JSONObject object = new JSONObject();
                        try {
                            object.put("user", ME.get_id());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return object.toString().getBytes(StandardCharsets.UTF_8);
                    }
                }).setRetryPolicy(new DefaultRetryPolicy());
            }
        });

        binding.openDescription.setOnClickListener(view -> {
            binding.descriptionArea.setVisibility(View.VISIBLE);
            binding.initArea.setVisibility(View.GONE);
        });
        binding.closeDescription.setOnClickListener(view -> {
            binding.descriptionArea.setVisibility(View.GONE);
            binding.initArea.setVisibility(View.VISIBLE);
        });

        binding.openComments.setOnClickListener(view -> {
            binding.commentsArea.setVisibility(View.VISIBLE);
            binding.initArea.setVisibility(View.GONE);
        });
        binding.closeComment.setOnClickListener(view -> {
            binding.commentsArea.setVisibility(View.GONE);
            binding.initArea.setVisibility(View.VISIBLE);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) player.setPlayWhenReady(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.release();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            goFullscreen();
        } else {
            exitFullScreen();
        }
    }

    private void goFullscreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.video.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        binding.video.setLayoutParams(params);
        binding.restContainer.setVisibility(View.GONE);
    }

    private void exitFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.video.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.video.setLayoutParams(params);
        binding.restContainer.setVisibility(View.VISIBLE);
    }
}