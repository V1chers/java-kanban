package com.practicum.TaskManager.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    Subtask subtask;
    Subtask subtask2;

    @BeforeEach
    void createEpic() {
        epic = new Epic("Random name", "...");
        subtask = new Subtask("Again Random Name", "...", Status.NEW, epic.getId());
        subtask2 = new Subtask("Another Random Name", "...", Status.NEW, epic.getId());
        epic.addSubtask(subtask);
        epic.addSubtask(subtask2);
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtasks = epic.getSubtasks();

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(subtask));
    }

    @Test
    void removeSubtask() {
        epic.removeSubtask(subtask.getId());

        assertEquals(1, epic.getSubtasks().size());
    }

    @Test
    void removeAllSubtasks() {
        epic.removeAllSubtasks();

        assertEquals(0, epic.getSubtasks().size());
    }
}