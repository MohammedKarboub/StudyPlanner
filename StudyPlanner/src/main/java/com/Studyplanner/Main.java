package com.Studyplanner;

import com.Studyplanner.View.MainView;
import com.Studyplanner.service.DataService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String APP_TITLE = "Smart Study Planner";
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize data service
            DataService dataService = DataService.getInstance();
            dataService.loadData();

            // Create main view
            MainView mainView = new MainView();
            Scene scene = new Scene(mainView.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

            // Load CSS
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            // Configure stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // Save data on close
            primaryStage.setOnCloseRequest(event -> {
                dataService.saveData();
                System.exit(0);
            });

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        DataService.getInstance().saveData();
    }

    public static void main(String[] args) {
        launch(args);
    }
}