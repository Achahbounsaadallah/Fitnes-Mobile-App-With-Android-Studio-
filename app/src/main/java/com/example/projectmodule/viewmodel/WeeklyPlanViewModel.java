package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.WeeklyPlan;
import com.example.projectmodule.data.model.WeeklyTask;
import com.example.projectmodule.data.repository.WeeklyPlanRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeeklyPlanViewModel extends AndroidViewModel {

    private final WeeklyPlanRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<WeeklyPlan> currentPlan;
    private final MutableLiveData<List<WeeklyTask>> tasks;
    private final MutableLiveData<String> message;
    private long currentUserId = -1L;
    private long currentPlanId = -1L;

    public WeeklyPlanViewModel(@NonNull Application application) {
        super(application);
        repository = new WeeklyPlanRepository(application.getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        currentPlan = new MutableLiveData<>();
        tasks = new MutableLiveData<>();
        message = new MutableLiveData<>();
    }

    public LiveData<WeeklyPlan> getCurrentPlan() {
        return currentPlan;
    }

    public LiveData<List<WeeklyTask>> getTasks() {
        return tasks;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadCurrentPlan(long userId) {
        currentUserId = userId;
        executorService.execute(() -> {
            try {
                WeeklyPlan plan = repository.getOrCreateCurrentWeekPlan(userId);
                currentPlanId = plan.getId();
                currentPlan.postValue(plan);
                tasks.postValue(plan.getTasks());
            } catch (SQLException exception) {
                message.postValue(exception.getMessage());
            }
        });
    }

    public void addTask(String title) {
        if (currentPlanId <= 0) {
            message.postValue("Weekly plan not loaded");
            return;
        }

        executorService.execute(() -> {
            try {
                repository.addTask(currentPlanId, title);
                reloadCurrentPlan();
            } catch (SQLException exception) {
                message.postValue(exception.getMessage());
            }
        });
    }

    public void updateTaskStatus(WeeklyTask task, String status) {
        if (task == null) {
            return;
        }

        executorService.execute(() -> {
            try {
                repository.updateTaskStatus(task.getId(), status);
                task.setStatus(status);
                reloadCurrentPlan();
            } catch (SQLException exception) {
                message.postValue(exception.getMessage());
            }
        });
    }

    public void closeCurrentWeek() {
        if (currentUserId <= 0) {
            return;
        }

        executorService.execute(() -> {
            try {
                repository.closeCurrentWeek(currentUserId);
                reloadCurrentPlan();
                message.postValue("Week archived successfully");
            } catch (SQLException exception) {
                message.postValue(exception.getMessage());
            }
        });
    }

    private void reloadCurrentPlan() throws SQLException {
        WeeklyPlan plan = repository.getOrCreateCurrentWeekPlan(currentUserId);
        currentPlanId = plan.getId();
        currentPlan.postValue(plan);
        tasks.postValue(plan.getTasks());
    }
}

