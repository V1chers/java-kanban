package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void createHistory() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Random name", "...", Status.DONE);
        epic = new Epic("Random name2", "...");
        subtask = new Subtask("Random name3", "...", Status.DONE, 123);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
    }

    @Test
    void ShouldGetNullFromEmptyHistory() {
        HistoryManager emptyHistoryManager = Managers.getDefaultHistory();

        assertNull(emptyHistoryManager.getHistory());
    }

    @Test
    void ShoulReplaceSameTask() {
        Task sameTask = new Task("Random name", "...", Status.DONE);
        historyManager.add(sameTask);
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertEquals(sameTask, history.getLast());
    }

    @Test
    void addAndGetHistory() {
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size());
        assertTrue(history.contains(epic));
    }

    @Test
    void remove() {
        historyManager.remove(epic.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertFalse(history.contains(epic));

        historyManager.remove(subtask.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertFalse(history.contains(subtask));

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertNull(history);
    }
}