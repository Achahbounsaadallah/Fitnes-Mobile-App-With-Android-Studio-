package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.WeeklyTask;
import com.example.projectmodule.data.repository.SessionManager;
import com.example.projectmodule.viewmodel.WeeklyPlanViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class WeeklyPlanActivity extends AppCompatActivity implements WeeklyTaskAdapter.OnTaskActionListener {

    private SessionManager sessionManager;
    private WeeklyPlanViewModel viewModel;
    private WeeklyTaskAdapter adapter;
    private TextView weekLabel;
    private TextView weekRange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_plan);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setupToolbar();
        setupViews();
        setupViewModel();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_weekly_plan);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupViews() {
        weekLabel = findViewById(R.id.weekLabel);
        weekRange = findViewById(R.id.weekRange);
        RecyclerView recyclerView = findViewById(R.id.weeklyTaskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new WeeklyTaskAdapter(this);
        recyclerView.setAdapter(adapter);

        MaterialButton addTaskButton = findViewById(R.id.addTaskButton);
        MaterialButton closeWeekButton = findViewById(R.id.closeWeekButton);
        MaterialButton historyButton = findViewById(R.id.historyButton);
        MaterialButton logoutButton = findViewById(R.id.logoutButton);

        addTaskButton.setOnClickListener(v -> showAddTaskDialog());
        closeWeekButton.setOnClickListener(v -> viewModel.closeCurrentWeek());
        historyButton.setOnClickListener(v -> startActivity(new Intent(this, HistoryActivity.class)));
        logoutButton.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(WeeklyPlanViewModel.class);
        View root = findViewById(android.R.id.content);

        viewModel.getCurrentPlan().observe(this, plan -> {
            if (plan != null) {
                weekLabel.setText(getString(R.string.weekly_plan_for_user, safeName()));
                weekRange.setText(getString(R.string.week_range_format, plan.getWeekStart(), plan.getWeekEnd()));
            }
        });

        viewModel.getTasks().observe(this, tasks -> adapter.submitTasks(tasks));

        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.loadCurrentPlan(sessionManager.getUserId());
    }

    private void showAddTaskDialog() {
        final EditText input = new EditText(this);
        input.setHint(R.string.task_hint);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_task_title)
                .setView(input)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String title = input.getText() == null ? "" : input.getText().toString().trim();
                    viewModel.addTask(title);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onYesClicked(WeeklyTask task) {
        viewModel.updateTaskStatus(task, WeeklyTask.STATUS_YES);
    }

    @Override
    public void onNoClicked(WeeklyTask task) {
        viewModel.updateTaskStatus(task, WeeklyTask.STATUS_NO);
    }

    private String safeName() {
        String displayName = sessionManager.getDisplayName();
        if (displayName == null || displayName.trim().isEmpty()) {
            String username = sessionManager.getUsername();
            return username == null ? getString(R.string.app_name) : username;
        }
        return displayName;
    }
}

