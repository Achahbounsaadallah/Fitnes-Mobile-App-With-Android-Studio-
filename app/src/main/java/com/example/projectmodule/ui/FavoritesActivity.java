package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.viewmodel.FavoritesViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class FavoritesActivity extends AppCompatActivity {

    private FavoritesAdapter adapter;
    private FavoritesViewModel viewModel;
    private TextView emptyStateText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupToolbar();
        setupRecycler();
        setupViewModel();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_favorites);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerFavorites);
        emptyStateText = findViewById(R.id.emptyFavoritesText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new FavoritesAdapter(
                exercise -> {
                    Intent intent = new Intent(this, ExerciseDetailActivity.class);
                    intent.putExtra(ExerciseActivity.EXTRA_EXERCISE_ID, exercise.getId());
                    startActivity(intent);
                },
                exercise -> viewModel.toggleFavorite(exercise.getId())
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        View root = findViewById(android.R.id.content);

        if (!viewModel.isLoggedIn()) {
            Snackbar.make(root, R.string.login_required_for_favorites, Snackbar.LENGTH_LONG).show();
        }

        viewModel.getSections().observe(this, sections -> {
            adapter.submitSections(sections);
            boolean hasFavorites = sections != null && !sections.isEmpty();
            emptyStateText.setVisibility(hasFavorites ? View.GONE : View.VISIBLE);
            emptyStateText.setText(viewModel.isLoggedIn()
                    ? getString(R.string.no_favorites_yet)
                    : getString(R.string.login_to_view_favorites));
        });
    }
}

