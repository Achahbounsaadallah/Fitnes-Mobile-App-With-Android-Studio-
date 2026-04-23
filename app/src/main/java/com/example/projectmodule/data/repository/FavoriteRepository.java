package com.example.projectmodule.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteRepository {

    private static final Map<Long, MutableLiveData<Set<String>>> FAVORITES_CACHE = new HashMap<>();

    private final MySQLiteHelper dbHelper;

    public FavoriteRepository(Context context) {
        dbHelper = new MySQLiteHelper(context.getApplicationContext());
    }

    public LiveData<Set<String>> getFavoriteIdsLiveData(long userId) {
        MutableLiveData<Set<String>> liveData = FAVORITES_CACHE.get(userId);
        if (liveData == null) {
            liveData = new MutableLiveData<>(new HashSet<>(getFavoriteIds(userId)));
            FAVORITES_CACHE.put(userId, liveData);
        }
        return liveData;
    }

    public boolean isFavorite(long userId, String exerciseId) {
        if (userId <= 0 || isBlank(exerciseId)) {
            return false;
        }
        return getFavoriteIds(userId).contains(exerciseId);
    }

    public boolean toggleFavorite(long userId, String exerciseId) {
        if (userId <= 0 || isBlank(exerciseId)) {
            return false;
        }

        if (isFavorite(userId, exerciseId)) {
            removeFavorite(userId, exerciseId);
            return false;
        }
        addFavorite(userId, exerciseId);
        return true;
    }

    public void addFavorite(long userId, String exerciseId) {
        if (userId <= 0 || isBlank(exerciseId)) {
            return;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_FAVORITE_USER_ID, userId);
        values.put(MySQLiteHelper.COLUMN_FAVORITE_EXERCISE_ID, exerciseId);
        database.insertWithOnConflict(
                MySQLiteHelper.TABLE_FAVORITES,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
        );
        refreshCache(userId);
    }

    public void removeFavorite(long userId, String exerciseId) {
        if (userId <= 0 || isBlank(exerciseId)) {
            return;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.delete(
                MySQLiteHelper.TABLE_FAVORITES,
                MySQLiteHelper.COLUMN_FAVORITE_USER_ID + " = ? AND " + MySQLiteHelper.COLUMN_FAVORITE_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(userId), exerciseId}
        );
        refreshCache(userId);
    }

    public Set<String> getFavoriteIds(long userId) {
        if (userId <= 0) {
            return Collections.emptySet();
        }

        Set<String> favoriteIds = new HashSet<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_FAVORITES,
                    new String[]{MySQLiteHelper.COLUMN_FAVORITE_EXERCISE_ID},
                    MySQLiteHelper.COLUMN_FAVORITE_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    MySQLiteHelper.COLUMN_CREATED_AT + " DESC"
            );

            while (cursor.moveToNext()) {
                favoriteIds.add(cursor.getString(0));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return favoriteIds;
    }

    public List<String> getFavoriteIdsList(long userId) {
        if (userId <= 0) {
            return Collections.emptyList();
        }

        List<String> favoriteIds = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_FAVORITES,
                    new String[]{MySQLiteHelper.COLUMN_FAVORITE_EXERCISE_ID},
                    MySQLiteHelper.COLUMN_FAVORITE_USER_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null,
                    null,
                    MySQLiteHelper.COLUMN_CREATED_AT + " DESC"
            );

            while (cursor.moveToNext()) {
                favoriteIds.add(cursor.getString(0));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return favoriteIds;
    }

    private void refreshCache(long userId) {
        MutableLiveData<Set<String>> liveData = FAVORITES_CACHE.get(userId);
        if (liveData == null) {
            liveData = new MutableLiveData<>();
            FAVORITES_CACHE.put(userId, liveData);
        }
        liveData.postValue(new HashSet<>(getFavoriteIds(userId)));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

