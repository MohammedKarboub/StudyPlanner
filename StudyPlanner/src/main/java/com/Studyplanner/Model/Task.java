package com.Studyplanner.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private String id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Priority priority;
    private int estimatedMinutes;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String category;

    public enum Priority {
        LOW("Laag", "#4CAF50"),
        MEDIUM("Gemiddeld", "#FF9800"),
        HIGH("Hoog", "#F44336");

        private final String displayName;
        private final String color;

        Priority(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }

    public Task(String title, String description, LocalDate deadline, Priority priority, int estimatedMinutes) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.estimatedMinutes = estimatedMinutes;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.category = "Algemeen";
    }


    public Task() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public int getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(int estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Utility methods
    public long getDaysUntilDeadline() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    public boolean isOverdue() {
        return !completed && deadline != null && deadline.isBefore(LocalDate.now());
    }

    public int getUrgencyScore() {
        int score = 0;

        // Priority score
        switch (priority) {
            case HIGH -> score += 100;
            case MEDIUM -> score += 50;
            case LOW -> score += 25;
        }

        // Deadline score (closer = higher score)
        if (deadline != null) {
            long days = getDaysUntilDeadline();
            if (days < 0) score += 200; // Overdue
            else if (days == 0) score += 150; // Today
            else if (days <= 3) score += 75;
            else if (days <= 7) score += 40;
        }

        return score;
    }

    @Override
    public String toString() {
        return title + " (" + priority.getDisplayName() + ")";
    }
}