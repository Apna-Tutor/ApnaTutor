package com.debuggers.apnatutor.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.debuggers.apnatutor.Models.Video;
import com.debuggers.apnatutor.databinding.ItemVideoBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final List<Video> videos;
    private final List<Video> allVideos;
    private Context context;


    public VideoAdapter(List<Video> videos, setOnClickListener listener) {
        this.videos = videos;
        this.allVideos = new ArrayList<>(videos);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new VideoViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.binding.videoName.setText(video.getTitle());
        Glide.with(context).load(video.getThumbnail()).into(holder.binding.videoThumbnail);
        holder.binding.uploadDate.setText(new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date(video.getDate())));
        holder.binding.viewersCount.setText(String.format(Locale.getDefault(), "%d views", video.getViewedBy().size()));

        holder.itemView.setOnClickListener(view -> listener.OnClickListener(video, position));
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Video video, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Video> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allVideos);
                } else {
                    tmp.addAll(allVideos.stream().filter(course -> course.getTitle().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                videos.clear();
                videos.addAll((Collection<? extends Video>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public VideoViewHolder(@NonNull ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
