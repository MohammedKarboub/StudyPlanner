package com.Studyplanner.View;

import java.time.LocalDate;

import com.Studyplanner.Model.Task;
import com.Studyplanner.service.DataService;
import com.Studyplanner.util.TimeFormatter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TaskListView {

    private final VBox root;
    private final ListView<Task> taskListView;
    private final DataService dataService;
    private final ComboBox<String> filterComboBox;

    public TaskListView() {
        this.root = new VBox(15);
        this.dataService = DataService.getInstance();
        this.taskListView = new ListView<>();
        this.filterComboBox = new ComboBox<>();

        setupUI();
        loadTasks();
    }

    private void setupUI() {
        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("📋 Mijn Taken");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Nieuwe Taak");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setOnAction(e -> showAddTaskDialog());

        header.getChildren().addAll(titleLabel, spacer, addBtn);

        // Filter controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label filterLabel = new Label("Filter:");
        filterComboBox.getItems().addAll("Alle taken", "Actief", "Voltooid", "Vandaag", "Deze week");
        filterComboBox.setValue("Actief");
        filterComboBox.setOnAction(e -> filterTasks());

        filterBox.getChildren().addAll(filterLabel, filterComboBox);

        // Task list
        taskListView.setCellFactory(lv -> new TaskCell());
        taskListView.setPlaceholder(new Label("Geen taken. Voeg je eerste taak toe! 🎯"));
        VBox.setVgrow(taskListView, Priority.ALWAYS);

        root.getChildren().addAll(header, filterBox, taskListView);
    }

    private void loadTasks() {
        taskListView.setItems(dataService.getTasks());
        filterTasks();
    }

    private void filterTasks() {
        String filter = filterComboBox.getValue();

        taskListView.setItems(
                dataService.getTasks().filtered(task -> {
                    switch (filter) {
                        case "Actief":
                            return !task.isCompleted();
                        case "Voltooid":
                            return task.isCompleted();
                        case "Vandaag":
                            return task.getDeadline() != null &&
                                    task.getDeadline().equals(LocalDate.now());
                        case "Deze week":
                            return task.getDeadline() != null &&
                                    task.getDaysUntilDeadline() <= 7 &&
                                    task.getDaysUntilDeadline() >= 0;
                        default:
                            return true;
                    }
                })
        );
    }

    private void showAddTaskDialog() {
        TaskDialog dialog = new TaskDialog(null);
        dialog.showAndWait().ifPresent(task -> {
            dataService.addTask(task);
            filterTasks();
        });
    }

    public VBox getRoot() {
        return root;
    }

    // Custom cell for task display
    private class TaskCell extends ListCell<Task> {
        private final HBox container;
        private final CheckBox checkBox;
        private final VBox contentBox;
        private final Label titleLabel;
        private final Label detailsLabel;
        private final Label priorityLabel;
        private final Button editBtn;
        private final Button deleteBtn;

        public TaskCell() {
            container = new HBox(15);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(10));

            checkBox = new CheckBox();
            checkBox.setOnAction(e -> {
                Task task = getItem();
                if (task != null) {
                    task.setCompleted(checkBox.isSelected());
                    dataService.updateTask(task);
                    updateItem(task, false);
                }
            });

            contentBox = new VBox(5);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            titleLabel = new Label();
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            detailsLabel = new Label();
            detailsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

            contentBox.getChildren().addAll(titleLabel, detailsLabel);

            priorityLabel = new Label();
            priorityLabel.setStyle(
                    "-fx-padding: 5px 10px; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-font-size: 11px; " +
                            "-fx-text-fill: white;"
            );

            editBtn = new Button("✏️");
            editBtn.getStyleClass().add("icon-button");
            editBtn.setOnAction(e -> editTask(getItem()));

            deleteBtn = new Button("🗑️");
            deleteBtn.getStyleClass().add("icon-button");
            deleteBtn.setOnAction(e -> deleteTask(getItem()));

            container.getChildren().addAll(
                    checkBox, contentBox, priorityLabel, editBtn, deleteBtn
            );
        }

        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);

            if (empty || task == null) {
                setGraphic(null);
            } else {
                checkBox.setSelected(task.isCompleted());
                titleLabel.setText(task.getTitle());

                String details = "";
                if (task.getDeadline() != null) {
                    long days = task.getDaysUntilDeadline();
                    if (days < 0) {
                        details += "⚠️ " + Math.abs(days) + " dagen over deadline | ";
                    } else if (days == 0) {
                        details += "⏰ Vandaag | ";
                    } else {
                        details += "📅 Over " + days + " dagen | ";
                    }
                }
                details += "⏱️ " + TimeFormatter.formatMinutes(task.getEstimatedMinutes());

                detailsLabel.setText(details);

                priorityLabel.setText(task.getPriority().getDisplayName());
                priorityLabel.setStyle(
                        priorityLabel.getStyle() +
                                "-fx-background-color: " + task.getPriority().getColor() + ";"
                );

                if (task.isCompleted()) {
                    titleLabel.setStyle(titleLabel.getStyle() + "-fx-strikethrough: true; -fx-text-fill: #999;");
                    detailsLabel.setStyle(detailsLabel.getStyle() + "-fx-text-fill: #999;");
                }

                setGraphic(container);
            }
        }

        private void editTask(Task task) {
            if (task != null) {
                TaskDialog dialog = new TaskDialog(task);
                dialog.showAndWait().ifPresent(updatedTask -> {
                    dataService.updateTask(task);
                    filterTasks();
                });
            }
        }

        private void deleteTask(Task task) {
            if (task != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Taak verwijderen");
                alert.setHeaderText("Weet je zeker dat je deze taak wilt verwijderen?");
                alert.setContentText(task.getTitle());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        dataService.removeTask(task);
                        filterTasks();
                    }
                });
            }
        }
    }
}