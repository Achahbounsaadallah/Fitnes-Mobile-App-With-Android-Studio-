package com.example.projectmodule.data.model;

import java.util.ArrayList;
import java.util.List;

public class WeeklyPlan {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CLOSED = "CLOSED";

    private final long id;
    private final long userId;
    private final String weekStart;
    private final String weekEnd;
    private String status;
    private final List<WeeklyTask> tasks;

    public WeeklyPlan(long id, long userId, String weekStart, String weekEnd, String status) {
        this.id = id;
        this.userId = userId;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.status = status;
        this.tasks = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getWeekStart() {
        return weekStart;
    }

    public String getWeekEnd() {
        return weekEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<WeeklyTask> getTasks() {
        return tasks;
    }

    public void addTask(WeeklyTask task) {
        tasks.add(task);
    }

    public boolean isClosed() {
        return STATUS_CLOSED.equalsIgnoreCase(status);
    }
}

