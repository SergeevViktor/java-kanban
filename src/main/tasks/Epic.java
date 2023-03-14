package main.tasks;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {

    private String endTime;
    private HashMap<Integer, Subtask> subInEpic = new HashMap<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, String status) {
        super(name, description, id, status);
    }

    public Epic(String name, String description, int id, String status, String type) {
        super(name, description, id, status, type);
    }

    public HashMap<Integer, Subtask> getSubInEpic() {
        return subInEpic;
    }

    public void setSubInEpic(HashMap<Integer, Subtask> subInEpic) {
        this.subInEpic = subInEpic;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime.format(formatter);
    }

    @Override
    public String toString() {
        return getId() +
                "," + getType() +
                "," + getName() +
                "," + getStatus() +
                "," + getDescription() +
                "," + getStartTime() +
                "," + getDuration()
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(endTime, epic.endTime) && Objects.equals(subInEpic, epic.subInEpic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endTime, subInEpic);
    }
}