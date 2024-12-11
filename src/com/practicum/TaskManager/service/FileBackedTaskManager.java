package com.practicum.TaskManager.service;

import com.practicum.TaskManager.model.*;

import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final Path savedData;

    public FileBackedTaskManager(Path savedData) {
        super();
        this.savedData = savedData;

        loadFromFile(savedData);
    }

    private void loadFromFile(Path file) {
        try {
            String[] data = Files.readString(file).split("\n");

            if (!data[0].equals("type,name,description,status,epicId")) {
                return;
            }

            for (int i = 1; i < data.length; i++) {
                String[] splittedData = data[i].split(",");
                switch (TaskTypes.valueOf(splittedData[0])) {
                    case TaskTypes.TASK:
                        createTask(taskFromString(splittedData));
                        break;
                    case TaskTypes.EPIC:
                        createEpic(epicFromString(splittedData));
                        break;
                    case TaskTypes.SUBTASK:
                        createSubtask(subtaskFromString(splittedData));
                        break;
                    default:
                }

            }
        } catch (java.io.IOException e) {
            try {
                //По аналогии с ошибкой в сохранении
                throw new ManagerLoadException();
            } catch (ManagerLoadException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    private void save() {
        try (Writer FileWriter = new FileWriter(savedData.toString())) {
            FileWriter.write("type,name,description,status,epicId\n");
            for (Task task : getTasks()) {
                FileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                FileWriter.write(epicToString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                FileWriter.write(subtaskToString(subtask) + "\n");
            }
        } catch (java.io.IOException e) {
            //Думаю не совсем понял задание в этом месте
            try {
                throw new ManagerSaveException();
            } catch (ManagerSaveException exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    private String taskToString(Task task) {
        return TaskTypes.TASK + "," + task.getName() + "," + task.getDescription() + "," + task.getStatus() + ",";
    }

    private String epicToString(Epic epic) {
        return TaskTypes.EPIC + "," + epic.getName() + "," + epic.getDescription() + ",,";
    }

    private String subtaskToString(Subtask subtask) {
        return TaskTypes.SUBTASK + "," + subtask.getName() + "," + subtask.getDescription() + ","
                + subtask.getStatus() + "," + subtask.getEpicId();
    }

    private Task taskFromString(String[] data) {
        return new Task(data[1], data[2], Status.valueOf(data[3]));
    }

    private Epic epicFromString(String[] data) {
        return new Epic(data[1], data[2]);
    }

    private Subtask subtaskFromString(String[] data) {
        return new Subtask(data[1], data[2], Status.valueOf(data[3]), Integer.parseInt(data[4]));
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubtaskById(int subtaskId) {
        super.removeSubtaskById(subtaskId);
        save();
    }
}
