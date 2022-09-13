package com.debuggers.apnatutor.Adapters;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Comment;
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ItemCommentBinding;
import com.debuggers.apnatutor.databinding.ItemCourseBinding;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final setOnClickListener listener;
    private final List<Comment> comments;
    private Context context;

    public CommentAdapter(List<Comment> comments, setOnClickListener listener) {
        this.comments = comments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.binding.commentDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(comment.getDate())));
        holder.binding.commentText.setText(comment.getComment());
        holder.getAuthor(comment.getUserId(), (author, error) -> {
            if (error != null) {
                Toast.makeText(context, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            } else if (author != null){
                Glide.with(context).load(author.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.userPicture);
                holder.binding.commentUser.setText(author.getName());
            }
        });

        holder.itemView.setOnClickListener(v-> listener.OnClickListener(comment, position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Comment comment, int position);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder  {
        public interface setOnReadyAuthor {
            void OnReadyAuthor(@Nullable User author, @Nullable VolleyError error);
        }

        ItemCommentBinding binding;
        private User user;

        public CommentViewHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void getAuthor(String id, setOnReadyAuthor listener) {
            if (Objects.equals(id, ME.get_id())) {
                user = ME;
                listener.OnReadyAuthor(user, null);
            } else {
                if (user != null) {
                    listener.OnReadyAuthor(user, null);
                } else {
                    QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, id), null, response -> {
                        user = new Gson().fromJson(response.toString(), User.class);
                        listener.OnReadyAuthor(user, null);
                    }, error -> {
                        listener.OnReadyAuthor(null, error);
                    })).setRetryPolicy(new DefaultRetryPolicy());
                }
            }
        }
    }
}
