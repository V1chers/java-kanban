package com.practicum.TaskManager.model;

import java.util.*;

/* equals и hashCode вроде уже созданы были в родительском классе и унаследованы. В Subtask слегка изменил их,
   ибо epic id финальное число, но в Epic не хочется их изменять, ибо в subtasks значения могут менятся, соотвественно и id,
   генерируемое для Epic в hashCode, тоже. */

public class Epic extends Task {
    private final Map<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    public List<Subtask> getSubtasks() {
        return Collections.unmodifiableList(new ArrayList<>(subtasks.values()));
    }

    @Override
    public String toString() {
        return "Epic{" +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks.size() +
                '}';
    }

}
