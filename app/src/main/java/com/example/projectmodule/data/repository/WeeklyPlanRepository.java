package com.example.projectmodule.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.projectmodule.data.model.WeeklyPlan;
import com.example.projectmodule.data.model.WeeklyTask;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WeeklyPlanRepository {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final MySQLiteHelper dbHelper;

    public WeeklyPlanRepository(Context context) {
        dbHelper = new MySQLiteHelper(context.getApplicationContext());
    }

    public WeeklyPlan getOrCreateCurrentWeekPlan(long userId) throws SQLException {
        WeekRange currentWeek = getCurrentWeekRange();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            closeExpiredActivePlanIfNeeded(database, userId, currentWeek);

            WeeklyPlan existingPlan = findActivePlanForWeek(database, userId, currentWeek);
            if (existingPlan != null) {
                loadTasks(database, existingPlan);
                return existingPlan;
            }

            WeeklyPlan createdPlan = createPlan(database, userId, currentWeek);
            loadTasks(database, createdPlan);
            return createdPlan;
        } catch (Exception exception) {
            throw new SQLException("Could not load weekly plan", exception);
        }
    }

    public List<WeeklyPlan> getHistory(long userId) throws SQLException {
        List<WeeklyPlan> plans = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_WEEKLY_PLANS,
                    new String[]{
                            MySQLiteHelper.COLUMN_PLAN_ID,
                            MySQLiteHelper.COLUMN_PLAN_USER_ID,
                            MySQLiteHelper.COLUMN_WEEK_START,
                            MySQLiteHelper.COLUMN_WEEK_END,
                            MySQLiteHelper.COLUMN_PLAN_STATUS
                    },
                    MySQLiteHelper.COLUMN_PLAN_USER_ID + " = ? AND " + MySQLiteHelper.COLUMN_PLAN_STATUS + " = ?",
                    new String[]{String.valueOf(userId), WeeklyPlan.STATUS_CLOSED},
                    null,
                    null,
                    MySQLiteHelper.COLUMN_WEEK_START + " DESC"
            );

            while (cursor.moveToNext()) {
                WeeklyPlan plan = new WeeklyPlan(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
                loadTasks(database, plan);
                plans.add(plan);
            }
            return plans;
        } catch (Exception exception) {
            throw new SQLException("Could not load history", exception);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public WeeklyTask addTask(long planId, String title) throws SQLException {
        String normalizedTitle = normalize(title);
        if (normalizedTitle.isEmpty()) {
            throw new SQLException("Task title is required");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            int nextOrderIndex = getNextOrderIndex(database, planId);
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_TASK_PLAN_ID, planId);
            values.put(MySQLiteHelper.COLUMN_TASK_TITLE, normalizedTitle);
            values.put(MySQLiteHelper.COLUMN_TASK_STATUS, WeeklyTask.STATUS_PENDING);
            values.put(MySQLiteHelper.COLUMN_TASK_ORDER, nextOrderIndex);

            long taskId = database.insertOrThrow(MySQLiteHelper.TABLE_WEEKLY_TASKS, null, values);
            return new WeeklyTask(taskId, planId, normalizedTitle, WeeklyTask.STATUS_PENDING, nextOrderIndex);
        } catch (Exception exception) {
            throw new SQLException("Could not add task", exception);
        }
    }

    public void updateTaskStatus(long taskId, String status) throws SQLException {
        String normalizedStatus = normalizeStatus(status);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_TASK_STATUS, normalizedStatus);
            database.update(
                    MySQLiteHelper.TABLE_WEEKLY_TASKS,
                    values,
                    MySQLiteHelper.COLUMN_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)}
            );
        } catch (Exception exception) {
            throw new SQLException("Could not update task status", exception);
        }
    }

    public void closeCurrentWeek(long userId) throws SQLException {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_PLAN_STATUS, WeeklyPlan.STATUS_CLOSED);
            database.update(
                    MySQLiteHelper.TABLE_WEEKLY_PLANS,
                    values,
                    MySQLiteHelper.COLUMN_PLAN_USER_ID + " = ? AND " + MySQLiteHelper.COLUMN_PLAN_STATUS + " = ?",
                    new String[]{String.valueOf(userId), WeeklyPlan.STATUS_ACTIVE}
            );
        } catch (Exception exception) {
            throw new SQLException("Could not close current week", exception);
        }
    }

    private void closeExpiredActivePlanIfNeeded(SQLiteDatabase database, long userId, WeekRange currentWeek) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_WEEKLY_PLANS,
                    new String[]{MySQLiteHelper.COLUMN_PLAN_ID, MySQLiteHelper.COLUMN_WEEK_START, MySQLiteHelper.COLUMN_WEEK_END},
                    MySQLiteHelper.COLUMN_PLAN_USER_ID + " = ? AND " + MySQLiteHelper.COLUMN_PLAN_STATUS + " = ?",
                    new String[]{String.valueOf(userId), WeeklyPlan.STATUS_ACTIVE},
                    null,
                    null,
                    null
            );

            while (cursor.moveToNext()) {
                long planId = cursor.getLong(0);
                String storedStart = cursor.getString(1);
                String storedEnd = cursor.getString(2);
                if (!currentWeek.start.equals(storedStart) || !currentWeek.end.equals(storedEnd)) {
                    closePlanById(database, planId);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void closePlanById(SQLiteDatabase database, long planId) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PLAN_STATUS, WeeklyPlan.STATUS_CLOSED);
        database.update(
                MySQLiteHelper.TABLE_WEEKLY_PLANS,
                values,
                MySQLiteHelper.COLUMN_PLAN_ID + " = ?",
                new String[]{String.valueOf(planId)}
        );
    }

    private WeeklyPlan findActivePlanForWeek(SQLiteDatabase database, long userId, WeekRange weekRange) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_WEEKLY_PLANS,
                    new String[]{
                            MySQLiteHelper.COLUMN_PLAN_ID,
                            MySQLiteHelper.COLUMN_PLAN_USER_ID,
                            MySQLiteHelper.COLUMN_WEEK_START,
                            MySQLiteHelper.COLUMN_WEEK_END,
                            MySQLiteHelper.COLUMN_PLAN_STATUS
                    },
                    MySQLiteHelper.COLUMN_PLAN_USER_ID + " = ? AND "
                            + MySQLiteHelper.COLUMN_PLAN_STATUS + " = ? AND "
                            + MySQLiteHelper.COLUMN_WEEK_START + " = ? AND "
                            + MySQLiteHelper.COLUMN_WEEK_END + " = ?",
                    new String[]{String.valueOf(userId), WeeklyPlan.STATUS_ACTIVE, weekRange.start, weekRange.end},
                    null,
                    null,
                    null,
                    "1"
            );
            if (cursor.moveToFirst()) {
                return new WeeklyPlan(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                );
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private WeeklyPlan createPlan(SQLiteDatabase database, long userId, WeekRange weekRange) throws SQLException {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PLAN_USER_ID, userId);
        values.put(MySQLiteHelper.COLUMN_WEEK_START, weekRange.start);
        values.put(MySQLiteHelper.COLUMN_WEEK_END, weekRange.end);
        values.put(MySQLiteHelper.COLUMN_PLAN_STATUS, WeeklyPlan.STATUS_ACTIVE);

        long planId = database.insertOrThrow(MySQLiteHelper.TABLE_WEEKLY_PLANS, null, values);
        if (planId > 0) {
            return new WeeklyPlan(planId, userId, weekRange.start, weekRange.end, WeeklyPlan.STATUS_ACTIVE);
        }
        throw new SQLException("Could not create weekly plan");
    }

    private void loadTasks(SQLiteDatabase database, WeeklyPlan plan) {
        Cursor cursor = null;
        try {
            cursor = database.query(
                    MySQLiteHelper.TABLE_WEEKLY_TASKS,
                    new String[]{
                            MySQLiteHelper.COLUMN_TASK_ID,
                            MySQLiteHelper.COLUMN_TASK_PLAN_ID,
                            MySQLiteHelper.COLUMN_TASK_TITLE,
                            MySQLiteHelper.COLUMN_TASK_STATUS,
                            MySQLiteHelper.COLUMN_TASK_ORDER
                    },
                    MySQLiteHelper.COLUMN_TASK_PLAN_ID + " = ?",
                    new String[]{String.valueOf(plan.getId())},
                    null,
                    null,
                    MySQLiteHelper.COLUMN_TASK_ORDER + " ASC, " + MySQLiteHelper.COLUMN_TASK_ID + " ASC"
            );
            while (cursor.moveToNext()) {
                plan.addTask(new WeeklyTask(
                        cursor.getLong(0),
                        cursor.getLong(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4)
                ));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private int getNextOrderIndex(SQLiteDatabase database, long planId) {
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(
                    "SELECT IFNULL(MAX(" + MySQLiteHelper.COLUMN_TASK_ORDER + "), 0) + 1 FROM " + MySQLiteHelper.TABLE_WEEKLY_TASKS + " WHERE " + MySQLiteHelper.COLUMN_TASK_PLAN_ID + " = ?",
                    new String[]{String.valueOf(planId)}
            );
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private WeekRange getCurrentWeekRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int diffToMonday = (dayOfWeek == Calendar.SUNDAY) ? -6 : (Calendar.MONDAY - dayOfWeek);
        calendar.add(Calendar.DAY_OF_MONTH, diffToMonday);
        String start = dateToString(calendar.getTime());

        Calendar endCalendar = (Calendar) calendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, 6);
        String end = dateToString(endCalendar.getTime());

        return new WeekRange(start, end);
    }

    private String dateToString(java.util.Date date) {
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(date);
        }
    }


    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStatus(String status) {
        String normalized = normalize(status).toUpperCase(Locale.US);
        if (WeeklyTask.STATUS_YES.equals(normalized)) {
            return WeeklyTask.STATUS_YES;
        }
        if (WeeklyTask.STATUS_NO.equals(normalized)) {
            return WeeklyTask.STATUS_NO;
        }
        return WeeklyTask.STATUS_PENDING;
    }

    private static class WeekRange {
        final String start;
        final String end;

        WeekRange(String start, String end) {
            this.start = start;
            this.end = end;
        }
    }
}

