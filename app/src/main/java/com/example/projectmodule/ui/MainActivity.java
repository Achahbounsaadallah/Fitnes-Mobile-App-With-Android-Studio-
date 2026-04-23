package com.example.projectmodule.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectmodule.R;
import com.example.projectmodule.data.model.Category;
import com.example.projectmodule.data.repository.SessionManager;
import com.example.projectmodule.viewmodel.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

	public static final String EXTRA_CATEGORY_ID = "extra_category_id";
	public static final String EXTRA_CATEGORY_TITLE = "extra_category_title";

	private CategoryAdapter categoryAdapter;
	private MainViewModel mainViewModel;
	private SessionManager sessionManager;
	private TextView sessionStatusText;
	private MaterialButton loginButton;
	private MaterialButton signUpButton;
	private MaterialButton weeklyPlanButton;
	private MaterialButton logoutButton;
	private MaterialButton favoritesButton;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		sessionManager = new SessionManager(this);
		setupToolbar();
		setupAuthButtons();
		setupRecyclerView();
		setupViewModel();
		updateAuthUi();
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateAuthUi();
	}

	private void setupToolbar() {
		MaterialToolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle(R.string.title_home);
		}
	}

	private void setupAuthButtons() {
		sessionStatusText = findViewById(R.id.sessionStatusText);
		loginButton = findViewById(R.id.loginButton);
		signUpButton = findViewById(R.id.signUpButton);
		weeklyPlanButton = findViewById(R.id.weeklyPlanButton);
		logoutButton = findViewById(R.id.logoutButton);
		favoritesButton = findViewById(R.id.favoritesButton);

		loginButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
		signUpButton.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
		weeklyPlanButton.setOnClickListener(v -> openWeeklyPlan());
		favoritesButton.setOnClickListener(v -> openFavorites());
		logoutButton.setOnClickListener(v -> {
			sessionManager.clearSession();
			updateAuthUi();
		});
	}

	private void updateAuthUi() {
		boolean loggedIn = sessionManager.isLoggedIn();
		String displayName = sessionManager.getDisplayName();
		String username = sessionManager.getUsername();
		String userLabel = displayName != null && !displayName.trim().isEmpty()
				? displayName
				: (username != null ? username : getString(R.string.guest_user));
		sessionStatusText.setText(getString(loggedIn ? R.string.logged_in_as : R.string.not_logged_in, userLabel));
		weeklyPlanButton.setText(loggedIn ? R.string.open_weekly_plan : R.string.login_to_weekly_plan);
		logoutButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);
	}

	private void openWeeklyPlan() {
		Intent intent = sessionManager.isLoggedIn()
				? new Intent(this, WeeklyPlanActivity.class)
				: new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void openFavorites() {
		Intent intent = sessionManager.isLoggedIn()
				? new Intent(this, FavoritesActivity.class)
				: new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void setupRecyclerView() {
		RecyclerView recyclerView = findViewById(R.id.recyclerCategories);
		recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
		recyclerView.setHasFixedSize(true);
		categoryAdapter = new CategoryAdapter(this);
		recyclerView.setAdapter(categoryAdapter);
	}

	private void setupViewModel() {
		mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
		mainViewModel.getCategories().observe(this, categories -> categoryAdapter.submitCategories(categories));
	}

	@Override
	public void onCategoryClick(Category category) {
		Intent intent = new Intent(this, ExerciseActivity.class);
		intent.putExtra(EXTRA_CATEGORY_ID, category.getId());
		intent.putExtra(EXTRA_CATEGORY_TITLE, category.getTitle());
		startActivity(intent);
	}
}
