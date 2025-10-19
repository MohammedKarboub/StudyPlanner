package com.Studyplanner.View;

import com.Studyplanner.Model.Settings;
import com.Studyplanner.service.DataService;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SettingsDialog extends Dialog<Void> {

    private final Spinner<Integer> workDurationSpinner;
    private final Spinner<Integer> breakDurationSpinner;
    private final CheckBox soundCheckBox;
    private final CheckBox notificationsCheckBox;
    private final Settings settings;

    public SettingsDialog() {
        this.settings = DataService.getInstance().getSettings();

        setTitle("⚙️ Instellingen");
        setHeaderText("Pomodoro Timer Instellingen");

        // Create form fields
        workDurationSpinner = new Spinner<>(1, 120, settings.getWorkDurationMinutes(), 5);
        workDurationSpinner.setEditable(true);
        workDurationSpinner.setPrefWidth(100);

        breakDurationSpinner = new Spinner<>(1, 60, settings.getBreakDurationMinutes(), 5);
        breakDurationSpinner.setEditable(true);
        breakDurationSpinner.setPrefWidth(100);

        soundCheckBox = new CheckBox("Geluidsnotificaties inschakelen");
        soundCheckBox.setSelected(settings.isSoundEnabled());

        notificationsCheckBox = new CheckBox("Schermnotificaties inschakelen");
        notificationsCheckBox.setSelected(settings.isNotificationsEnabled());

        // Create layout
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // Timer settings section
        Label timerLabel = new Label("⏱️ Timer Instellingen");
        timerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane timerGrid = new GridPane();
        timerGrid.setHgap(10);
        timerGrid.setVgap(10);
        timerGrid.setPadding(new Insets(10, 0, 0, 0));

        timerGrid.add(new Label("Werkduur (minuten):"), 0, 0);
        timerGrid.add(workDurationSpinner, 1, 0);

        timerGrid.add(new Label("Pauzeduur (minuten):"), 0, 1);
        timerGrid.add(breakDurationSpinner, 1, 1);

        // Notification settings section
        Label notifLabel = new Label("🔔 Notificatie Instellingen");
        notifLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox notifBox = new VBox(10);
        notifBox.setPadding(new Insets(10, 0, 0, 0));
        notifBox.getChildren().addAll(soundCheckBox, notificationsCheckBox);

        // Info section
        Label infoLabel = new Label(
                "💡 Tip: De standaard Pomodoro techniek gebruikt 25 minuten werk " +
                        "en 5 minuten pauze, maar je kunt dit aanpassen naar je voorkeur."
        );
        infoLabel.setWrapText(true);
        infoLabel.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-text-fill: #666; " +
                        "-fx-padding: 10px; " +
                        "-fx-background-color: #E3F2FD; " +
                        "-fx-background-radius: 5px;"
        );

        content.getChildren().addAll(
                timerLabel, timerGrid,
                notifLabel, notifBox,
                infoLabel
        );

        getDialogPane().setContent(content);

        // Add buttons
        ButtonType saveButtonType = new ButtonType("Opslaan", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Handle save
        Button saveButton = (Button) getDialogPane().lookupButton(saveButtonType);
        saveButton.setOnAction(e -> saveSettings());
    }

    private void saveSettings() {
        settings.setWorkDurationMinutes(workDurationSpinner.getValue());
        settings.setBreakDurationMinutes(breakDurationSpinner.getValue());
        settings.setSoundEnabled(soundCheckBox.isSelected());
        settings.setNotificationsEnabled(notificationsCheckBox.isSelected());

        DataService.getInstance().updateSettings(settings);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instellingen opgeslagen");
        alert.setHeaderText(null);
        alert.setContentText("Je instellingen zijn succesvol opgeslagen!");
        alert.showAndWait();
    }
}