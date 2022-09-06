package com.debuggers.apna_tutor.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debuggers.apna_tutor.Models.Course;
import com.debuggers.apna_tutor.Models.Quiz;
import com.debuggers.apna_tutor.Models.Video;
import com.debuggers.apna_tutor.databinding.ItemVideoBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.VideoViewHolder> {
    private final setOnClickListener listener;
    private final List<Quiz> quizzes;

    public QuizAdapter(List<Quiz> quizzes, setOnClickListener listener) {
        this.quizzes = quizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);

    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Course hall, int position);
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ItemVideoBinding binding;

        public VideoViewHolder(@NonNull ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
