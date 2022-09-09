package com.debuggers.apnatutor.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.Quiz;
import com.debuggers.apnatutor.databinding.ItemQuizQuestionBinding;
import com.debuggers.apnatutor.databinding.ItemVideoBinding;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final List<Quiz> quizzes;

    public QuizAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuizViewHolder(ItemQuizQuestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.binding.quizQuestion.setText(quiz.getQuestion());
        holder.binding.optionA.setText(quiz.getOptions().get(0));
        holder.binding.optionB.setText(quiz.getOptions().get(1));
        holder.binding.optionC.setText(quiz.getOptions().get(2));
        holder.binding.optionD.setText(quiz.getOptions().get(3));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public interface setOnClickListener {
        void OnClickListener(Course hall, int position);
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        ItemQuizQuestionBinding binding;

        public QuizViewHolder(@NonNull ItemQuizQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
