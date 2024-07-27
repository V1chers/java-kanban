package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    final private Map<Integer, Task> history;
    private int lastInHistory;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        lastInHistory = 10;
    }

    public void add(Task task) {
        if (lastInHistory < 9) {
            lastInHistory++;
        } else {
            lastInHistory = 0;
        }

        history.put(lastInHistory, task);
    }

    public List<Task> getHistory() {
        return new ArrayList<>(history.values());
    }
}
