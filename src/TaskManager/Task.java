package TaskManager;

import java.util.Objects;

public class Task {
    final private String name;
    final private String description;
    final private int id;
    private Status status;

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = hashCode();
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                ", name='" + name + '\'' +
                ", description='" + description.length() + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
