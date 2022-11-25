package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int id = 0;

    public void createTask(Task task) {
        tasks.put(getNextId(), task);
        task.setStatus(Status.NEW);
        task.setId(id);
    }

    public void createEpic(Epic epic) {
        epics.put(getNextId(), epic);
        epic.setStatus(Status.NEW);
        epic.setId(id);
    }

    public void createSub(Subtask subtask, int epicId) {
        subtasks.put(getNextId(), subtask);
        subtask.setStatus(Status.NEW);
        subtask.setId(id);
        subtask.setEpicId(epicId);
        epics.get(epicId).getSubInEpic().put(id, subtask);
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setSubInEpic(epics.get(epic.getId()).getSubInEpic());
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
        }
    }

    public void updateSub(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).getSubInEpic().put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
        }
    }

    private String updateStatus(Subtask subtask) {
        int statusNew = 0;
        int statusDone = 0;
        Epic useObject = epics.get(subtask.getEpicId());

        for (Subtask object : useObject.getSubInEpic().values()) {
            if (object.getStatus().equals("NEW")) {
                statusNew++;
            } else if (object.getStatus().equals("DONE")) {
                statusDone++;
            }
        }
        if (statusNew == useObject.getSubInEpic().size()) {
            return Status.NEW;
        } else if (statusDone == useObject.getSubInEpic().size()) {
            return (Status.DONE);
        } else {
            return Status.IN_PROGRESS;
        }
    }

    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSub(int subId) {
        return subtasks.get(subId);
    }

    public HashMap<Integer, Subtask> getSubInEpic(int epicId) {
        return epics.get(epicId).getSubInEpic();
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSub() {
        return subtasks;
    }

    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        for (Integer subId : epics.get(epicId).getSubInEpic().keySet()) {
            subtasks.remove(subId);
        }
        epics.remove(epicId);
    }

    public void deleteSubtask(int subId) {
        epics.get(subtasks.get(subId).getEpicId()).getSubInEpic().remove(subId);
        epics.get(subtasks.get(subId).getEpicId()).setStatus(updateStatus(subtasks.get(subId)));
        subtasks.remove(subId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        ArrayList<Integer> objectId = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epicId) {
                    objectId.add(subtask.getId());
                }
            }
        }
        for (Integer object : objectId) {
            subtasks.remove(object);
        }
        epics.clear();
    }

    public void deleteAllSub() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).getSubInEpic().clear();
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
        }
        subtasks.clear();
    }

    public int getNextId() {
        id++;
        return id;
    }
}
