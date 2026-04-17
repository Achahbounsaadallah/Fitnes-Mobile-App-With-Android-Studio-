package com.example.projectmodule.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.WeeklyPlan;
import com.example.projectmodule.data.model.WeeklyTask;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HistoryWeekAdapter extends RecyclerView.Adapter<HistoryWeekAdapter.HistoryViewHolder> {

    private final List<WeeklyPlan> items = new ArrayList<>();

    public void submitHistory(List<WeeklyPlan> plans) {
        items.clear();
        if (plans != null) {
            items.addAll(plans);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView weekTitle;
        private final TextView weekRange;
        private final TextView weekSummary;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.historyCard);
            weekTitle = itemView.findViewById(R.id.historyWeekTitle);
            weekRange = itemView.findViewById(R.id.historyWeekRange);
            weekSummary = itemView.findViewById(R.id.historyWeekSummary);
        }

        void bind(WeeklyPlan plan) {
            weekTitle.setText(itemView.getContext().getString(R.string.history_week_label, plan.getWeekStart()));
            weekRange.setText(itemView.getContext().getString(R.string.history_week_range, plan.getWeekStart(), plan.getWeekEnd()));
            weekSummary.setText(buildSummary(plan.getTasks()));
        }

        private String buildSummary(List<WeeklyTask> tasks) {
            if (tasks == null || tasks.isEmpty()) {
                return itemView.getContext().getString(R.string.no_tasks_yet);
            }

            StringBuilder builder = new StringBuilder();
            for (WeeklyTask task : tasks) {
                String status = task.isYes() ? "YES" : task.isNo() ? "NO" : "PENDING";
                builder.append("• ").append(task.getTitle()).append(" — ").append(status).append('\n');
            }
            return builder.toString().trim();
        }
    }
}

