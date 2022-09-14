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
import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.User;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ItemCourseBinding;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final List<Course> courses;
    private final List<Course> allCourses;
    private Context context;

    public CourseAdapter(List<Course> courses, setOnClickListener listener) {
        this.courses = courses;
        this.allCourses = new ArrayList<>(courses);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new CourseViewHolder(ItemCourseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        Glide.with(context).load(course.getThumbnail()).into(holder.binding.courseThumbnail);
        holder.binding.courseName.setText(course.getTitle());
        holder.binding.videosCount.setText(String.format(Locale.getDefault(),"%d videos", course.getVideos().size()));
        holder.binding.followersCount.setText(String.format(Locale.getDefault(),"%d followers", course.getFollowedBy().size()));

        holder.getAuthor(course.getAuthor(), (author, error) -> {
            if (error != null) {
                Toast.makeText(context, API.parseVolleyError(error), Toast.LENGTH_SHORT).show();
            } else if (author != null){
                Glide.with(context).load(author.getAvatar()).placeholder(R.drawable.ic_profile).into(holder.binding.authorDp);
                holder.binding.authorName.setText(author.getName());
            }
        });

        holder.itemView.setOnClickListener(v-> listener.OnClickListener(course, position));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Course course, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Course> tmp = new ArrayList<>();
                if (constraint.toString().trim().isEmpty()) {
                    tmp.addAll(allCourses);
                } else {
                    tmp.addAll(allCourses.stream().filter(course -> course.getTitle().toLowerCase(Locale.ROOT).contains(constraint.toString().trim().toLowerCase(Locale.ROOT))).collect(Collectors.toList()));
                }
                FilterResults results = new FilterResults();
                results.values = tmp;
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                courses.clear();
                courses.addAll((Collection<? extends Course>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder  {
        public interface setOnReadyAuthor {
            void OnReadyAuthor(@Nullable User author, @Nullable VolleyError error);
        }

        ItemCourseBinding binding;
        private User author;

        public CourseViewHolder(@NonNull ItemCourseBinding binding) {
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
