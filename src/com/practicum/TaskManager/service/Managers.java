package com.practicum.TaskManager.service;

import java.nio.file.Path;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBacked(Path path) {
        return new FileBackedTaskManager(path, true);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
