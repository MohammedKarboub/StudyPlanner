package com.Studyplanner.util;

public class TimeFormatter {

    public static String formatSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public static String formatMinutes(int minutes) {
        if (minutes < 60) {
            return minutes + " min";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            if (remainingMinutes == 0) {
                return hours + " uur";
            }
            return hours + " uur " + remainingMinutes + " min";
        }
    }

    public static String formatTimeAgo(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Nooit";

        java.time.Duration duration = java.time.Duration.between(dateTime, java.time.LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return "Zojuist";
        if (seconds < 3600) return (seconds / 60) + " minuten geleden";
        if (seconds < 86400) return (seconds / 3600) + " uur geleden";

        long days = duration.toDays();
        if (days == 1) return "Gisteren";
        if (days < 7) return days + " dagen geleden";
        if (days < 30) return (days / 7) + " weken geleden";

        return dateTime.toLocalDate().toString();
    }
}