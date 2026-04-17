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
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

	public interface OnExerciseClickListener {
		void onExerciseClick(Exercise exercise);
	}

	private final List<Exercise> exercises = new ArrayList<>();
	private final OnExerciseClickListener listener;

	public ExerciseAdapter(OnExerciseClickListener listener) {
		this.listener = listener;
	}

	public void submitExercises(List<Exercise> newExercises) {
		exercises.clear();
		if (newExercises != null) {
			exercises.addAll(newExercises);
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
		holder.bind(exercises.get(position), listener);
	}

	@Override
	public int getItemCount() {
		return exercises.size();
	}

	static class ExerciseViewHolder extends RecyclerView.ViewHolder {

		private final MaterialCardView cardView;
		private final ImageView exerciseImage;
		private final TextView exerciseName;
		private final TextView exerciseDescription;

		ExerciseViewHolder(@NonNull View itemView) {
			super(itemView);
			cardView = itemView.findViewById(R.id.exerciseCard);
			exerciseImage = itemView.findViewById(R.id.exerciseImage);
			exerciseName = itemView.findViewById(R.id.exerciseName);
			exerciseDescription = itemView.findViewById(R.id.exerciseDescription);
		}

		void bind(Exercise exercise, OnExerciseClickListener listener) {
			exerciseName.setText(exercise.getName());
			exerciseDescription.setText(exercise.getShortDescription());
			exerciseImage.setImageResource(exercise.getImageResId());
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
