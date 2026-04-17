package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.repository.SessionManager;
import com.example.projectmodule.viewmodel.HistoryViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class HistoryActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private HistoryWeekAdapter adapter;
    private HistoryViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        setupViewModel();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_history);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.historyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new HistoryWeekAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        View root = findViewById(android.R.id.content);

        viewModel.getHistoryPlans().observe(this, plans -> adapter.submitHistory(plans));
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.loadHistory(sessionManager.getUserId());
    }
}

