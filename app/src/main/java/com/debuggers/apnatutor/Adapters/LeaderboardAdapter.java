package com.debuggers.apnatutor.Adapters;

import static com.debuggers.apnatutor.App.ME;
import static com.debuggers.apnatutor.App.QUEUE;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
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
import com.debuggers.apnatutor.Models.Rank;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ItemLeaderboardBinding;
import com.google.gson.Gson;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {
    private final setOnClickListener listener;
    private final List<Rank> ranks;
    private Context context;

    public LeaderboardAdapter(List<Rank> ranks, setOnClickListener listener) {
        this.ranks = ranks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new LeaderboardViewHolder(ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        Rank rank = ranks.get(position);
        holder.getAuthor(rank.getUserId(), (author, error) -> {
            if (error != null) {
                Toast.makeText(context, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            } else if (author != null){
                Glide.with(context).load(author.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.studentDp);
                holder.binding.studentName.setText(author.getName());
                holder.binding.studentScore.setText(String.format(Locale.getDefault(),"%f%%", rank.getPercentage()));
                holder.itemView.setOnClickListener(v-> listener.OnClickListener(author, position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return ranks.size();
    }

    public interface setOnClickListener {
        void OnClickListener(User user, int position);
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder  {
        public interface setOnReadyAuthor {
            void OnReadyAuthor(@Nullable User author, @Nullable VolleyError error);
        }

        ItemLeaderboardBinding binding;
        private User author;

        public LeaderboardViewHolder(@NonNull ItemLeaderboardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void getAuthor(String id, setOnReadyAuthor listener) {
            if (author != null) {
                listener.OnReadyAuthor(author, null);
            } else {
                if (Objects.equals(id, ME.get_id())) {
                    author = ME;
                    listener.OnReadyAuthor(author, null);
                } else {
                    QUEUE.add(new JsonObjectRequest(Request.Method.GET, String.format("%s?user=%s", API.USER_BY_ID, id), null, response -> {
                        author = new Gson().fromJson(response.toString(), User.class);
                        listener.OnReadyAuthor(author, null);
                    }, error -> {
                        listener.OnReadyAuthor(null, error);
                    })).setRetryPolicy(new DefaultRetryPolicy());
                }
            }
        }
    }
}
