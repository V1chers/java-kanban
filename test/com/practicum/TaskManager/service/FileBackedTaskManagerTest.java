package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    static Path tempFile;
    static FileBackedTaskManager taskManager;

    @BeforeAll
    static void createFileBackedTaskManager() {
        try {
            tempFile = Files.createTempFile(null, ".txt");
        } catch (java.io.IOException e) {
            System.out.println("ошибка в тесте");
        }

        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void save() {
        Task strollTask = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask);
        Task trashTask = new Task("мусор", "выкинуть мусор", Status.NEW);
        taskManager.createTask(trashTask);
        Epic productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        Subtask milkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(milkSubtask);
        Subtask breadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(breadSubtask);
        Epic pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        Subtask feedSubtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId());
        taskManager.createSubtask(feedSubtask);

        try {
            String[] data = Files.readString(tempFile).split("\n");

            assertEquals(8, data.length);
        } catch (java.io.IOException e) {
            System.out.println("ошибка в тесте");
        }
    }

    @Test
    void loadFromFile() {
        FileBackedTaskManager taskManager2 = new FileBackedTaskManager(tempFile);

        assertEquals(taskManager.getTasks(), taskManager2.getTasks());
        assertEquals(taskManager.getEpics(), taskManager2.getEpics());
        assertEquals(taskManager.getSubtasks(), taskManager2.getSubtasks());
    }
}
