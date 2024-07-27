package com.practicum.TaskManager.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
        TaskManager taskManager2 = new InMemoryTaskManager();

        assertEquals(taskManager.getClass(), taskManager2.getClass());
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        HistoryManager historyManager2 = new InMemoryHistoryManager();

        assertEquals(historyManager.getClass(), historyManager2.getClass());
    }
}