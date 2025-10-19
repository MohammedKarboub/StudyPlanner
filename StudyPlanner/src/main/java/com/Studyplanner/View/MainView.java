package com.Studyplanner.View;

import com.Studyplanner.service.DataService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainView {

    private final BorderPane root;
    private final TaskListView taskListView;
    private final PomodoroView pomodoroView;
    private final DataService dataService;

    public MainView() {
        this.root = new BorderPane();
        this.dataService = DataService.getInstance();

        // Create components
        this.taskListView = new TaskListView();
        this.pomodoroView = new PomodoroView();

        // Setup layout
        setupTopBar();
        setupMainContent();

        root.getStyleClass().add("main-container");
    }

    private void setupTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #1976D2;");

        Label titleLabel = new Label("📚 Smart Study Planner");
        titleLabel.setStyle(
                "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button settingsBtn = new Button("⚙️ Instellingen");
        settingsBtn.getStyleClass().add("top-bar-button");
        settingsBtn.setOnAction(e -> openSettings());

        Button statsBtn = new Button("📊 Statistieken");
        statsBtn.getStyleClass().add("top-bar-button");
        statsBtn.setOnAction(e -> openStatistics());

        topBar.getChildren().addAll(titleLabel, spacer, statsBtn, settingsBtn);
        root.setTop(topBar);
    }

    private void setupMainContent() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.5);

        // Left side - Task List
        VBox leftSide = new VBox(10);
        leftSide.setPadding(new Insets(20));
        leftSide.getChildren().add(taskListView.getRoot());
        VBox.setVgrow(taskListView.getRoot(), Priority.ALWAYS);

        // Right side - Pomodoro Timer
        VBox rightSide = new VBox(10);
        rightSide.setPadding(new Insets(20));
        rightSide.getChildren().add(pomodoroView.getRoot());
        VBox.setVgrow(pomodoroView.getRoot(), Priority.ALWAYS);

        splitPane.getItems().addAll(leftSide, rightSide);
        root.setCenter(splitPane);
    }

    private void openSettings() {
        SettingsDialog dialog = new SettingsDialog();
        dialog.showAndWait();
    }

    private void openStatistics() {
        StatisticsDialog dialog = new StatisticsDialog();
        dialog.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }
}