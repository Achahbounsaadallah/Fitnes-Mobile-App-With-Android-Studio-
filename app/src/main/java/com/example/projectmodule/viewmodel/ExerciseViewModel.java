package com.example.projectmodule.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectmodule.data.model.Exercise;
import com.example.projectmodule.data.repository.FitnessRepository;

import java.util.List;

public class ExerciseViewModel extends ViewModel {

	private final FitnessRepository repository;
	private final MutableLiveData<List<Exercise>> exercises;

	public ExerciseViewModel() {
		repository = new FitnessRepository();
		exercises = new MutableLiveData<>();
	}

	public void loadExercises(String categoryId) {
		exercises.setValue(repository.getExercisesForCategory(categoryId));
	}

	public LiveData<List<Exercise>> getExercises() {
		return exercises;
	}
}
