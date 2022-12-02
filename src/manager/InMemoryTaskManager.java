package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    HistoryManager historyManager = Managers.getDefaultHistory();

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int id = 0;

    @Override
    public void createTask(Task task) {
        tasks.put(getNextId(), task);
        task.setStatus(String.valueOf(Status.NEW));
        task.setId(id);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(getNextId(), epic);
        epic.setStatus(String.valueOf(Status.NEW));
        epic.setId(id);
    }

    @Override
    public void createSub(Subtask subtask, int epicId) {
        subtasks.put(getNextId(), subtask);
        subtask.setStatus(String.valueOf(Status.NEW));
        subtask.setId(id);
        subtask.setEpicId(epicId);
        epics.get(epicId).getSubInEpic().put(id, subtask);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epic.setSubInEpic(epics.get(epic.getId()).getSubInEpic());
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
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
            return String.valueOf(Status.NEW);
        } else if (statusDone == useObject.getSubInEpic().size()) {
            return String.valueOf((Status.DONE));
        } else {
            return String.valueOf(Status.IN_PROGRESS);
        }
    }

    @Override
    public Task getTask(int taskId) {
        if (InMemoryHistoryManager.callHistory.size() == 10) {
            InMemoryHistoryManager.callHistory.remove(0);
            historyManager.add(tasks.get(taskId));
        } else {
            historyManager.add(tasks.get(taskId));
        }
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        if (InMemoryHistoryManager.callHistory.size() == 10) {
            InMemoryHistoryManager.callHistory.remove(0);
            historyManager.add(epics.get(epicId));
        } else {
            historyManager.add(epics.get(epicId));
        }
        return epics.get(epicId);
    }

    @Override
    public Subtask getSub(int subId) {
        if (InMemoryHistoryManager.callHistory.size() == 10) {
            InMemoryHistoryManager.callHistory.remove(0);
            historyManager.add(subtasks.get(subId));
        } else {
            historyManager.add(subtasks.get(subId));
        }
        return subtasks.get(subId);
    }

    @Override
    public HashMap<Integer, Subtask> getSubInEpic(int epicId) {
        return epics.get(epicId).getSubInEpic();
    }

    @Override
    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getAllEpic() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getAllSub() {
        return subtasks;
    }


    @Override
    public void deleteTask(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        for (Integer subId : epics.get(epicId).getSubInEpic().keySet()) {
            subtasks.remove(subId);
        }
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subId) {
        epics.get(subtasks.get(subId).getEpicId()).getSubInEpic().remove(subId);
        epics.get(subtasks.get(subId).getEpicId()).setStatus(updateStatus(subtasks.get(subId)));
        subtasks.remove(subId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
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

    @Override
    public void deleteAllSub() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).getSubInEpic().clear();
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
        }
        subtasks.clear();
    }

    @Override
    public int getNextId() {
        id++;
        return id;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
