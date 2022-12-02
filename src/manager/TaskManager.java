package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSub(Subtask subtask, int epicId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSub(Subtask subtask);

    Task getTask(int taskId);

    Epic getEpic(int epicId);

    Subtask getSub(int subId);

    HashMap<Integer, Subtask> getSubInEpic(int epicId);

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Epic> getAllEpic();

    HashMap<Integer, Subtask> getAllSub();

    void deleteTask(int taskId);

    void deleteEpic(int epicId);

    void deleteSubtask(int subId);

    void deleteAllTasks();

    void deleteAllEpic();

    void deleteAllSub();

    int getNextId();

    List<Task> getHistory();
}
