package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;
import util.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected int id = 0;

    @Override
    public void createTask(Task task) {
        tasks.put(getNextId(), task);
        task.setStatus(String.valueOf(Status.NEW));
        task.setType(String.valueOf(TaskType.TASK));
        task.setId(id);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(getNextId(), epic);
        epic.setStatus(String.valueOf(Status.NEW));
        epic.setType(String.valueOf(TaskType.EPIC));
        epic.setId(id);
    }

    @Override
    public void createSub(Subtask subtask, int epicId) {
        subtasks.put(getNextId(), subtask);
        subtask.setStatus(String.valueOf(Status.NEW));
        subtask.setType(String.valueOf(TaskType.SUBTASK));
        subtask.setId(id);
        subtask.setEpicId(epicId);
        epics.get(epicId).getSubInEpic().put(id, subtask);
    }

    @Override
    public void updateTask(Task task) {
        task.setType(String.valueOf(TaskType.TASK));
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setType(String.valueOf(TaskType.EPIC));
        if (epics.containsKey(epic.getId())) {
            epic.setSubInEpic(epics.get(epic.getId()).getSubInEpic());
            epic.setStatus(epics.get(epic.getId()).getStatus());
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSub(Subtask subtask) {
        subtask.setType(String.valueOf(TaskType.SUBTASK));
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).getSubInEpic().put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
        }
    }


    protected String updateStatus(Subtask subtask) {
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
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpic(int epicId) {
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public Subtask getSub(int subId) {
        historyManager.add(subtasks.get(subId));
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
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteEpic(int epicId) {
        for (Integer subId : epics.get(epicId).getSubInEpic().keySet()) {
            historyManager.remove(subId);
            subtasks.remove(subId);
        }
        historyManager.remove(epicId);
        epics.remove(epicId);
    }

    @Override
    public void deleteSubtask(int subId) {
        epics.get(subtasks.get(subId).getEpicId()).getSubInEpic().remove(subId);
        epics.get(subtasks.get(subId).getEpicId()).setStatus(updateStatus(subtasks.get(subId)));
        historyManager.remove(subId);
        subtasks.remove(subId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
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
            historyManager.remove(object);
            subtasks.remove(object);
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    @Override
    public void deleteAllSub() {
        for (Subtask subtask : subtasks.values()) {
            epics.get(subtask.getEpicId()).getSubInEpic().clear();
            epics.get(subtask.getEpicId()).setStatus(updateStatus(subtask));
            historyManager.remove(subtask.getId());
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
