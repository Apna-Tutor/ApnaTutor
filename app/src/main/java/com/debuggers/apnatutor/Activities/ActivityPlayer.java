package com.debuggers.apnatutor.Activities;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.debuggers.apnatutor.Adapters.CommentAdapter;
import com.debuggers.apnatutor.Adapters.VideoAdapter;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Comment;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ActivityPlayerBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityPlayer extends AppCompatActivity {
    ActivityPlayerBinding binding;
    ExoPlayer player;
    boolean fullscreen;
    String videoId, courseId;
    Video video;
    Course course;
    List<Comment> comments;
    List<Video> videos;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.playerToolbar);
        binding.playerToolbar.setNavigationOnClickListener(view -> finish());

        player = new ExoPlayer.Builder(this).build();
        comments = new ArrayList<>();
        videos = new ArrayList<>();
        binding.video.setPlayer(player);

        binding.videosRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.videosRv.setAdapter(new VideoAdapter(videos, videoId, (video, position) -> {
            if (!video.get_id().equals(videoId)) startActivity(new Intent(this, ActivityPlayer.class).putExtra("VIDEO", video.get_id()).putExtra("COURSE", courseId));
        }));

        binding.comments.setLayoutManager(new LinearLayoutManager(this));
        binding.comments.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.comments.setAdapter(new CommentAdapter(comments, (comment, position) -> {

        }));

        fullscreen = false;
        videoId = getIntent().getStringExtra("VIDEO");
        courseId = getIntent().getStringExtra("COURSE");

        fetchVideo();
        fetchCourse();
        fetchVideos();
        fetchComments();
        binding.commentsRefresher.setOnRefreshListener(this::fetchComments);

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

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.video.getLayoutParams();
                    params.height = (fullscreen) ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.video.setLayoutParams(params);
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                ImageButton btn = binding.video.findViewById(R.id.play_pause);
                if (isPlaying) btn.setImageResource(R.drawable.ic_pause);
                else {
                    if (player.getPlaybackState() == Player.STATE_ENDED) btn.setImageResource(R.drawable.ic_refresh);
                    else btn.setImageResource(R.drawable.ic_play);
                }
            }
        });

        binding.likeBtn.setOnClickListener(view -> {
            if (video != null) {
                binding.likeBtn.setEnabled(false);
                if (video.getLikedBy().contains(ME.get_id())) {
                    QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?video=%s", API.VIDEO_REMOVE_LIKE, videoId), null, response -> {
                        video = new Gson().fromJson(response.toString(), Video.class);
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
                    QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?video=%s", API.VIDEO_ADD_LIKE, videoId), null, response -> {
                        video = new Gson().fromJson(response.toString(), Video.class);
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
            } else {
                Toast.makeText(this, "Please wait till the video loads!", Toast.LENGTH_SHORT).show();
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

        binding.postComment.setOnClickListener(view -> {
            if (binding.comment.getText().toString().trim().isEmpty()) {
                binding.comment.setError("Can not post an empty comment!");
                return;
            }
            Comment newComment = new Comment(binding.comment.getText().toString().trim());
            QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?video=%s", API.COMMENT_ADD, videoId), null, response -> {
                comments.add(0, new Gson().fromJson(response.toString(), Comment.class));
                Objects.requireNonNull(binding.comments.getAdapter()).notifyItemInserted(0);
            }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show()) {
                @Override
                public byte[] getBody() {
                    return new Gson().toJson(newComment).getBytes(StandardCharsets.UTF_8);
                }
            }).setRetryPolicy(new DefaultRetryPolicy());
            binding.comment.setText("");
        });

        binding.openNotes.setOnClickListener(view -> startActivity(new Intent(this, ActivityNotes.class)));
        binding.openQuiz.setOnClickListener(view -> startActivity(new Intent(this, ActivityQuiz.class)));
        binding.openLeaderboard.setOnClickListener(view -> startActivity(new Intent(this, ActivityLeaderboard.class)));
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
        fullscreen = true;
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
        fullscreen = false;
    }

    private void fetchVideo() {
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?video=%s", API.VIDEO_BY_ID, videoId), null, courseRes -> {
            video = new Gson().fromJson(courseRes.toString(), Video.class);
            player.addMediaItem(MediaItem.fromUri(video.getVideoUrl()));
            player.prepare();

            binding.videoTitle.setText(video.getTitle());
            binding.dateOfUpload.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(video.getDate())));
            binding.viewsCount.setText(String.format(Locale.getDefault(), "%d views", video.getViewedBy().size()));
            binding.likeBtn.setImageResource((video.getLikedBy().contains(ME.get_id())) ? R.drawable.ic_like_filled : R.drawable.ic_like_outlined);
            binding.videoDesciption.setText(video.getDescription());

            if (!video.getViewedBy().contains(ME.get_id())) {
                addMyView();
            }
        }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
    }

    private void addMyView() {
        QUEUE.add(new JsonObjectRequest(Request.Method.POST, String.format("%s?video=%s", API.VIDEO_ADD_VIEW, videoId), null,
                response -> video = new Gson().fromJson(response.toString(), Video.class),
                error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show()) {
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

    private void fetchCourse() {
        QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?course=%s", API.COURSE_BY_ID, courseId), null, courseRes -> {
            course = new Gson().fromJson(courseRes.toString(), Course.class);
            if (course.getAuthor().equals(ME.get_id())) {
                binding.authorName.setText(ME.getName());
            } else {
                QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, course.getAuthor()), null, response -> {
                    User author = new Gson().fromJson(response.toString(), User.class);
                    binding.authorName.setText(author.getName());
                }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
            }
        }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchVideos() {
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, String.format("%s?course=%s", API.VIDEOS_ALL, courseId), null, videosRes -> {
            videos.clear();
            videos.addAll(new Gson().fromJson(videosRes.toString(), new TypeToken<List<Video>>(){}.getType()));
            Objects.requireNonNull(binding.comments.getAdapter()).notifyDataSetChanged();
        }, error -> Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchComments() {
        binding.commentsRefresher.setRefreshing(true);
        QUEUE.add(new JsonArrayRequest(Request.Method.GET, String.format("%s?video=%s", API.COMMENTS_ALL, videoId), null, commentsRes -> {
            comments.clear();
            comments.addAll(new Gson().fromJson(commentsRes.toString(), new TypeToken<List<Comment>>(){}.getType()));
            Objects.requireNonNull(binding.comments.getAdapter()).notifyDataSetChanged();
            binding.commentsRefresher.setRefreshing(false);
        }, error -> {
            binding.commentsRefresher.setRefreshing(false);
            Toast.makeText(this, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
        })).setRetryPolicy(new DefaultRetryPolicy());
    }
}