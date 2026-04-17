package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.WeeklyPlan;
import com.example.projectmodule.data.repository.WeeklyPlanRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends AndroidViewModel {

    private final WeeklyPlanRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<List<WeeklyPlan>> historyPlans;
    private final MutableLiveData<String> errorMessage;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new WeeklyPlanRepository(application.getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        historyPlans = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<List<WeeklyPlan>> getHistoryPlans() {
        return historyPlans;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadHistory(long userId) {
        executorService.execute(() -> {
            try {
                historyPlans.postValue(repository.getHistory(userId));
            } catch (SQLException exception) {
                errorMessage.postValue(exception.getMessage());
            }
        });
    }
}

