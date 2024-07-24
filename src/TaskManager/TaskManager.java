package TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    final private HashMap<Integer, Task> tasks;
    final private HashMap<Integer, Epic> epics;
    final private HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        epic.addSubtask(subtask.getId(), subtask);
        subtasks.put(subtask.getId(), subtask);
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    //ни одно значение эпика менять нельзя
    private void updateEpic(Epic epic) {
        HashMap<Integer, Subtask> SubtasksOfEpic = epic.getSubtasks();
        boolean haveNew = false;
        boolean haveInProgress = false;
        boolean haveDone = false;
        Status statusOfEpic;

        for (Subtask subtaskOfEpic : SubtasksOfEpic.values()) {
            Status statusOfSubtask = subtaskOfEpic.getStatus();

            if (statusOfSubtask == Status.NEW) {
                haveNew = true;
            } else if (statusOfSubtask == Status.IN_PROGRESS) {
                haveInProgress = true;
            } else if (statusOfSubtask == Status.DONE) {
                haveDone = true;
            }
        }

        if (!haveInProgress  && !haveDone) {
            statusOfEpic = Status.NEW;
        } else if (!haveNew && !haveInProgress && haveDone) {
            statusOfEpic = Status.DONE;
        } else {
            statusOfEpic = Status.IN_PROGRESS;
        }

        epic.setStatus(statusOfEpic);
    }

    public void updateSubTask(Subtask subtask) {
        Subtask thisSubtask = subtasks.get(subtask.getId());

        if (thisSubtask.getEpicId() != subtask.getEpicId()) {
            return;
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        subtasks.put(subtask.getId(), subtask);
        updateEpic(epic);
    }

    public void removeTaskById(int taskId) {
        tasks.remove(taskId);
    }

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

    public void removeSubtaskById(int subtaskId) {
        subtasks.remove(subtaskId);

        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().containsKey(subtaskId)) {
                epic.removeSubtask(subtaskId);
                return;
            }
        }
    }

    public HashMap<Integer, Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }
}

