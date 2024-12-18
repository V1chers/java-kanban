package com.practicum.TaskManager.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task;

    @BeforeEach
    void createtask() {
        task = new Task("Поспать", "zzz", Status.IN_PROGRESS);
    }

    @Test
    void getDescription() {
        assertEquals("zzz", task.getDescription());
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task sameTask = new Task("Поспать", "zzz", Status.IN_PROGRESS);
        Task otherTask = new Task("Не спать", "Только не спать", Status.IN_PROGRESS);

        assertEquals(sameTask.getId(), task.getId());
        assertTrue(sameTask.equals(task));
        assertFalse(otherTask.equals(task));
        assertNotEquals(otherTask.getId(), task.getId());
    }

    @Test
    void getName() {
        assertEquals("Поспать", task.getName());
    }

    @Test
    void getStatus() {
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void setStatus() {
    }

    @Test
    void getEndTime() {
        LocalDateTime ldt = LocalDateTime.now();
        assertEquals(Optional.empty(), task.getEndTime());
        task.setStartTime(ldt);
        assertEquals(Optional.empty(), task.getEndTime());
        task.setDuration(Duration.ofMinutes(60));
        assertEquals(ldt.plusHours(1), task.getEndTime().get());
        task.setStartTime(null);
        assertEquals(Optional.empty(), task.getEndTime());
    }
}