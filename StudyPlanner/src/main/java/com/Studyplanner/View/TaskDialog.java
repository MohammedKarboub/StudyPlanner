package com.Studyplanner.View;

import com.Studyplanner.Model.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;

public class TaskDialog extends Dialog<Task> {

    private final TextField titleField;
    private final TextArea descriptionArea;
    private final DatePicker deadlinePicker;
    private final ComboBox<Task.Priority> priorityComboBox;
    private final Spinner<Integer> estimatedTimeSpinner;
    private final TextField categoryField;
    private final Task existingTask;

    public TaskDialog(Task task) {
        this.existingTask = task;

        setTitle(task == null ? "Nieuwe Taak" : "Taak Bewerken");
        setHeaderText(task == null ? "Voeg een nieuwe taak toe" : "Bewerk taak");

        // Create form fields
        titleField = new TextField();
        titleField.setPromptText("Bijv. Hoofdstuk 5 lezen");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Beschrijving van de taak...");
        descriptionArea.setPrefRowCount(3);

        deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Kies een deadline");
        deadlinePicker.setValue(LocalDate.now().plusDays(7));

        priorityComboBox = new ComboBox<>();
        priorityComboBox.getItems().addAll(Task.Priority.values());
        priorityComboBox.setValue(Task.Priority.MEDIUM);

        estimatedTimeSpinner = new Spinner<>(15, 480, 60, 15);
        estimatedTimeSpinner.setEditable(true);

        categoryField = new TextField();
        categoryField.setPromptText("Bijv. Wiskunde, Geschiedenis...");
        categoryField.setText("Algemeen");

        // If editing, populate fields
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            deadlinePicker.setValue(task.getDeadline());
            priorityComboBox.setValue(task.getPriority());
            estimatedTimeSpinner.getValueFactory().setValue(task.getEstimatedMinutes());
            categoryField.setText(task.getCategory());
        }

        // Create layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titel:"), 0, 0);
        grid.add(titleField, 1, 0);

        grid.add(new Label("Beschrijving:"), 0, 1);
        grid.add(descriptionArea, 1, 1);

        grid.add(new Label("Deadline:"), 0, 2);
        grid.add(deadlinePicker, 1, 2);

        grid.add(new Label("Prioriteit:"), 0, 3);
        grid.add(priorityComboBox, 1, 3);

        grid.add(new Label("Geschatte tijd (min):"), 0, 4);
        grid.add(estimatedTimeSpinner, 1, 4);

        grid.add(new Label("Categorie:"), 0, 5);
        grid.add(categoryField, 1, 5);

        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Opslaan", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Enable/disable save button
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
        });

        // Convert result
        setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Task result = existingTask != null ? existingTask : new Task();
                result.setTitle(titleField.getText().trim());
                result.setDescription(descriptionArea.getText().trim());
                result.setDeadline(deadlinePicker.getValue());
                result.setPriority(priorityComboBox.getValue());
                result.setEstimatedMinutes(estimatedTimeSpinner.getValue());
                result.setCategory(categoryField.getText().trim());
                return result;
            }
            return null;
        });
    }
}