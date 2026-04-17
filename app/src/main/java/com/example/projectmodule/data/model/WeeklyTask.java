package com.example.projectmodule.data.model;

public class WeeklyTask {

    public static final String STATUS_YES = "YES";
    public static final String STATUS_NO = "NO";
    public static final String STATUS_PENDING = "PENDING";

    private final long id;
    private final long planId;
    private final String title;
    private String status;
    private final int orderIndex;

    public WeeklyTask(long id, long planId, String title, String status, int orderIndex) {
        this.id = id;
        this.planId = planId;
        this.title = title;
        this.status = status;
        this.orderIndex = orderIndex;
    }

    public long getId() {
        return id;
    }

    public long getPlanId() {
        return planId;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public boolean isYes() {
        return STATUS_YES.equalsIgnoreCase(status);
    }

    public boolean isNo() {
        return STATUS_NO.equalsIgnoreCase(status);
    }

    public boolean isPending() {
        return status == null || status.trim().isEmpty() || STATUS_PENDING.equalsIgnoreCase(status);
    }
}

