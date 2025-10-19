package com.Studyplanner.Model;

import java.time.LocalDateTime;

public class PomodoroSession {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int workMinutes;
    private int breakMinutes;
    private String taskId;
    private boolean completed;

    public PomodoroSession(int workMinutes, int breakMinutes, String taskId) {
        this.startTime = LocalDateTime.now();
        this.workMinutes = workMinutes;
        this.breakMinutes = breakMinutes;
        this.taskId = taskId;
        this.completed = false;
    }

    public void complete() {
        this.endTime = LocalDateTime.now();
        this.completed = true;
    }

    public int getTotalMinutes() {
        return workMinutes + breakMinutes;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public int getWorkMinutes() { return workMinutes; }
    public void setWorkMinutes(int workMinutes) { this.workMinutes = workMinutes; }

    public int getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}