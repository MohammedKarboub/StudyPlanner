package com.Studyplanner.service;

import java.awt.Toolkit;
import java.util.List;

import com.Studyplanner.Model.Settings;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

public class NotificationService {
    
    private static final int NOTIFICATION_DURATION_SECONDS = 5;
    
    public NotificationService() {
        System.out.println("NotificationService initialized with JavaFX notifications");
    }
    
    public void showNotification(String title, String message) {
        Settings settings = DataService.getInstance().getSettings();
        
        if (!settings.isNotificationsEnabled()) {
            return;
        }
        
        // Show JavaFX notification
        Platform.runLater(() -> showJavaFXNotification(title, message));
        
        // Play sound if enabled
        if (settings.isSoundEnabled()) {
            playNotificationSound();
        }
    }
    
    private void showJavaFXNotification(String title, String message) {
        Popup popup = new Popup();
        
        VBox content = new VBox(10);
        content.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-padding: 20px; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white;"
        );
        
        Label messageLabel = new Label(message);
        messageLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: white;"
        );
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        
        content.getChildren().addAll(titleLabel, messageLabel);
        content.setAlignment(Pos.CENTER_LEFT);
        popup.getContent().add(content);
        
        Platform.runLater(() -> {
            List<Window> windows = Window.getWindows();
            if (!windows.isEmpty()) {
                Stage stage = (Stage) windows.get(0);
                
                // Position at top-right of screen
                double x = stage.getX() + stage.getWidth() - 350;
                double y = stage.getY() + 50;
                
                popup.show(stage, x, y);
                
                // Auto-hide after duration
                PauseTransition delay = new PauseTransition(Duration.seconds(NOTIFICATION_DURATION_SECONDS));
                delay.setOnFinished(e -> popup.hide());
                delay.play();
            }
        });
    }
    
    private void playNotificationSound() {
        new Thread(() -> {
            try {
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception e) {
                System.err.println("Could not play notification sound");
            }
        }).start();
    }
}