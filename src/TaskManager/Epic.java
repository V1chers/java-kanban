package TaskManager;

import java.util.HashMap;

public class Epic extends Task {
    final private HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    void addSubtask(int subtaskId, Subtask subtask) {
        subtasks.put(subtaskId, subtask);
    }

    void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks.size() +
                '}';
    }
}
