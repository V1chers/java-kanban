package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;

import java.util.List;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    List<Epic> getEpics();

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(Subtask subtask);

    void removeTaskById(int taskId);

    void removeEpicById(int epicId);

    void removeSubtaskById(int subtaskId);

    List<Subtask> getSubtasksOfEpic(int epicId);

    List<Task> getHistory();
}
