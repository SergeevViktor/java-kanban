import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> sub = new HashMap<>();

    int id = 1;
    String NEW = "NEW";
    String IN_PROGRESS = "IN_PROGRESS";
    String DONE = "DONE";

    public void createTask(Task task) {
        tasks.put(id, task);
        tasks.get(id).setStatus(NEW);
        tasks.get(id).setId(id++);
    }

    public void createEpic(Epic epic) {
        epics.put(id, epic);
        epics.get(id).setStatus(NEW);
        epics.get(id).setId(id++);
    }

    public void createSub(Subtask subtask, int epicId) {
        sub.put(id, subtask);
        sub.get(id).setStatus(NEW);
        sub.get(id).setId(id);
        sub.get(id).setEpicId(epicId);
        epics.get(epicId).subInEpic.put(id++, subtask);
    }

    /*public void updateTask(Task task, int taskId, String status) {
        tasks.put(task.getId(), task));
    }*/

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
        }
        sub.clear();
    }
}
