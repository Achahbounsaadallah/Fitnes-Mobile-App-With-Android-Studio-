package com.example.projectmodule.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.model.FavoriteSection;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_EXERCISE = 1;

    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(Exercise exercise);
    }

    private static class RowItem {
        private final String header;
        private final Exercise exercise;

        RowItem(String header) {
            this.header = header;
            this.exercise = null;
        }

        RowItem(Exercise exercise) {
            this.header = null;
            this.exercise = exercise;
        }

        boolean isHeader() {
            return header != null;
        }
    }

    private final List<RowItem> rows = new ArrayList<>();
    private final OnExerciseClickListener clickListener;
    private final OnFavoriteToggleListener favoriteToggleListener;

    public FavoritesAdapter(OnExerciseClickListener clickListener, OnFavoriteToggleListener favoriteToggleListener) {
        this.clickListener = clickListener;
        this.favoriteToggleListener = favoriteToggleListener;
    }

    public void submitSections(List<FavoriteSection> sections) {
        rows.clear();
        if (sections != null) {
            for (FavoriteSection section : sections) {
                rows.add(new RowItem(section.getCategoryTitle()));
                for (Exercise exercise : section.getExercises()) {
                    rows.add(new RowItem(exercise));
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_EXERCISE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_favorite_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowItem row = rows.get(position);
        if (row.isHeader()) {
            ((HeaderViewHolder) holder).bind(row.header);
        } else {
            ((ExerciseViewHolder) holder).bind(row.exercise, clickListener, favoriteToggleListener);
        }
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.headerTitle);
        }

        void bind(String categoryTitle) {
            title.setText(categoryTitle);
        }
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final ImageView exerciseImage;
        private final ImageView favoriteButton;
        private final TextView exerciseName;
        private final TextView exerciseDescription;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.exerciseCard);
            exerciseImage = itemView.findViewById(R.id.exerciseImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            exerciseName = itemView.findViewById(R.id.exerciseName);
            exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
        }

        void bind(Exercise exercise, OnExerciseClickListener clickListener, OnFavoriteToggleListener favoriteToggleListener) {
            exerciseName.setText(exercise.getName());
            exerciseDescription.setText(exercise.getShortDescription());
            exerciseImage.setImageResource(exercise.getImageResId());
            favoriteButton.setImageResource(R.drawable.ic_star_filled);

            cardView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onExerciseClick(exercise);
                }
            });

            favoriteButton.setOnClickListener(v -> {
                if (favoriteToggleListener != null) {
                    favoriteToggleListener.onFavoriteToggle(exercise);
                }
            });
        }
    }
}

