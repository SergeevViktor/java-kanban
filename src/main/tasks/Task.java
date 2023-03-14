package main.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private String type;
    private String name;
    private String description;
    private int id;
    private String status;
    private String startTime;
    private String duration;
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy/HH:mm");

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime.format(formatter);
        this.duration = String.valueOf(Duration.ofMinutes(duration).toMinutes());
    }

    public Task(String name, String description, int id, String status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String name, String description, int id, String status, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime.format(formatter);
        this.duration = String.valueOf(Duration.ofMinutes(duration).toMinutes());
    }

    public Task(String name, String description, int id, String status, String type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;
    }

    public Task(String name, String description, int id, String status, String type, LocalDateTime startTime, long duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;
        this.startTime = startTime.format(formatter);
        this.duration = String.valueOf(Duration.ofMinutes(duration).toMinutes());
    }

    @Override
    public String toString() {
        return id +
                "," + type +
                "," + name +
                "," + status +
                "," + description +
                "," + startTime +
                "," + duration
                ;
    }

    public String getEndTime() {
        LocalDateTime endTime = LocalDateTime.parse(startTime, formatter).plusMinutes(Long.parseLong(duration));
        return endTime.format(formatter);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime.format(formatter);
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = String.valueOf(Duration.ofMinutes(duration).toMinutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(type, task.type) && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && Objects.equals(status, task.status)
                && Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, description, id, status, startTime, duration);
    }
}