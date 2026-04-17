package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.viewmodel.ExerciseViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class ExerciseActivity extends AppCompatActivity {

	public static final String EXTRA_EXERCISE_ID = "extra_exercise_id";

	private ExerciseAdapter exerciseAdapter;
	private ExerciseViewModel exerciseViewModel;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercise);

		String categoryId = getIntent().getStringExtra(MainActivity.EXTRA_CATEGORY_ID);
		String categoryTitle = getIntent().getStringExtra(MainActivity.EXTRA_CATEGORY_TITLE);

		setupToolbar(categoryTitle);
		setupRecyclerView();
		setupViewModel(categoryId);
	}

	private void setupToolbar(String categoryTitle) {
		MaterialToolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(categoryTitle != null ? categoryTitle : getString(R.string.title_exercises));
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
	}

	private void setupRecyclerView() {
		RecyclerView recyclerView = findViewById(R.id.recyclerExercises);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setHasFixedSize(true);
		exerciseAdapter = new ExerciseAdapter(exercise -> {
			Intent intent = new Intent(ExerciseActivity.this, ExerciseDetailActivity.class);
			intent.putExtra(EXTRA_EXERCISE_ID, exercise.getId());
			startActivity(intent);
		});
		recyclerView.setAdapter(exerciseAdapter);
	}

	private void setupViewModel(String categoryId) {
		exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
		exerciseViewModel.getExercises().observe(this, exercises -> exerciseAdapter.submitExercises(exercises));
		exerciseViewModel.loadExercises(categoryId);
	}
}
