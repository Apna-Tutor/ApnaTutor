package com.debuggers.apna_tutor.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debuggers.apna_tutor.Models.Course;
import com.debuggers.apna_tutor.databinding.ItemCourseBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> implements Filterable {
    private final setOnClickListener listener;
    private final List<Course> courses;
    private final List<Course> allCourses;

    public CourseAdapter(List<Course> courses, setOnClickListener listener) {
        this.courses = courses;
        this.allCourses = new ArrayList<>(courses);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CourseViewHolder(ItemCourseBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);

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

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ItemCourseBinding binding;

        public CourseViewHolder(@NonNull ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
