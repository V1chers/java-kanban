package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    final private Map<Integer, Task> tasks;
    final private Map<Integer, Epic> epics;
    final private Map<Integer, Subtask> subtasks;
    final private HistoryManager history;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();

    }

    @Override
    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        history.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        history.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        history.add(subtask);
        return subtask;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void updateEpicStatus(Epic epic) {
        ArrayList<Subtask> SubtasksOfEpic = epic.getSubtasks();
        boolean haveNew = false;
        boolean haveInProgress = false;
        boolean haveDone = false;
        Status statusOfEpic;

        for (Subtask subtaskOfEpic : SubtasksOfEpic) {
            Status statusOfSubtask = subtaskOfEpic.getStatus();

            if (statusOfSubtask == Status.NEW) {
                haveNew = true;
            } else if (statusOfSubtask == Status.IN_PROGRESS) {
                haveInProgress = true;
            } else if (statusOfSubtask == Status.DONE) {
                haveDone = true;
            }
        }

        if (!haveInProgress && !haveDone) {
            statusOfEpic = Status.NEW;
        } else if (!haveNew && !haveInProgress && haveDone) {
            statusOfEpic = Status.DONE;
        } else {
            statusOfEpic = Status.IN_PROGRESS;
        }

        epic.setStatus(statusOfEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);

        for (Subtask subtasksOfEpic : epic.getSubtasks()) {
            subtasks.put(subtasksOfEpic.getId(), subtasksOfEpic);
        }

        ArrayList<Integer> subtasksToRemove = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epic.getId() && !epic.getSubtasks().contains(subtask)) {
                subtasksToRemove.add(subtask.getId());
            }
        }

        for (int subtaskToRemove : subtasksToRemove) {
            subtasks.remove(subtaskToRemove);
        }

        updateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        Subtask thisSubtask = subtasks.get(subtask.getId());

        if (thisSubtask.getEpicId() != subtask.getEpicId()) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        epics.remove(epicId);
        ArrayList<Integer> subtasksToRemove = new ArrayList<>();

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksToRemove.add(subtask.getId());
            }
        }

        for (int subtaskToRemove : subtasksToRemove) {
            subtasks.remove(subtaskToRemove);
        }
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = getSubtaskById(subtaskId);

        subtasks.remove(subtaskId);

        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().contains(subtask)) {
                epic.removeSubtask(subtaskId);
                updateEpicStatus(epic);
                return;
            }
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);

        return epic.getSubtasks();
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }
}


