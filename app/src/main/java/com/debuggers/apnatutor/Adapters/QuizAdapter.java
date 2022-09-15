package com.debuggers.apnatutor.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debuggers.apnatutor.Models.Quiz;
import com.debuggers.apnatutor.R;
import com.debuggers.apnatutor.databinding.ItemQuizQuestionBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private final List<Quiz> quizzes;
    private final Map<Quiz, String> selectedAns;
    private final boolean showAns;
    private Context context;

    public QuizAdapter(List<Quiz> quizzes) {
        this.quizzes = quizzes;
        selectedAns = new HashMap<>();
        showAns = false;
    }

    public QuizAdapter(List<Quiz> quizzes, Map<Quiz, String> selectedAns, boolean showAns) {
        this.quizzes = quizzes;
        this.selectedAns = selectedAns;
        this.showAns = showAns;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new QuizViewHolder(ItemQuizQuestionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        RadioButton[] options = new RadioButton[] {holder.binding.optionA, holder.binding.optionB, holder.binding.optionC, holder.binding.optionD};
        holder.binding.quizQuestion.setText(quiz.getQuestion());
        for (int i = 0; i < 4; i++) {
            options[i].setEnabled(!showAns);
            options[i].setText(quiz.getOptions().get(i));
            options[i].setTextColor(context.getColor(R.color.black));
        }

        holder.binding.quizQuestionCounter.setText(String.format(Locale.getDefault(), "%d:", position + 1));
        holder.binding.quizOptions.setOnCheckedChangeListener(null);
        holder.binding.quizOptions.clearCheck();

        if (selectedAns.containsKey(quiz)) {
            int selectedIndex = quiz.getOptions().indexOf(selectedAns.get(quiz));
            options[selectedIndex].setChecked(true);
        }

        if (showAns) {
            options[quiz.getOptions().indexOf(quiz.getAnswer())].setChecked(true);
            options[quiz.getOptions().indexOf(quiz.getAnswer())].setTextColor(context.getColor(R.color.success_color));
            if (selectedAns.containsKey(quiz) && !Objects.equals(selectedAns.get(quiz), quiz.getAnswer())) {
                options[quiz.getOptions().indexOf(selectedAns.get(quiz))].setTextColor(context.getColor(R.color.error_color));
            }
        }

        holder.binding.quizOptions.setOnCheckedChangeListener((radioGroup, checkedId) -> selectedAns.put(quiz, ((RadioButton)radioGroup.findViewById(checkedId)).getText().toString()));
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        ItemQuizQuestionBinding binding;

        public QuizViewHolder(@NonNull ItemQuizQuestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
