package com.example.projectmodule.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectmodule.data.model.Category;
import com.example.projectmodule.data.repository.FitnessRepository;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final FitnessRepository repository;
    private final MutableLiveData<List<Category>> categories;

    public MainViewModel() {
        repository = new FitnessRepository();
        categories = new MutableLiveData<>();
        loadCategories();
    }

    private void loadCategories() {
        categories.setValue(repository.getCategories());
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }
}

