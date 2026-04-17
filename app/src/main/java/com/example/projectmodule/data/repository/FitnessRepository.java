package com.example.projectmodule.data.repository;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.Category;
import com.example.projectmodule.data.model.Exercise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FitnessRepository {

	public static final String CATEGORY_CHEST = "chest";
	public static final String CATEGORY_BICEPS_TRICEPS = "arms";
	public static final String CATEGORY_BACK = "back";
	public static final String CATEGORY_LEGS = "legs";
	public static final String CATEGORY_SHOULDERS = "shoulders";

	private final List<Category> categories;
	private final List<Exercise> exercises;

	public FitnessRepository() {
		categories = createCategories();
		exercises = createExercises();
	}

	public List<Category> getCategories() {
		return new ArrayList<>(categories);
	}

	public List<Exercise> getExercisesForCategory(String categoryId) {
		if (categoryId == null || categoryId.trim().isEmpty()) {
			return getAllExercises();
		}

		List<Exercise> filteredExercises = new ArrayList<>();
		for (Exercise exercise : exercises) {
			if (categoryId.equals(exercise.getCategoryId())) {
				filteredExercises.add(exercise);
			}
		}
		return filteredExercises;
	}

	public List<Exercise> getAllExercises() {
		return new ArrayList<>(exercises);
	}

	public Exercise getExerciseById(String exerciseId) {
		if (exerciseId == null || exerciseId.trim().isEmpty()) {
			return null;
		}

		for (Exercise exercise : exercises) {
			if (exerciseId.equals(exercise.getId())) {
				return exercise;
			}
		}
		return null;
	}

	private List<Category> createCategories() {
		List<Category> categoryList = new ArrayList<>();
		categoryList.add(new Category(CATEGORY_CHEST, "Chest", R.drawable.ic_menu_crop));
		categoryList.add(new Category(CATEGORY_BICEPS_TRICEPS, "Biceps & Triceps", R.drawable.ic_menu_manage));
		categoryList.add(new Category(CATEGORY_BACK, "Back", R.drawable.ic_menu_directions));
		categoryList.add(new Category(CATEGORY_LEGS, "Legs", R.drawable.ic_menu_compass));
		categoryList.add(new Category(CATEGORY_SHOULDERS, "Shoulders", R.drawable.ic_menu_shoulders));
		return Collections.unmodifiableList(categoryList);
	}

	private List<Exercise> createExercises() {
		List<Exercise> exerciseList = new ArrayList<>();

		// Chest
		exerciseList.add(new Exercise(
				"chest_pushups",
				CATEGORY_CHEST,
				"Push-Ups",
				"Bodyweight move to train chest and triceps.",
				"Start in a high plank. Keep your body straight, lower your chest toward the floor, then push back up. Focus on controlled reps and avoid collapsing your lower back.",
				R.drawable.push_ups
		));
		exerciseList.add(new Exercise(
				"chest_bench_press",
				CATEGORY_CHEST,
				"Dumbbell Bench Press",
				"Strength exercise for chest mass.",
				"Lie on a flat bench with one dumbbell in each hand. Press the weights upward over your chest, then lower slowly until elbows are around 90 degrees. Keep your shoulder blades squeezed on the bench.",
				R.drawable.chest_bench_press
		));

		// Biceps & Triceps
		exerciseList.add(new Exercise(
				"arms_biceps_curls",
				CATEGORY_BICEPS_TRICEPS,
				"Biceps Curls",
				"Classic isolation exercise for biceps.",
				"Stand tall with dumbbells at your sides. Curl the weights up without swinging your torso. Lower with control to fully extend your arms between reps.",
				R.drawable.arms_biceps_curls
		));
		exerciseList.add(new Exercise(
				"arms_triceps_dips",
				CATEGORY_BICEPS_TRICEPS,
				"Triceps Dips",
				"Bodyweight movement for triceps.",
				"Use parallel bars or a stable bench. Lower your body by bending the elbows, then press back up. Keep elbows tracking backward and avoid shoulder shrugging.",
				R.drawable.arms_triceps_dips
		));

		// Back
		exerciseList.add(new Exercise(
				"back_pullups",
				CATEGORY_BACK,
				"Pull-Ups",
				"Compound lift to build lats and upper back.",
				"Hang from a bar with an overhand grip. Pull your chest toward the bar by driving elbows down. Lower under control. Use assistance if needed to keep strict form.",
				R.drawable.back_pullups
		));
		exerciseList.add(new Exercise(
				"back_rows",
				CATEGORY_BACK,
				"Bent-Over Rows",
				"Back thickness exercise with free weights.",
				"Hinge at the hips with a neutral spine and slight knee bend. Pull the weight toward your lower ribs, pause briefly, and lower slowly while keeping your torso stable.",
				R.drawable.back_rows
		));

		// Legs
		exerciseList.add(new Exercise(
				"legs_squats",
				CATEGORY_LEGS,
				"Bodyweight Squats",
				"Lower-body movement for quads and glutes.",
				"Stand with feet shoulder-width apart. Sit back and down until thighs are near parallel, then drive through your heels to stand. Keep knees tracking over toes.",
				R.drawable.bodyweight_squats
		));
		exerciseList.add(new Exercise(
				"legs_lunges",
				CATEGORY_LEGS,
				"Lunges",
				"Single-leg exercise for balance and strength.",
				"Step forward into a split stance and lower until both knees are bent around 90 degrees. Push through the front foot to return. Alternate sides for balanced training.",
				R.drawable.legs_lunges
		));

		// Shoulders
		exerciseList.add(new Exercise(
				"shoulders_press",
				CATEGORY_SHOULDERS,
				"Shoulder Press",
				"Overhead movement for deltoid strength.",
				"Sit or stand with dumbbells at shoulder height. Press straight up until arms are extended, then lower slowly to the start position. Keep your core tight and avoid leaning back.",
				R.drawable.shoulder_press
		));
		exerciseList.add(new Exercise(
				"shoulders_lateral_raise",
				CATEGORY_SHOULDERS,
				"Lateral Raises",
				"Isolation move for side delts.",
				"With a slight bend in elbows, raise dumbbells to shoulder height and lower with control. Use light weights and smooth tempo to avoid swinging.",
				R.drawable.shoulders_lateral_raise
		));

		return Collections.unmodifiableList(exerciseList);
	}
}
