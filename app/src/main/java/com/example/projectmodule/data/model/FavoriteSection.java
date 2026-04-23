package com.example.projectmodule.data.model;

import java.util.List;

public class FavoriteSection {

    private final String categoryId;
    private final String categoryTitle;
    private final List<Exercise> exercises;

    public FavoriteSection(String categoryId, String categoryTitle, List<Exercise> exercises) {
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
        this.exercises = exercises;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}

