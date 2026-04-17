package com.example.projectmodule.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.projectmodule.data.model.UserAccount;

public class SessionManager {

    private static final String PREFS_NAME = "fitness_session_prefs";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_DISPLAY_NAME = "key_display_name";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(UserAccount userAccount) {
        preferences.edit()
                .putLong(KEY_USER_ID, userAccount.getId())
                .putString(KEY_USERNAME, userAccount.getUsername())
                .putString(KEY_DISPLAY_NAME, userAccount.getDisplayName())
                .apply();
    }

    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, -1L);
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    public String getDisplayName() {
        return preferences.getString(KEY_DISPLAY_NAME, null);
    }

    public boolean isLoggedIn() {
        return getUserId() > 0;
    }

    public void clearSession() {
        preferences.edit().clear().apply();
    }
}

