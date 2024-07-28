package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    TaskManager taskManager;
    Task strollTask;
    Task trashTask;
    Epic productsEpic;
    Subtask milkSubtask;
    Subtask breadSubtask;
    Epic pigeonsEpic;
    Subtask feedSubtask;

    @BeforeEach
    void createTaskManager() {
        taskManager = Managers.getDefault();

        strollTask = new Task("прогулка", "сходить погулять", Status.NEW);
        taskManager.createTask(strollTask);

        trashTask = new Task("мусор", "выкинуть мусор", Status.NEW);
        taskManager.createTask(trashTask);


        productsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        taskManager.createEpic(productsEpic);
        milkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(milkSubtask);
        breadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        taskManager.createSubtask(breadSubtask);

        pigeonsEpic = new Epic("покормить голубей", "покормить голубей купленным в магазине хлебом");
        taskManager.createEpic(pigeonsEpic);
        feedSubtask = new Subtask("раскрошить хлеб", "что бы голуби смогли его съесть",
                Status.NEW, pigeonsEpic.getId());
        taskManager.createSubtask(feedSubtask);
    }

    @Test
    void shouldNotAddSameTasks() {
        Task sameStrollTask = new Task("прогулка", "сходить погулять", Status.NEW);
        Epic sameProductsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        Subtask sameMilkSubtask = new Subtask("купить молоко", "", Status.NEW, productsEpic.getId());

        taskManager.createTask(sameStrollTask);
        taskManager.createEpic(sameProductsEpic);
        taskManager.createSubtask(sameMilkSubtask);

        assertEquals(2, taskManager.getTasks().size());
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(3, taskManager.getSubtasks().size());
    }

    @Test
    void getEpics() {
        List<Epic> epics = taskManager.getEpics();

        assertEquals(2, epics.size());
        assertTrue(epics.contains(productsEpic));
    }

    @Test
    void getTasks() {
        List<Task> tasks = taskManager.getTasks();

        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(strollTask));
    }

    @Test
    void getSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(3, subtasks.size());
        assertTrue(subtasks.contains(breadSubtask));
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void deleteAllEpics() {
        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(0, productsEpic.getSubtasks().size());
    }

    @Test
    void getTaskById() {
        Task mayStrolTask = taskManager.getTaskById(strollTask.getId());

        assertEquals(strollTask, mayStrolTask);
    }

    @Test
    void getEpicById() {
        Epic mayProductsEpic = taskManager.getEpicById(productsEpic.getId());

        assertEquals(productsEpic, mayProductsEpic);
    }

    @Test
    void getSubtaskById() {
        Subtask mayBreadSubtask = taskManager.getSubtaskById(breadSubtask.getId());

        assertEquals(breadSubtask, mayBreadSubtask);
    }

    @Test
    void updateTask() {
        Task newStrollTask = new Task("прогулка", "сходить погулять", Status.DONE);

        taskManager.updateTask(newStrollTask);

        Task strollTask = taskManager.getTaskById(newStrollTask.getId());

        assertEquals(Status.DONE, strollTask.getStatus());
    }

    @Test
    void updateEpic() {
        Epic newProductsEpic = new Epic("купить продукты", "сходить в ближайший магазин во время прогулки");
        Subtask newbreadSubtask = new Subtask("купить хлеб", "", Status.NEW, productsEpic.getId());
        Subtask cheapsSubtask = new Subtask("купить чипсы", "", Status.NEW, productsEpic.getId());

        newProductsEpic.addSubtask(newbreadSubtask);
        newProductsEpic.addSubtask(cheapsSubtask);
        taskManager.updateEpic(newProductsEpic);

        List<Subtask> subtasks = taskManager.getSubtasks();

        assertTrue(subtasks.contains(cheapsSubtask));
        assertFalse(subtasks.contains(milkSubtask));
    }

    @Test
    void updateEpicStatus() {
        milkSubtask.setStatus(Status.DONE);
        taskManager.updateSubTask(milkSubtask);

        assertEquals(Status.IN_PROGRESS, productsEpic.getStatus());

        breadSubtask.setStatus(Status.DONE);
        taskManager.updateSubTask(breadSubtask);

        assertEquals(Status.DONE, productsEpic.getStatus());
    }

    @Test
    void updateSubTask() {
        Subtask newMilkSubtask = new Subtask("купить молоко", "", Status.DONE, 123);
        Subtask newBreadSubtask = new Subtask("купить хлеб", "", Status.DONE, productsEpic.getId());

        taskManager.updateSubTask(newMilkSubtask);
        taskManager.updateSubTask(newBreadSubtask);

        Subtask milkSubtask = taskManager.getSubtaskById(newMilkSubtask.getId());
        Subtask breadSubtask = taskManager.getSubtaskById(newBreadSubtask.getId());

        assertNotEquals(newMilkSubtask.getEpicId(), milkSubtask.getEpicId());
        assertEquals(newBreadSubtask.getStatus(), breadSubtask.getStatus());
    }

    @Test
    void removeTaskById() {
        taskManager.removeTaskById(strollTask.getId());

        List<Task> tasks = taskManager.getTasks();

        assertEquals(1, tasks.size());
        assertFalse(tasks.contains(strollTask));
    }

    @Test
    void removeEpicById() {
        taskManager.removeEpicById(productsEpic.getId());

        List<Epic> epics = taskManager.getEpics();
        List<Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(1, epics.size());
        assertFalse(epics.contains(productsEpic));
        assertEquals(1, subtasks.size());
        assertFalse(subtasks.contains(breadSubtask));
    }

    @Test
    void removeSubtaskById() {
        taskManager.removeSubtaskById(milkSubtask.getId());

        List<Subtask> subtasks = taskManager.getSubtasks();
        List<Subtask> productsSubtasks = productsEpic.getSubtasks();

        assertEquals(2, subtasks.size());
        assertFalse(subtasks.contains(milkSubtask));
        assertEquals(1, productsSubtasks.size());
        assertFalse(productsSubtasks.contains(milkSubtask));
    }
}