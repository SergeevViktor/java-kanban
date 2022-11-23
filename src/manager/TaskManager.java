package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> sub = new HashMap<>();

    int id = 1;

    public void createTask(Task task) {
        tasks.put(id, task);
        tasks.get(id).setStatus(Status.NEW);
        tasks.get(id).setId(id++);
    }

    public void createEpic(Epic epic) {
        epics.put(id, epic);
        epics.get(id).setStatus(Status.NEW);
        epics.get(id).setId(id++);
    }

    public void createSub(Subtask subtask, int epicId) {
        sub.put(id, subtask);
        sub.get(id).setStatus(Status.NEW);
        sub.get(id).setId(id);
        sub.get(id).setEpicId(epicId);
        epics.get(epicId).subInEpic.put(id++, subtask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epic.subInEpic = epics.get(epic.getId()).subInEpic;
        epic.setStatus(epics.get(epic.getId()).getStatus());
        epics.put(epic.getId(), epic);
    }

    public void updateSub(Subtask subtask) {
        sub.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).subInEpic.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
    }

    private String updateStatus(Subtask subtask) {
        int statusNew = 0;
        int statusDone = 0;

        for (Subtask object : epics.get(subtask.getEpicId()).subInEpic.values()) {
            if (object.getStatus().equals("NEW")) {
                statusNew++;
            } else if (object.getStatus().equals("DONE")) {
                statusDone++;
            }
        }
        if (statusNew == epics.get(subtask.getEpicId()).subInEpic.size()
                || epics.get(subtask.getEpicId()).subInEpic == null) {
            return Status.NEW;
        } else if (statusDone == epics.get(subtask.getEpicId()).subInEpic.size()) {
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
        return sub.get(subId);
    }

    public HashMap<Integer, Subtask> getSubInEpic(int epicId) {
        return epics.get(epicId).subInEpic;
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSub() {
        return sub;
    }

    public void deleteTasks(int taskId) {
        tasks.remove(taskId);
    }

    public void deleteEpic(int epicId) {
        for (Integer subId : epics.get(epicId).subInEpic.keySet()) {
            sub.remove(subId);
        }
        epics.get(epicId).subInEpic.clear();
        epics.remove(epicId);
    }

    public void deleteSub(int subId) {
        epics.get(sub.get(subId).getEpicId()).subInEpic.remove(subId);
        epics.get(sub.get(subId).getEpicId()).setStatus(updateStatus(sub.get(subId)));
        sub.remove(subId);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpic() {
        ArrayList<Integer> objectId = new ArrayList<>();
        for (Integer epicId : epics.keySet()) {
            for (Subtask subtask : sub.values()) {
                if (subtask.getEpicId() == epicId) {
                    objectId.add(subtask.getId());
                }
            }
        }
        for (Integer object : objectId) {
            sub.remove(object);
        }
        epics.clear();
    }

    public void deleteAllSub() {
        for (Subtask subtask : sub.values()) {
            epics.get(subtask.getEpicId()).subInEpic.clear();
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
        }
        sub.clear();
    }
}
