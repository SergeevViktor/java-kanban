package main.tasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, LocalDateTime startTime, long duration) {
        super(name, description, startTime, duration);
    }

    public Subtask(String name, String description, int id, String status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, String status, int epicId, LocalDateTime startTime, long duration) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, String status, String type, int epicId) {
        super(name, description, id, status, type);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int id, String status, String type, int epicId,
                   LocalDateTime startTime, long duration) {
        super(name, description, id, status, type, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return getId() +
                "," + getType() +
                "," + getName() +
                "," + getStatus() +
                "," + getDescription() +
                "," + getStartTime() +
                "," + getDuration() +
                "," + epicId
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}