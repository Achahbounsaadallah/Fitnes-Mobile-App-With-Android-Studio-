package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectmodule.R;
import com.example.projectmodule.data.repository.SessionManager;
import com.example.projectmodule.viewmodel.AuthViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    private AuthViewModel viewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sessionManager = new SessionManager(this);
        setupToolbar();
        setupActions();
        setupObservers();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_sign_up);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void setupActions() {
        TextInputEditText displayNameInput = findViewById(R.id.displayNameInput);
        TextInputEditText usernameInput = findViewById(R.id.usernameInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);
        TextInputEditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String displayName = getText(displayNameInput);
            String username = getText(usernameInput);
            String password = getText(passwordInput);
            String confirmPassword = getText(confirmPasswordInput);

            if (!password.equals(confirmPassword)) {
                Snackbar.make(findViewById(android.R.id.content), R.string.passwords_do_not_match, Snackbar.LENGTH_LONG).show();
                return;
            }

            viewModel.signUp(displayName, username, password);
        });
    }

    private void setupObservers() {
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        View root = findViewById(android.R.id.content);

        viewModel.getAuthenticatedUser().observe(this, userAccount -> {
            if (userAccount != null) {
                sessionManager.saveSession(userAccount);
                startActivity(new Intent(this, WeeklyPlanActivity.class));
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.trim().isEmpty()) {
                Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String getText(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}

