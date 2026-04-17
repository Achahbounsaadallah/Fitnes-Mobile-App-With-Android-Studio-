package com.example.projectmodule.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.WeeklyTask;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class WeeklyTaskAdapter extends RecyclerView.Adapter<WeeklyTaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onYesClicked(WeeklyTask task);

        void onNoClicked(WeeklyTask task);
    }

    private final List<WeeklyTask> items = new ArrayList<>();
    private final OnTaskActionListener listener;

    public WeeklyTaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void submitTasks(List<WeeklyTask> tasks) {
        items.clear();
        if (tasks != null) {
            items.addAll(tasks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weekly_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView titleView;
        private final TextView statusView;
        private final MaterialButton yesButton;
        private final MaterialButton noButton;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.taskCard);
            titleView = itemView.findViewById(R.id.taskTitle);
            statusView = itemView.findViewById(R.id.taskStatus);
            yesButton = itemView.findViewById(R.id.yesButton);
            noButton = itemView.findViewById(R.id.noButton);
        }

        void bind(WeeklyTask task, OnTaskActionListener listener) {
            titleView.setText(task.getTitle());
            statusView.setText(task.isYes() ? R.string.status_yes : task.isNo() ? R.string.status_no : R.string.status_pending);
            updateButtonState(task);

            yesButton.setOnClickListener(v -> {
                animateClick(v);
                if (listener != null) {
                    listener.onYesClicked(task);
                }
            });

            noButton.setOnClickListener(v -> {
                animateClick(v);
                if (listener != null) {
                    listener.onNoClicked(task);
                }
            });
        }

        private void updateButtonState(WeeklyTask task) {
            yesButton.setChecked(task.isYes());
            noButton.setChecked(task.isNo());

            yesButton.setAlpha(task.isYes() ? 1f : 0.65f);
            noButton.setAlpha(task.isNo() ? 1f : 0.65f);
        }

        private void animateClick(View view) {
            view.animate().scaleX(0.96f).scaleY(0.96f).setDuration(70).withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(70).start()).start();
        }
    }
}

