package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.repository.FavoriteRepository;
import com.example.projectmodule.data.repository.FitnessRepository;
import com.example.projectmodule.data.repository.SessionManager;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ExerciseViewModel extends AndroidViewModel {

	private final FitnessRepository repository;
	private final FavoriteRepository favoriteRepository;
	private final SessionManager sessionManager;
	private final MutableLiveData<List<Exercise>> exercises;
	private final LiveData<Set<String>> favoriteIds;

	public ExerciseViewModel(@NonNull Application application) {
		super(application);
		repository = new FitnessRepository();
		favoriteRepository = new FavoriteRepository(application);
		sessionManager = new SessionManager(application);
		exercises = new MutableLiveData<>();

		long userId = sessionManager.getUserId();
		if (userId > 0) {
			favoriteIds = favoriteRepository.getFavoriteIdsLiveData(userId);
		} else {
			MutableLiveData<Set<String>> guestFavorites = new MutableLiveData<>();
			guestFavorites.setValue(Collections.emptySet());
			favoriteIds = guestFavorites;
		}
	}

	public void loadExercises(String categoryId) {
		exercises.setValue(repository.getExercisesForCategory(categoryId));
	}

	public LiveData<List<Exercise>> getExercises() {
		return exercises;
	}

	public LiveData<Set<String>> getFavoriteIds() {
		return favoriteIds;
	}

	public boolean toggleFavorite(String exerciseId) {
		long userId = sessionManager.getUserId();
		if (userId <= 0) {
			return false;
		}
		favoriteRepository.toggleFavorite(userId, exerciseId);
		return true;
	}
}
