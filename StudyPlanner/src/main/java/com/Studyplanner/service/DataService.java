package com.Studyplanner.service;

import com.Studyplanner.Model.PomodoroSession;
import com.Studyplanner.Model.Settings;
import com.Studyplanner.Model.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.Studyplanner.util.LocalDateAdapter;
import com.Studyplanner.util.LocalDateTimeAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataService {

    private static DataService instance;

    private final ObservableList<Task> tasks;
    private final ObservableList<PomodoroSession> sessions;
    private Settings settings;

    private final Gson gson;
    private static final String DATA_DIR = System.getProperty("user.home") + File.separator + ".studyplanner";
    private static final String TASKS_FILE = DATA_DIR + File.separator + "tasks.json";
    private static final String SESSIONS_FILE = DATA_DIR + File.separator + "sessions.json";
    private static final String SETTINGS_FILE = DATA_DIR + File.separator + "settings.json";

    private DataService() {
        this.tasks = FXCollections.observableArrayList();
        this.sessions = FXCollections.observableArrayList();
        this.settings = new Settings();

        // Configure Gson with custom adapters for LocalDate and LocalDateTime
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();

        // Create data directory if it doesn't exist
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    // Task operations
    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveData();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        saveData();
    }

    public void updateTask(Task task) {
        saveData();
    }

    public List<Task> getActiveTasks() {
        return tasks.stream()
                .filter(t -> !t.isCompleted())
                .sorted((t1, t2) -> Integer.compare(t2.getUrgencyScore(), t1.getUrgencyScore()))
                .toList();
    }

    // Session operations
    public ObservableList<PomodoroSession> getSessions() {
        return sessions;
    }

    public void addSession(PomodoroSession session) {
        sessions.add(session);
        saveData();
    }

    public List<PomodoroSession> getTodaySessions() {
        LocalDate today = LocalDate.now();
        return sessions.stream()
                .filter(s -> s.getStartTime().toLocalDate().equals(today))
                .toList();
    }

    // Settings operations
    public Settings getSettings() {
        return settings;
    }

    public void updateSettings(Settings newSettings) {
        this.settings = newSettings;
        saveData();
    }

    // Data persistence
    public void loadData() {
        loadTasks();
        loadSessions();
        loadSettings();
    }

    public void saveData() {
        saveTasks();
        saveSessions();
        saveSettings();
    }

    private void loadTasks() {
        File file = new File(TASKS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
                List<Task> loadedTasks = gson.fromJson(reader, listType);
                if (loadedTasks != null) {
                    tasks.clear();
                    tasks.addAll(loadedTasks);
                }
            } catch (IOException e) {
                System.err.println("Error loading tasks: " + e.getMessage());
            }
        }
    }

    private void saveTasks() {
        try (FileWriter writer = new FileWriter(TASKS_FILE)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    private void loadSessions() {
        File file = new File(SESSIONS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<ArrayList<PomodoroSession>>(){}.getType();
                List<PomodoroSession> loadedSessions = gson.fromJson(reader, listType);
                if (loadedSessions != null) {
                    sessions.clear();
                    sessions.addAll(loadedSessions);
                }
            } catch (IOException e) {
                System.err.println("Error loading sessions: " + e.getMessage());
            }
        }
    }

    private void saveSessions() {
        try (FileWriter writer = new FileWriter(SESSIONS_FILE)) {
            gson.toJson(sessions, writer);
        } catch (IOException e) {
            System.err.println("Error saving sessions: " + e.getMessage());
        }
    }

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Settings loadedSettings = gson.fromJson(reader, Settings.class);
                if (loadedSettings != null) {
                    this.settings = loadedSettings;
                }
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
            }
        }
    }

    private void saveSettings() {
        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            gson.toJson(settings, writer);
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
}