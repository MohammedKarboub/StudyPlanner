package com.Studyplanner.service;

import com.Studyplanner.Model.PomodoroSession;
import com.Studyplanner.Model.Settings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.util.Duration;

public class PomodoroTimerService {

    public enum TimerState {
        IDLE, WORKING, BREAK, PAUSED
    }

    private final IntegerProperty secondsRemaining;
    private final ObjectProperty<TimerState> state;
    private final IntegerProperty completedPomodoros;

    private Timeline timeline;
    private int workSeconds;
    private int breakSeconds;
    private int totalSeconds;
    private String currentTaskId;
    private PomodoroSession currentSession;

    private final NotificationService notificationService;

    public PomodoroTimerService() {
        this.secondsRemaining = new SimpleIntegerProperty(0);
        this.state = new SimpleObjectProperty<>(TimerState.IDLE);
        this.completedPomodoros = new SimpleIntegerProperty(0);
        this.notificationService = new NotificationService();

        // Default durations from settings
        Settings settings = DataService.getInstance().getSettings();
        this.workSeconds = settings.getWorkDurationMinutes() * 60;
        this.breakSeconds = settings.getBreakDurationMinutes() * 60;
    }

    public void startWork(String taskId) {
        if (state.get() != TimerState.IDLE && state.get() != TimerState.PAUSED) {
            return;
        }

        Settings settings = DataService.getInstance().getSettings();
        this.workSeconds = settings.getWorkDurationMinutes() * 60;
        this.breakSeconds = settings.getBreakDurationMinutes() * 60;
        this.currentTaskId = taskId;

        if (state.get() == TimerState.IDLE) {
            this.totalSeconds = workSeconds;
            this.secondsRemaining.set(workSeconds);
            this.currentSession = new PomodoroSession(
                    settings.getWorkDurationMinutes(),
                    settings.getBreakDurationMinutes(),
                    taskId
            );
        }

        state.set(TimerState.WORKING);
        notificationService.showNotification(
                "Focus tijd gestart! 🎯",
                "Werk gedurende " + settings.getWorkDurationMinutes() + " minuten zonder afleiding."
        );

        startTimer();
    }

    public void startBreak() {
        if (state.get() != TimerState.IDLE && state.get() != TimerState.PAUSED) {
            return;
        }

        if (state.get() == TimerState.IDLE) {
            this.totalSeconds = breakSeconds;
            this.secondsRemaining.set(breakSeconds);
        }

        state.set(TimerState.BREAK);
        notificationService.showNotification(
                "Pauze tijd! ☕",
                "Neem " + DataService.getInstance().getSettings().getBreakDurationMinutes() + " minuten rust."
        );

        startTimer();
    }

    private void startTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int current = secondsRemaining.get();

            if (current > 0) {
                secondsRemaining.set(current - 1);
            } else {
                onTimerComplete();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void onTimerComplete() {
        timeline.stop();

        if (state.get() == TimerState.WORKING) {
            completedPomodoros.set(completedPomodoros.get() + 1);

            notificationService.showNotification(
                    "Geweldig gedaan! 🎉",
                    "Je hebt een focus sessie voltooid. Tijd voor een pauze!"
            );

            // Save session
            if (currentSession != null) {
                currentSession.complete();
                DataService.getInstance().addSession(currentSession);
            }

            state.set(TimerState.IDLE);
            secondsRemaining.set(breakSeconds);

        } else if (state.get() == TimerState.BREAK) {
            notificationService.showNotification(
                    "Pauze afgelopen! 💪",
                    "Klaar om weer aan de slag te gaan?"
            );

            state.set(TimerState.IDLE);
            secondsRemaining.set(workSeconds);
        }
    }

    public void pause() {
        if (timeline != null && (state.get() == TimerState.WORKING || state.get() == TimerState.BREAK)) {
            timeline.pause();
            state.set(TimerState.PAUSED);
        }
    }

    public void resume() {
        if (timeline != null && state.get() == TimerState.PAUSED) {
            timeline.play();
            state.set(secondsRemaining.get() > workSeconds / 2 ? TimerState.WORKING : TimerState.BREAK);
        }
    }

    public void reset() {
        if (timeline != null) {
            timeline.stop();
        }

        Settings settings = DataService.getInstance().getSettings();
        this.workSeconds = settings.getWorkDurationMinutes() * 60;
        this.breakSeconds = settings.getBreakDurationMinutes() * 60;

        secondsRemaining.set(workSeconds);
        state.set(TimerState.IDLE);
        currentSession = null;
    }

    public double getProgress() {
        if (totalSeconds == 0) return 0;
        return 1.0 - ((double) secondsRemaining.get() / totalSeconds);
    }

    // Properties
    public IntegerProperty secondsRemainingProperty() { return secondsRemaining; }
    public int getSecondsRemaining() { return secondsRemaining.get(); }

    public ObjectProperty<TimerState> stateProperty() { return state; }
    public TimerState getState() { return state.get(); }

    public IntegerProperty completedPomodorosProperty() { return completedPomodoros; }
    public int getCompletedPomodoros() { return completedPomodoros.get(); }

    public String getCurrentTaskId() { return currentTaskId; }
}