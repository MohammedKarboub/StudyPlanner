package com.Studyplanner.Model;

public class Settings {

    private int workDurationMinutes;
    private int breakDurationMinutes;
    private boolean soundEnabled;
    private boolean notificationsEnabled;
    private String theme;

    public Settings() {
        // Default settings
        this.workDurationMinutes = 25;
        this.breakDurationMinutes = 5;
        this.soundEnabled = true;
        this.notificationsEnabled = true;
        this.theme = "light";
    }

    // Getters and Setters
    public int getWorkDurationMinutes() { return workDurationMinutes; }
    public void setWorkDurationMinutes(int workDurationMinutes) {
        this.workDurationMinutes = Math.max(1, Math.min(120, workDurationMinutes));
    }

    public int getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(int breakDurationMinutes) {
        this.breakDurationMinutes = Math.max(1, Math.min(60, breakDurationMinutes));
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.soundEnabled = soundEnabled; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
}