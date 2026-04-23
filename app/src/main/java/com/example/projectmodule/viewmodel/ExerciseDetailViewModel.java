package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.repository.FavoriteRepository;
import com.example.projectmodule.data.repository.FitnessRepository;
import com.example.projectmodule.data.repository.SessionManager;

import java.util.Collections;
import java.util.Set;

public class ExerciseDetailViewModel extends AndroidViewModel {

    private final FitnessRepository repository;
    private final FavoriteRepository favoriteRepository;
    private final SessionManager sessionManager;
    private final MutableLiveData<Exercise> exercise;
    private final LiveData<Set<String>> favoriteIds;
    private final MediatorLiveData<Boolean> isFavorite;

    public ExerciseDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new FitnessRepository();
        favoriteRepository = new FavoriteRepository(application);
        sessionManager = new SessionManager(application);
        exercise = new MutableLiveData<>();

        long userId = sessionManager.getUserId();
        if (userId > 0) {
            favoriteIds = favoriteRepository.getFavoriteIdsLiveData(userId);
        } else {
            MutableLiveData<Set<String>> guestFavorites = new MutableLiveData<>();
            guestFavorites.setValue(Collections.emptySet());
            favoriteIds = guestFavorites;
        }

        isFavorite = new MediatorLiveData<>(false);
        isFavorite.addSource(exercise, value -> updateFavoriteState());
        isFavorite.addSource(favoriteIds, value -> updateFavoriteState());
    }

    public void loadExercise(String exerciseId) {
        exercise.setValue(repository.getExerciseById(exerciseId));
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }

    public LiveData<Boolean> getIsFavorite() {
        return isFavorite;
    }

    public boolean toggleFavorite() {
        Exercise currentExercise = exercise.getValue();
        long userId = sessionManager.getUserId();
        if (currentExercise == null || userId <= 0) {
            return false;
        }
        favoriteRepository.toggleFavorite(userId, currentExercise.getId());
        return true;
    }

    private void updateFavoriteState() {
        Exercise currentExercise = exercise.getValue();
        Set<String> favorites = favoriteIds.getValue();
        boolean value = currentExercise != null && favorites != null && favorites.contains(currentExercise.getId());
        isFavorite.setValue(value);
    }
}

