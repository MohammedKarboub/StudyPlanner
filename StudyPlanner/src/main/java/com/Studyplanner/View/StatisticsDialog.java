package com.Studyplanner.View;

import com.Studyplanner.Model.PomodoroSession;
import com.Studyplanner.Model.Task;
import com.Studyplanner.service.DataService;
import javafx.geometry.Insets;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsDialog extends Dialog<Void> {

    private final DataService dataService;

    public StatisticsDialog() {
        this.dataService = DataService.getInstance();

        setTitle("📊 Statistieken");
        setHeaderText("Je Studeer Prestaties");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(800);
        content.setPrefHeight(600);

        // Summary cards
        HBox summaryBox = createSummaryCards();

        // Charts
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // Tab 1: Pomodoro sessions over time
        Tab sessionsTab = new Tab("Pomodoro Sessies");
        sessionsTab.setContent(createSessionsChart());

        // Tab 2: Tasks by priority
        Tab tasksTab = new Tab("Taken Overzicht");
        tasksTab.setContent(createTasksChart());

        // Tab 3: Time by category
        Tab categoryTab = new Tab("Tijd per Categorie");
        categoryTab.setContent(createCategoryChart());

        tabPane.getTabs().addAll(sessionsTab, tasksTab, categoryTab);

        content.getChildren().addAll(summaryBox, tabPane);
        getDialogPane().setContent(content);

        // Add close button
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    }

    private HBox createSummaryCards() {
        HBox box = new HBox(15);
        box.setStyle("-fx-alignment: center;");

        // Total tasks
        long totalTasks = dataService.getTasks().size();
        long completedTasks = dataService.getTasks().stream()
                .filter(Task::isCompleted)
                .count();

        VBox tasksCard = createSummaryCard(
                "📋 Taken",
                completedTasks + " / " + totalTasks,
                "Voltooid"
        );

        // Total pomodoros
        int totalPomodoros = dataService.getSessions().size();
        int todayPomodoros = dataService.getTodaySessions().size();

        VBox pomodorosCard = createSummaryCard(
                "🍅 Pomodoro's",
                todayPomodoros + " vandaag",
                totalPomodoros + " totaal"
        );

        // Total study time
        int totalMinutes = dataService.getSessions().stream()
                .mapToInt(PomodoroSession::getWorkMinutes)
                .sum();
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        VBox timeCard = createSummaryCard(
                "⏱️ Studeetijd",
                hours + " uur " + minutes + " min",
                "Totaal gefocust"
        );

        // Active tasks
        long activeTasks = dataService.getTasks().stream()
                .filter(t -> !t.isCompleted())
                .count();

        VBox activeCard = createSummaryCard(
                "🎯 Actief",
                activeTasks + " taken",
                "Te voltooien"
        );

        box.getChildren().addAll(tasksCard, pomodorosCard, timeCard, activeCard);
        return box;
    }

    private VBox createSummaryCard(String title, String value, String subtitle) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-background-radius: 10px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2); " +
                        "-fx-min-width: 150px; " +
                        "-fx-alignment: center;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    private VBox createSessionsChart() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label chartTitle = new Label("Pomodoro Sessies per Dag (Laatste 7 Dagen)");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Datum");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Aantal Sessies");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Pomodoro Activiteit");
        barChart.setLegendVisible(false);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Get last 7 days
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        Map<LocalDate, Long> sessionsByDate = dataService.getSessions().stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStartTime().toLocalDate(),
                        Collectors.counting()
                ));

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long count = sessionsByDate.getOrDefault(date, 0L);
            series.getData().add(new XYChart.Data<>(date.format(formatter), count));
        }

        barChart.getData().add(series);

        container.getChildren().addAll(chartTitle, barChart);
        return container;
    }

    private VBox createTasksChart() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label chartTitle = new Label("Taken Status Overzicht");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Taken Verdeling");
        VBox.setVgrow(pieChart, Priority.ALWAYS);

        long completed = dataService.getTasks().stream()
                .filter(Task::isCompleted)
                .count();
        long active = dataService.getTasks().stream()
                .filter(t -> !t.isCompleted() && !t.isOverdue())
                .count();
        long overdue = dataService.getTasks().stream()
                .filter(Task::isOverdue)
                .count();

        if (completed > 0) {
            PieChart.Data completedData = new PieChart.Data("Voltooid (" + completed + ")", completed);
            pieChart.getData().add(completedData);
        }
        if (active > 0) {
            PieChart.Data activeData = new PieChart.Data("Actief (" + active + ")", active);
            pieChart.getData().add(activeData);
        }
        if (overdue > 0) {
            PieChart.Data overdueData = new PieChart.Data("Over deadline (" + overdue + ")", overdue);
            pieChart.getData().add(overdueData);
        }

        if (pieChart.getData().isEmpty()) {
            Label noDataLabel = new Label("Geen taken gevonden. Voeg je eerste taak toe!");
            noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            container.getChildren().addAll(chartTitle, noDataLabel);
        } else {
            container.getChildren().addAll(chartTitle, pieChart);
        }

        return container;
    }

    private VBox createCategoryChart() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));

        Label chartTitle = new Label("Studietijd per Categorie");
        chartTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Group tasks by category and sum estimated time
        Map<String, Integer> timeByCategory = dataService.getTasks().stream()
                .collect(Collectors.groupingBy(
                        Task::getCategory,
                        Collectors.summingInt(Task::getEstimatedMinutes)
                ));

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Tijd Allocatie");
        VBox.setVgrow(pieChart, Priority.ALWAYS);

        timeByCategory.forEach((category, minutes) -> {
            int hours = minutes / 60;
            String label = category + " (" + hours + "u " + (minutes % 60) + "m)";
            pieChart.getData().add(new PieChart.Data(label, minutes));
        });

        if (pieChart.getData().isEmpty()) {
            Label noDataLabel = new Label("Geen categorieën gevonden.");
            noDataLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            container.getChildren().addAll(chartTitle, noDataLabel);
        } else {
            container.getChildren().addAll(chartTitle, pieChart);
        }

        return container;
    }
}