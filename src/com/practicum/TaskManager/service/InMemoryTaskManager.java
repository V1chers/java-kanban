package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.Epic;
import com.practicum.TaskManager.model.Status;
import com.practicum.TaskManager.model.Subtask;
import com.practicum.TaskManager.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final TreeSet<Task> sortedTasks;
    private final HistoryManager history;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        sortedTasks = new TreeSet<>((Task o1, Task o2) -> {
            if (o1.equals(o2)) {
                return 0;
            }
            return o1.getStartTime().isAfter(o2.getStartTime()) ? 1 : -1;
        });
    }

    @Override
    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        if (isTasksOverlap(task)) {
            return;
        }

        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
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
        if (isTasksOverlap(subtask)) {
            return;
        }
        if (!epics.containsKey(subtask.getEpicId())) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        updatePrioritizedTasks(subtask);
    }

    @Override
    public List<Epic> getEpics() {
        return Collections.unmodifiableList(new ArrayList<>(epics.values()));
    }

    @Override
    public List<Task> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks.values()));
    }

    @Override
    public List<Subtask> getSubtasks() {
        return Collections.unmodifiableList(new ArrayList<>(subtasks.values()));
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(history::remove);
        tasks.values().forEach(this::deletePrioritizedTasks);

        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(history::remove);
        subtasks.keySet().forEach(history::remove);

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(history::remove);
        subtasks.values().forEach(this::deletePrioritizedTasks);

        subtasks.clear();

        epics.values().forEach(Epic::removeAllSubtasks);
        epics.values().forEach(this::updateEpicStatus);
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
        if (isTasksOverlap(task)) {
            return;
        }

        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksOfEpic = epic.getSubtasks();
        boolean haveNew = false;
        boolean haveInProgress = false;
        boolean haveDone = false;
        Status statusOfEpic;

        for (Subtask subtaskOfEpic : subtasksOfEpic) {
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

        epic.getSubtasks().forEach(subtask -> subtasks.put(subtask.getId(), subtask));

        ArrayList<Integer> subtasksToRemove = new ArrayList<>();

        subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epic.getId() && !epic.getSubtasks().contains(subtask))
                .forEach(subtask -> subtasksToRemove.add(subtask.getId()));

        subtasksToRemove.forEach(history::remove);
        subtasksToRemove.forEach(subtasks::remove);

        updateEpicStatus(epic);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        if (isTasksOverlap(subtask)) {
            return;
        }

        Subtask thisSubtask = subtasks.get(subtask.getId());

        if (thisSubtask.getEpicId() != subtask.getEpicId()) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        updatePrioritizedTasks(subtask);
    }

    @Override
    public void removeTaskById(int taskId) {
        history.remove(taskId);
        deletePrioritizedTasks(tasks.get(taskId));
        tasks.remove(taskId);
    }

    @Override
    public void removeEpicById(int epicId) {
        history.remove(epicId);
        epics.remove(epicId);
        ArrayList<Integer> subtasksToRemove = new ArrayList<>();


        subtasks.values().stream().filter(subtask -> subtask.getEpicId() == epicId)
                .forEach(subtask -> subtasksToRemove.add(subtask.getId()));

        subtasksToRemove.forEach(history::remove);
        subtasksToRemove.forEach(subtasks::remove);
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        Subtask subtask = getSubtaskById(subtaskId);

        history.remove(subtaskId);
        deletePrioritizedTasks(subtask);
        subtasks.remove(subtaskId);

        epics.values().stream()
                .filter(epic -> epic.getSubtasks().contains(subtask))
                .forEach(epic -> {
                    epic.removeSubtask(subtaskId);
                    updateEpicStatus(epic);
                });
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);

        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.copyOf(sortedTasks);
    }

    private void updatePrioritizedTasks(Task task) {
        if (task.getEndTime().isEmpty()) {
            Optional<Task> oldTask = sortedTasks.stream().filter(task2 -> task.getId() == task2.getId()).findFirst();
            if (oldTask.isPresent()) {
                sortedTasks.remove(oldTask.get());
            }
            return;
        }

        sortedTasks.remove(task);
        sortedTasks.add(task);
    }

    private void deletePrioritizedTasks(Task task) {
        if (task.getEndTime().isEmpty()) {
            return;
        }

        sortedTasks.remove(task);
    }

    private boolean isTasksOverlap(Task task) {
        if (task.getEndTime().isEmpty()) {
            return false;
        }

        return sortedTasks.stream()
                .anyMatch(existingTask -> !(task.getEndTime().get().isBefore(existingTask.getStartTime()) ||
                        task.getStartTime().isAfter(existingTask.getEndTime().get()) ||
                        // И все равно усложнил до плохо читаемого вида, что бы при соприкосновении границ отрезков
                        // времени задача все равно создавалась
                        task.getEndTime().get().isBefore(existingTask.getStartTime()) ==
                        task.getEndTime().get().isAfter(existingTask.getStartTime()) ||
                        task.getStartTime().isAfter(existingTask.getEndTime().get()) ==
                        task.getStartTime().isBefore(existingTask.getEndTime().get())));
    }
}


