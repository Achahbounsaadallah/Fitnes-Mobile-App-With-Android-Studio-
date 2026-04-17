package com.example.projectmodule.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectmodule.data.model.UserAccount;
import com.example.projectmodule.data.repository.AuthRepository;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;
    private final ExecutorService executorService;
    private final MutableLiveData<UserAccount> authenticatedUser;
    private final MutableLiveData<String> errorMessage;
    private final MutableLiveData<Boolean> loading;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application.getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();
        authenticatedUser = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
        loading = new MutableLiveData<>(false);
    }

    public LiveData<UserAccount> getAuthenticatedUser() {
        return authenticatedUser;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void login(String username, String password) {
        loading.postValue(true);
        executorService.execute(() -> {
            try {
                UserAccount account = repository.login(username, password);
                if (account == null) {
                    errorMessage.postValue("Invalid username or password");
                } else {
                    authenticatedUser.postValue(account);
                }
            } catch (SQLException exception) {
                errorMessage.postValue(exception.getMessage());
            } finally {
                loading.postValue(false);
            }
        });
    }

    public void signUp(String displayName, String username, String password) {
        loading.postValue(true);
        executorService.execute(() -> {
            try {
                UserAccount account = repository.register(username, displayName, password);
                authenticatedUser.postValue(account);
            } catch (SQLException exception) {
                errorMessage.postValue(exception.getMessage());
            } finally {
                loading.postValue(false);
            }
        });
    }
}

