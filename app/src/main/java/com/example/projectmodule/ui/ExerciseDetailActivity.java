package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectmodule.R;
import com.example.projectmodule.viewmodel.ExerciseDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class ExerciseDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        setupToolbar();
        setupViewModel();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_exercise_detail);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViewModel() {
        String exerciseId = getIntent().getStringExtra(ExerciseActivity.EXTRA_EXERCISE_ID);

        final ImageView detailImage = findViewById(R.id.detailImage);
        final ImageView favoriteButton = findViewById(R.id.detailFavoriteButton);
        final TextView detailTitle = findViewById(R.id.detailTitle);
        final TextView detailDescription = findViewById(R.id.detailDescription);

        ExerciseDetailViewModel viewModel = new ViewModelProvider(this).get(ExerciseDetailViewModel.class);
        viewModel.getExercise().observe(this, exercise -> {
            if (exercise == null) {
                detailTitle.setText(R.string.exercise_not_found_title);
                detailDescription.setText(R.string.exercise_not_found_desc);
                detailImage.setImageResource(R.drawable.ic_menu_compass);
                detailImage.setOnClickListener(null);
                favoriteButton.setOnClickListener(null);
                favoriteButton.setImageResource(R.drawable.ic_star_outline);
                return;
            }

            detailTitle.setText(exercise.getName());
            detailDescription.setText(exercise.getDetailDescription());
            detailImage.setImageResource(exercise.getImageResId());
            detailImage.setOnClickListener(v -> {
                Intent intent = new Intent(ExerciseDetailActivity.this, ImageViewerActivity.class);
                intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_RES_ID, exercise.getImageResId());
                intent.putExtra(ImageViewerActivity.EXTRA_IMAGE_TITLE, exercise.getName());
                startActivity(intent);
            });

            favoriteButton.setOnClickListener(v -> {
                boolean updated = viewModel.toggleFavorite();
                if (!updated) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.login_required_for_favorites, Snackbar.LENGTH_SHORT).show();
                }
            });
        });

        viewModel.getIsFavorite().observe(this, isFavorite ->
                favoriteButton.setImageResource(Boolean.TRUE.equals(isFavorite)
                        ? R.drawable.ic_star_filled
                        : R.drawable.ic_star_outline));
        viewModel.loadExercise(exerciseId);
    }
}

