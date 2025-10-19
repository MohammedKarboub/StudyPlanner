package com.Studyplanner.View;

import com.Studyplanner.service.DataService;
import com.Studyplanner.service.PomodoroTimerService;
import com.Studyplanner.service.PomodoroTimerService.TimerState;
import com.Studyplanner.util.TimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PomodoroView {

    private final VBox root;
    private final PomodoroTimerService timerService;
    private final Label timeLabel;
    private final Label statusLabel;
    private final ProgressBar progressBar;
    private final Button startWorkBtn;
    private final Button startBreakBtn;
    private final Button pauseBtn;
    private final Button resetBtn;
    private final Label pomodoroCountLabel;
    private final ComboBox<String> taskSelector;

    public PomodoroView() {
        this.root = new VBox(20);
        this.timerService = new PomodoroTimerService();
        this.timeLabel = new Label("25:00");
        this.statusLabel = new Label("Klaar om te beginnen");
        this.progressBar = new ProgressBar(0);
        this.startWorkBtn = new Button("▶️ Start Focustijd");
        this.startBreakBtn = new Button("☕ Start Pauze");
        this.pauseBtn = new Button("⏸️ Pauzeer");
        this.resetBtn = new Button("🔄 Reset");
        this.pomodoroCountLabel = new Label("Vandaag: 0 pomodoro's");
        this.taskSelector = new ComboBox<>();

        setupUI();
        bindProperties();
        updateTodayCount();
    }

    private void setupUI() {
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10px;");

        // Header
        Label titleLabel = new Label("⏱️ Pomodoro Timer");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Task selector
        VBox selectorBox = new VBox(5);
        Label selectorLabel = new Label("Selecteer taak:");
        taskSelector.setMaxWidth(Double.MAX_VALUE);
        taskSelector.setPromptText("Kies een taak of werk algemeen");

        // Populate task selector
        taskSelector.getItems().add("Algemeen");
        DataService.getInstance().getTasks().forEach(task -> {
            if (!task.isCompleted()) {
                taskSelector.getItems().add(task.getTitle());
            }
        });
        taskSelector.setValue("Algemeen");

        selectorBox.getChildren().addAll(selectorLabel, taskSelector);

        // Timer display
        VBox timerBox = new VBox(10);
        timerBox.setAlignment(Pos.CENTER);
        timerBox.setPadding(new Insets(20));
        timerBox.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        timeLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(10);
        progressBar.setStyle("-fx-accent: #1976D2;");

        pomodoroCountLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        timerBox.getChildren().addAll(timeLabel, statusLabel, progressBar, pomodoroCountLabel);

        // Control buttons
        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER);

        startWorkBtn.getStyleClass().add("primary-button");
        startWorkBtn.setPrefWidth(150);
        startWorkBtn.setOnAction(e -> startWork());

        startBreakBtn.getStyleClass().add("secondary-button");
        startBreakBtn.setPrefWidth(150);
        startBreakBtn.setOnAction(e -> startBreak());

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startWorkBtn, startBreakBtn);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);

        pauseBtn.getStyleClass().add("secondary-button");
        pauseBtn.setOnAction(e -> togglePause());
        pauseBtn.setDisable(true);

        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setOnAction(e -> reset());

        actionBox.getChildren().addAll(pauseBtn, resetBtn);

        // Info section
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(20));
        infoBox.setStyle(
                "-fx-background-color: #E3F2FD; " +
                        "-fx-background-radius: 10px;"
        );

        Label infoTitle = new Label("💡 Pomodoro Techniek");
        infoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label infoText = new Label(
                "1. Kies een taak\n" +
                        "2. Werk gefocust gedurende de ingestelde tijd\n" +
                        "3. Neem een korte pauze\n" +
                        "4. Herhaal en bereik je doelen!"
        );
        infoText.setStyle("-fx-font-size: 12px;");
        infoText.setWrapText(true);

        infoBox.getChildren().addAll(infoTitle, infoText);

        root.getChildren().addAll(
                titleLabel,
                selectorBox,
                timerBox,
                buttonBox,
                actionBox,
                infoBox
        );
    }

    private void bindProperties() {
        timerService.secondsRemainingProperty().addListener((obs, oldVal, newVal) -> {
            timeLabel.setText(TimeFormatter.formatSeconds(newVal.intValue()));
            progressBar.setProgress(timerService.getProgress());
        });

        timerService.stateProperty().addListener((obs, oldVal, newVal) -> {
            updateUIForState(newVal);
        });

        timerService.completedPomodorosProperty().addListener((obs, oldVal, newVal) -> {
            updateTodayCount();
        });
    }

    private void updateUIForState(TimerState state) {
        switch (state) {
            case IDLE -> {
                statusLabel.setText("Klaar om te beginnen");
                startWorkBtn.setDisable(false);
                startBreakBtn.setDisable(false);
                pauseBtn.setDisable(true);
                pauseBtn.setText("⏸️ Pauzeer");
            }

            case WORKING -> {
                statusLabel.setText("🎯 Focus tijd - blijf geconcentreerd!");
                startWorkBtn.setDisable(true);
                startBreakBtn.setDisable(true);
                pauseBtn.setDisable(false);
            }

            case BREAK -> {
                statusLabel.setText("☕ Pauze - neem even rust");
                startWorkBtn.setDisable(true);
                startBreakBtn.setDisable(true);
                pauseBtn.setDisable(false);
            }

            case PAUSED -> {
                statusLabel.setText("⏸️ Gepauzeerd");
                pauseBtn.setText("▶️ Hervat");
            }
        }
    }

    private void startWork() {
        String selectedTask = taskSelector.getValue();
        String taskId = selectedTask != null && !selectedTask.equals("Algemeen") ? selectedTask : null;
        timerService.startWork(taskId);
    }

    private void startBreak() {
        timerService.startBreak();
    }

    private void togglePause() {
        if (timerService.getState() == TimerState.PAUSED) {
            timerService.resume();
            pauseBtn.setText("⏸️ Pauzeer");
        } else {
            timerService.pause();
            pauseBtn.setText("▶️ Hervat");
        }
    }

    private void reset() {
        timerService.reset();
        timeLabel.setText(TimeFormatter.formatSeconds(
                DataService.getInstance().getSettings().getWorkDurationMinutes() * 60
        ));
        progressBar.setProgress(0);
    }

    private void updateTodayCount() {
        int todayCount = DataService.getInstance().getTodaySessions().size();
        pomodoroCountLabel.setText("Vandaag: " + todayCount + " pomodoro's 🍅");
    }

    public VBox getRoot() {
        return root;
    }
}