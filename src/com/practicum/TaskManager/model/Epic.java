package com.practicum.TaskManager.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final Map<Integer, Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateTimeAndDuration(subtask);
    }

    public void removeSubtask(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        subtasks.remove(subtaskId);
        updateTimeAndDuration(subtask);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        super.setDuration(null);
        super.setStartTime(null);
    }

    public List<Subtask> getSubtasks() {
        return Collections.unmodifiableList(new ArrayList<>(subtasks.values()));
    }

    private void updateTimeAndDuration(Subtask subtask) {
        if (subtask.getEndTime().isEmpty()) {
            return;
        }

        if (endTime == null) {
            super.setDuration(subtask.getDuration());
            super.setStartTime(subtask.getStartTime());
            endTime = subtask.getEndTime().get();
            return;
        }
        super.setDuration(super.getDuration().plus(subtask.getDuration()));
        if (subtask.getStartTime().isBefore(super.getStartTime())) {
            super.setStartTime(subtask.getStartTime());
        }
        if (subtask.getEndTime().get().isAfter(super.getEndTime().get())) {
            endTime = subtask.getEndTime().get();
        }
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
    }

    @Override
    public void setDuration(Duration duration) {
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
