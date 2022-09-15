package com.debuggers.apnatutor.Adapters;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.Helpers.API;
import com.debuggers.apnatutor.Models.Comment;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ItemCommentBinding;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final setOnEventListeners listener;
    private final List<Comment> comments;
    private final List<User> users;
    private Context context;

    public CommentAdapter(List<Comment> comments, List<User> users, setOnEventListeners listener) {
        this.comments = comments;
        this.users = users;
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

        holder.binding.commentUser.setText(null);
        holder.binding.userPicture.setImageDrawable(null);

        if (users.get(position) != null) {
            User user = users.get(position);
            holder.binding.commentUser.setText(user.getName());
            Glide.with(context).load(user.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.userPicture);
        } else {
            if (Objects.equals(comment.getUserId(), ME.get_id())) {
                holder.binding.commentUser.setText(ME.getName());
                Glide.with(context).load(ME.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.userPicture);
                users.set(position, ME);
            } else {
                QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, comment.getUserId()), null, response -> {
                    User user = new Gson().fromJson(response.toString(), User.class);
                    holder.binding.commentUser.setText(user.getName());
                    Glide.with(context).load(user.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.userPicture);
                    users.set(position, user);
                }, error -> Toast.makeText(context, API.parseVolleyError(error), Toast.LENGTH_SHORT).show())).setRetryPolicy(new DefaultRetryPolicy());
            }
        }

        if (comment.getLikedBy().contains(ME.get_id())) holder.binding.commentLikeBtn.setTextColor(context.getColor(R.color.primary_color));
        else holder.binding.commentLikeBtn.setTextColor(context.getColor(android.R.color.darker_gray));

        holder.binding.likeCount.setText(String.valueOf(comment.getLikedBy().size()));
        holder.binding.replyCount.setText(String.valueOf(comment.getReplies().size()));

        if (comment.getUserId().equals(ME.get_id())) holder.binding.commentOptions.setVisibility(View.VISIBLE);
        else holder.binding.commentOptions.setVisibility(View.GONE);

        holder.binding.commentOptions.setOnClickListener(view -> {
            if (comment.getUserId().equals(ME.get_id())) listener.OnDeleteListener(comment, position);
        });

        holder.binding.commentLikeBtn.setOnClickListener(view -> listener.OnLikeListener(comment, !comment.getLikedBy().contains(ME.get_id()), position));

        holder.binding.commentReplyBtn.setOnClickListener(view -> listener.OnReplyListener(comment, position));

        holder.itemView.setOnClickListener(v-> listener.OnClickListener(comment, position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public interface setOnEventListeners {
        void OnClickListener(Comment comment, int position);
        void OnDeleteListener(Comment comment, int position);
        void OnLikeListener(Comment comment, boolean like, int position);
        void OnReplyListener(Comment comment, int position);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder  {
        ItemCommentBinding binding;

        public CommentViewHolder(@NonNull ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
