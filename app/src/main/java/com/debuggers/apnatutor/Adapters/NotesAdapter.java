package com.debuggers.apnatutor.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.debuggers.apnatutor.Models.Course;
import com.debuggers.apnatutor.Models.Note;
import com.debuggers.apnatutor.databinding.ItemVideoBinding;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.VideoViewHolder> {
    private final setOnClickListener listener;
    private final List<Note> notes;

    public NotesAdapter(List<Note> notes, setOnClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoViewHolder(ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Note quiz = notes.get(position);

    }

    @Override
    public int getItemCount() {
        return notes.size();
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
