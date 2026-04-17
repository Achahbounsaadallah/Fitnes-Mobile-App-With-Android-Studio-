package com.example.projectmodule.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.repository.FitnessRepository;

public class ExerciseDetailViewModel extends ViewModel {

    private final FitnessRepository repository;
    private final MutableLiveData<Exercise> exercise;

    public ExerciseDetailViewModel() {
        repository = new FitnessRepository();
        exercise = new MutableLiveData<>();
    }

    public void loadExercise(String exerciseId) {
        exercise.setValue(repository.getExerciseById(exerciseId));
    }

    public LiveData<Exercise> getExercise() {
        return exercise;
    }
}

