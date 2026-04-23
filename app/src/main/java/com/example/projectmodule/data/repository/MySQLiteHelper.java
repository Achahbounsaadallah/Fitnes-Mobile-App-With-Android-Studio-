package com.example.projectmodule.data.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fitnessflow.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String TABLE_WEEKLY_PLANS = "weekly_plans";
    public static final String COLUMN_PLAN_ID = "id";
    public static final String COLUMN_PLAN_USER_ID = "user_id";
    public static final String COLUMN_WEEK_START = "week_start";
    public static final String COLUMN_WEEK_END = "week_end";
    public static final String COLUMN_PLAN_STATUS = "status";

    public static final String TABLE_WEEKLY_TASKS = "weekly_tasks";
    public static final String COLUMN_TASK_ID = "id";
    public static final String COLUMN_TASK_PLAN_ID = "plan_id";
    public static final String COLUMN_TASK_TITLE = "task_title";
    public static final String COLUMN_TASK_STATUS = "status";
    public static final String COLUMN_TASK_ORDER = "task_order";

    public static final String TABLE_FAVORITES = "favorites";
    public static final String COLUMN_FAVORITE_ID = "id";
    public static final String COLUMN_FAVORITE_USER_ID = "user_id";
    public static final String COLUMN_FAVORITE_EXERCISE_ID = "exercise_id";

    private static final String CREATE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, "
            + COLUMN_DISPLAY_NAME + " TEXT, "
            + COLUMN_PASSWORD_HASH + " TEXT NOT NULL, "
            + COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now'))"
            + ")";

    private static final String CREATE_WEEKLY_PLANS = "CREATE TABLE IF NOT EXISTS " + TABLE_WEEKLY_PLANS + " ("
            + COLUMN_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PLAN_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_WEEK_START + " TEXT NOT NULL, "
            + COLUMN_WEEK_END + " TEXT NOT NULL, "
            + COLUMN_PLAN_STATUS + " TEXT NOT NULL DEFAULT 'ACTIVE', "
            + COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
            + "FOREIGN KEY(" + COLUMN_PLAN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    private static final String CREATE_WEEKLY_TASKS = "CREATE TABLE IF NOT EXISTS " + TABLE_WEEKLY_TASKS + " ("
            + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TASK_PLAN_ID + " INTEGER NOT NULL, "
            + COLUMN_TASK_TITLE + " TEXT NOT NULL, "
            + COLUMN_TASK_STATUS + " TEXT, "
            + COLUMN_TASK_ORDER + " INTEGER NOT NULL DEFAULT 0, "
            + COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
            + "FOREIGN KEY(" + COLUMN_TASK_PLAN_ID + ") REFERENCES " + TABLE_WEEKLY_PLANS + "(" + COLUMN_PLAN_ID + ")"
            + ")";

    private static final String CREATE_FAVORITES = "CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + " ("
            + COLUMN_FAVORITE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_FAVORITE_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_FAVORITE_EXERCISE_ID + " TEXT NOT NULL, "
            + COLUMN_CREATED_AT + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
            + "UNIQUE(" + COLUMN_FAVORITE_USER_ID + ", " + COLUMN_FAVORITE_EXERCISE_ID + "), "
            + "FOREIGN KEY(" + COLUMN_FAVORITE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
            + ")";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_WEEKLY_PLANS);
        db.execSQL(CREATE_WEEKLY_TASKS);
        db.execSQL(CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_FAVORITES);
        }
    }
}

