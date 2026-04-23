package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.Category;
import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.model.FavoriteSection;
import com.example.projectmodule.data.repository.FavoriteRepository;
import com.example.projectmodule.data.repository.FitnessRepository;
import com.example.projectmodule.data.repository.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoritesViewModel extends AndroidViewModel {

    private final FitnessRepository fitnessRepository;
    private final FavoriteRepository favoriteRepository;
    private final SessionManager sessionManager;
    private final LiveData<Set<String>> favoriteIds;
    private final MediatorLiveData<List<FavoriteSection>> sections;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        fitnessRepository = new FitnessRepository();
        favoriteRepository = new FavoriteRepository(application);
        sessionManager = new SessionManager(application);

        long userId = sessionManager.getUserId();
        if (userId > 0) {
            favoriteIds = favoriteRepository.getFavoriteIdsLiveData(userId);
        } else {
            MutableLiveData<Set<String>> empty = new MutableLiveData<>();
            empty.setValue(Collections.emptySet());
            favoriteIds = empty;
        }

        sections = new MediatorLiveData<>(Collections.emptyList());
        sections.addSource(favoriteIds, value -> rebuildSections());
    }

    public LiveData<List<FavoriteSection>> getSections() {
        return sections;
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public boolean toggleFavorite(String exerciseId) {
        long userId = sessionManager.getUserId();
        if (userId <= 0) {
            return false;
        }
        favoriteRepository.toggleFavorite(userId, exerciseId);
        return true;
    }

    private void rebuildSections() {
        Set<String> favorites = favoriteIds.getValue();
        if (favorites == null || favorites.isEmpty()) {
            sections.setValue(Collections.emptyList());
            return;
        }

        Set<String> favoriteSet = new HashSet<>(favorites);
        Map<String, List<Exercise>> grouped = new LinkedHashMap<>();
        for (Category category : fitnessRepository.getCategories()) {
            grouped.put(category.getId(), new ArrayList<>());
        }

        for (Exercise exercise : fitnessRepository.getAllExercises()) {
            if (favoriteSet.contains(exercise.getId())) {
                List<Exercise> categoryExercises = grouped.get(exercise.getCategoryId());
                if (categoryExercises != null) {
                    categoryExercises.add(exercise);
                }
            }
        }

        List<FavoriteSection> mappedSections = new ArrayList<>();
        for (Category category : fitnessRepository.getCategories()) {
            List<Exercise> categoryExercises = grouped.get(category.getId());
            if (categoryExercises != null && !categoryExercises.isEmpty()) {
                mappedSections.add(new FavoriteSection(category.getId(), category.getTitle(), categoryExercises));
            }
        }
        sections.setValue(mappedSections);
    }
}

