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
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

	public interface OnExerciseClickListener {
		void onExerciseClick(Exercise exercise);
	}

	public interface OnFavoriteClickListener {
		void onFavoriteClick(Exercise exercise);
	}

	private final List<Exercise> exercises = new ArrayList<>();
	private final OnExerciseClickListener listener;
	private final OnFavoriteClickListener favoriteClickListener;
	private final Set<String> favoriteIds = new HashSet<>();

	public ExerciseAdapter(OnExerciseClickListener listener, OnFavoriteClickListener favoriteClickListener) {
		this.listener = listener;
		this.favoriteClickListener = favoriteClickListener;
	}

	public void submitExercises(List<Exercise> newExercises) {
		exercises.clear();
		if (newExercises != null) {
			exercises.addAll(newExercises);
		}
		notifyDataSetChanged();
	}

	public void submitFavoriteIds(Set<String> newFavoriteIds) {
		favoriteIds.clear();
		if (newFavoriteIds != null) {
			favoriteIds.addAll(newFavoriteIds);
		}
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
		return new ExerciseViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
		holder.bind(exercises.get(position), favoriteIds, listener, favoriteClickListener);
	}

	@Override
	public int getItemCount() {
		return exercises.size();
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

		void bind(Exercise exercise, Set<String> favoriteIds, OnExerciseClickListener listener, OnFavoriteClickListener favoriteClickListener) {
			exerciseName.setText(exercise.getName());
			exerciseDescription.setText(exercise.getShortDescription());
			exerciseImage.setImageResource(exercise.getImageResId());
			boolean isFavorite = favoriteIds != null && favoriteIds.contains(exercise.getId());
			favoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
			favoriteButton.setOnClickListener(v -> {
				animateClick(v);
				if (favoriteClickListener != null) {
					favoriteClickListener.onFavoriteClick(exercise);
				}
			});
			cardView.setOnClickListener(v -> {
				animateClick(v);
				if (listener != null) {
					listener.onExerciseClick(exercise);
				}
			});
		}

		private void animateClick(View view) {
			view.animate()
					.scaleX(0.98f)
					.scaleY(0.98f)
					.setDuration(70)
					.withEndAction(() -> view.animate()
							.scaleX(1f)
							.scaleY(1f)
							.setDuration(70)
							.start())
					.start();
		}
	}
}
