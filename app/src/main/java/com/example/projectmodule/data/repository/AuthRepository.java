package com.example.projectmodule.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectmodule.data.model.UserAccount;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class AuthRepository {

    private final MySQLiteHelper dbHelper;

    public AuthRepository(Context context) {
        dbHelper = new MySQLiteHelper(context.getApplicationContext());
    }

    public UserAccount register(String username, String displayName, String password) throws SQLException {
        String normalizedUsername = normalize(username);
        if (normalizedUsername.isEmpty() || normalize(password).isEmpty()) {
            throw new SQLException("Username and password are required");
        }

        String passwordHash = hashPassword(password);

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            if (usernameExists(database, normalizedUsername)) {
                throw new SQLException("Username already exists");
            }

            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_USERNAME, normalizedUsername);
            values.put(MySQLiteHelper.COLUMN_DISPLAY_NAME, normalize(displayName));
            values.put(MySQLiteHelper.COLUMN_PASSWORD_HASH, passwordHash);

            long userId = database.insertOrThrow(MySQLiteHelper.TABLE_USERS, null, values);
            return new UserAccount(userId, normalizedUsername, normalize(displayName));
        } catch (Exception exception) {
            if (exception instanceof SQLException) {
                throw (SQLException) exception;
            }
            throw new SQLException("Could not create account", exception);
        }
    }

    public UserAccount login(String username, String password) throws SQLException {
        String normalizedUsername = normalize(username);
        String passwordHash = hashPassword(password);

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_USERS,
                    new String[]{MySQLiteHelper.COLUMN_USER_ID, MySQLiteHelper.COLUMN_USERNAME, MySQLiteHelper.COLUMN_DISPLAY_NAME},
                    MySQLiteHelper.COLUMN_USERNAME + " = ? AND " + MySQLiteHelper.COLUMN_PASSWORD_HASH + " = ?",
                    new String[]{normalizedUsername, passwordHash},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                return new UserAccount(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
            }
            return null;
        } catch (Exception exception) {
            throw new SQLException("Login failed", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean usernameExists(SQLiteDatabase database, String username) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_USERS,
                    new String[]{MySQLiteHelper.COLUMN_USER_ID},
                    MySQLiteHelper.COLUMN_USERNAME + " = ?",
                    new String[]{username},
                    null,
                    null,
                    null,
                    "1"
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String hashPassword(String password) throws SQLException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(normalize(password).getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte aByte : bytes) {
                builder.append(String.format("%02x", aByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new SQLException("Could not hash password", e);
        }
    }
}

