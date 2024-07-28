package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
