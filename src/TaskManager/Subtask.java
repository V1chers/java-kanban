package TaskManager;

public class Subtask extends Task {
    final private int epicId;

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription().length() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
