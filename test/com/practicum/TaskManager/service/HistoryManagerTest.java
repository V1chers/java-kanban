package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;

    @Test
    void addAndGetHistory() {
        historyManager = Managers.getDefaultHistory();
        Task task = new Task("Random name", "...", Status.DONE);
        Epic epic = new Epic("Random name2", "...");
        Subtask subtask = new Subtask("Random name3", "...", Status.DONE, 123);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertTrue(history.contains(epic));
    }

    @Test
    void shouldBeNoMore10Objects() {
        historyManager = Managers.getDefaultHistory();

        for (int i = 0; i < 20; i++) {
            Task task = new Task("Random name" + i, "...", Status.DONE);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size());
    }
}