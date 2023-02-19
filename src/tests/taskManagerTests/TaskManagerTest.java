package tests.taskManagerTests;

import manager.taskManagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;
import util.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected Task task;
    protected Task task2;
    protected Epic epic;
    protected Epic epic2;
    protected Subtask subtask;
    protected Subtask subtask2;
    protected Subtask subtask3;

    @BeforeEach
    void beforeEach() {
        task = new Task("Task 1", "Descr");
        task2 = new Task("Task 2", "Descr");
        epic = new Epic("Epic 1", "Descr");
        epic2 = new Epic("Epic 2", "Descr");
        subtask = new Subtask("Sub 1", "Sub in Epic 1");
        subtask2 = new Subtask("Sub 2", "Sub in Epic 1");
        subtask3 = new Subtask("Sub 3", "Sub in Epic 1");
    }

    @Test
    void shouldCreateTask() {
        manager.createTask(task);
        assertEquals(String.valueOf(Status.NEW), task.getStatus(), "Статус задачи не NEW.");

        final Task savedTask = manager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<Integer, Task> tasks = manager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateEpic() {
        manager.createEpic(epic);
        assertEquals(String.valueOf(Status.NEW), epic.getStatus(), "Статус задачи не NEW.");

        final Epic savedEpic = manager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> epics = manager.getAllEpic();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void shouldCreateSubtask() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        assertEquals(String.valueOf(Status.NEW), subtask.getStatus(), "Статус задачи не NEW.");

        final Subtask savedSub = manager.getSub(subtask.getId());

        assertNotNull(savedSub, "Задача не найдена.");
        assertEquals(subtask, savedSub, "Задачи не совпадают.");

        final Map<Integer, Subtask> subtasks = manager.getAllSub();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(2), "Задачи не совпадают.");
        assertEquals(1, subtask.getEpicId(), "Задаче присвоен неверный Epic-ID.");
    }

    @Test
    void shouldUpdateTask() {
        manager.createTask(task);
        final Task oldTask = manager.getTask(task.getId());

        manager.updateTask(new Task("Task 1", "Descr", 1, String.valueOf(Status.DONE)));
        final Task savedTask = manager.getTask(1);

        assertEquals(String.valueOf(Status.DONE), savedTask.getStatus(), "Статус задачи не DONE.");
        assertNotEquals(savedTask, oldTask, "Обновление не произошло.");
    }

    @Test
    void shouldUpdateTaskWithEmptyTasksWithoutThrow() {
        final Task updateTask = new Task("Task", "Descr", 1 ,String.valueOf(Status.DONE));

        assertDoesNotThrow(() -> manager.updateTask(updateTask));
    }

    @Test
    void shouldUpdateTaskWithEmptyTaskIdWithoutThrow() {
        manager.createTask(task);
        final Task updateTask = new Task("Task", "Descr", 2 ,String.valueOf(Status.DONE));

        assertDoesNotThrow(() -> manager.updateTask(updateTask));
    }

    @Test
    void shouldUpdateEpic() {
        manager.createEpic(epic);
        final Epic oldEpic = manager.getEpic(epic.getId());

        manager.updateEpic(new Epic("Epic 1.1", "Descr", 1, String.valueOf(Status.DONE)));
        final Epic savedEpic = manager.getEpic(1);

        assertEquals(String.valueOf(Status.NEW), savedEpic.getStatus(), "Произошло зменение статуса.");
        assertNotEquals(savedEpic, oldEpic, "Обновление не произошло.");
    }

    @Test
    void shouldUpdateEpicWithEmptyEpicsWithoutThrow() {
        final Epic updateEpic = new Epic("Epic 1", "Descr", 1, String.valueOf(Status.DONE));

        assertDoesNotThrow(() -> manager.updateEpic(updateEpic));
    }

    @Test
    void shouldUpdateEpicWithEmptyEpicIdWithoutThrow() {
        manager.createEpic(epic);
        final Epic updateEpic = new Epic("Epic 1.1", "Descr", 2 ,String.valueOf(Status.DONE));

        assertDoesNotThrow(() -> manager.updateTask(updateEpic));
    }

    @Test
    void shouldUpdateSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        final Subtask oldSub = manager.getSub(subtask.getId());

        manager.updateSub(new Subtask("Sub 1.1", "Descr", 2, String.valueOf(Status.DONE), 1));
        final Subtask savedSub = manager.getSub(2);

        assertEquals(String.valueOf(Status.DONE), savedSub.getStatus(), "Статус задачи не DONE.");
        assertNotEquals(savedSub, oldSub, "Обновление не произошло.");
        assertEquals(savedSub, epic.getSubInEpic().get(2), "Sub в Epic не изменился.");
    }

    @Test
    void shouldUpdateSubWithEmptySubsWithoutThrow() {
        manager.createEpic(epic);
        final Subtask updateSub = new Subtask("Sub 1", "Descr", 2,
                String.valueOf(Status.DONE), 1);

        assertDoesNotThrow(() -> manager.updateSub(updateSub));
    }

    @Test
    void shouldUpdateSubWithEmptySubIdWithoutThrow() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        final Subtask updateSub = new Subtask("Sub 1.1", "Descr", 3,
                String.valueOf(Status.DONE), 1);

        assertDoesNotThrow(() -> manager.updateSub(updateSub));
    }

    @Test
    void shouldGetTask() {
        manager.createTask(task);

        assertEquals(new Task("Task 1", "Descr", 1, String.valueOf(Status.NEW),
                String.valueOf(TaskType.TASK)), manager.getTask(1), "Задачи не совпадают.");
        assertDoesNotThrow(() -> manager.getTask(1));
    }

    @Test
    void shouldGetEmptyTaskWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.getTask(1));
    }

    @Test
    void shouldGetEpic() {
        manager.createEpic(epic);

        assertEquals(new Epic("Epic 1", "Descr", 1, String.valueOf(Status.NEW),
                String.valueOf(TaskType.EPIC)), manager.getEpic(1), "Задачи не совпадают.");
        assertDoesNotThrow(() -> manager.getEpic(1));
    }

    @Test
    void shouldGetEmptyEpicWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.getEpic(1));
    }

    @Test
    void shouldGetSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());

        assertEquals(new Subtask("Sub 1", "Sub in Epic 1", 2, String.valueOf(Status.NEW),
                String.valueOf(TaskType.SUBTASK), 1), manager.getSub(2), "Задачи не совпадают.");
        assertDoesNotThrow(() -> manager.getSub(2));
    }

    @Test
    void shouldGetEmptySubWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.getSub(1));
    }

    @Test
    void shouldGetSubInEpic() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());

        final Map<Integer, Subtask> savedSubs = new HashMap<>();
        savedSubs.put(subtask.getId(), subtask);

        assertEquals(savedSubs, manager.getSubInEpic(epic.getId()), "Задачи не совпадают.");
    }

    @Test
    void shouldGetSubInEmptyEpicWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.getSubInEpic(1));
    }

    @Test
    void shouldGetAllTasks() {
        manager.createTask(task);

        final Map<Integer, Task> savedTasks = new HashMap<>();
        savedTasks.put(task.getId(), task);

        assertEquals(savedTasks, manager.getAllTasks(), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEmptyTasksWithoutThrows() {
        assertDoesNotThrow(() -> manager.getAllTasks());
    }

    @Test
    void shouldGetAllEpic() {
        manager.createEpic(epic);

        final Map<Integer, Epic> savedEpics = new HashMap<>();
        savedEpics.put(epic.getId(), epic);

        assertEquals(savedEpics, manager.getAllEpic(), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEmptyEpicWithoutThrows() {
        assertDoesNotThrow(() -> manager.getAllEpic());
    }

    @Test
    void shouldGetAllSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());

        final Map<Integer, Subtask> savedSubs = new HashMap<>();
        savedSubs.put(subtask.getId(), subtask);

        assertEquals(savedSubs, manager.getAllSub(), "Задачи не совпадают.");
    }

    @Test
    void shouldGetEmptySubWithoutThrows() {
        assertDoesNotThrow(() -> manager.getAllSub());
    }

    @Test
    void shouldDeleteTask() {
        manager.createTask(task);
        manager.createTask(task2);
        final int id = task.getId();
        manager.deleteTask(task.getId());

        final Map<Integer, Task> tasks = manager.getAllTasks();

        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertNotEquals(task, tasks.get(id), "Задача не удалена.");
    }

    @Test
    void shouldDeleteEmptyTaskWithoutThrow() {
        assertDoesNotThrow(() -> manager.deleteTask(1));
    }

    @Test
    void shouldDeleteEpic() {
        manager.createEpic(epic);
        manager.createEpic(epic2);
        manager.createSub(subtask, epic.getId());
        final int id = epic.getId();
        manager.deleteEpic(epic.getId());

        final Map<Integer, Epic> epics = manager.getAllEpic();
        final Map<Integer, Subtask> subs = manager.getAllSub();

        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(0, subs.size(), "Подзадачи не удалены.");
        assertNotEquals(epic, epics.get(id), "Задача не удалена.");
    }

    @Test
    void shouldDeleteEmptyEpicWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.deleteEpic(1));
    }

    @Test
    void shouldDeleteSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        manager.createSub(subtask2, epic.getId());
        final int id = subtask.getId();
        manager.deleteSubtask(subtask.getId());

        final Map<Integer, Subtask> subs = manager.getAllSub();

        assertEquals(1, subs.size(), "Неверное количество задач.");
        assertEquals(1, epic.getSubInEpic().size(), "Подзадачи из Epic не удалены.");
        assertNotEquals(subtask, subs.get(id), "Задача не удалена.");
    }

    @Test
    void shouldDeleteEmptySubWithNullPointerException() {
        assertThrows(NullPointerException.class, () -> manager.deleteSubtask(1));
    }

    @Test
    void shouldDeleteAllTasks() {
        manager.createTask(task);
        manager.deleteAllTasks();

        final Map<Integer, Task> tasks = manager.getAllTasks();

        assertEquals(0, tasks.size(), "Задачи не удалены.");
    }

    @Test
    void shouldDeleteAllTasksWithoutThrow() {
        assertDoesNotThrow(() -> manager.deleteAllTasks());
    }

    @Test
    void shouldDeleteAllEpic() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        manager.deleteAllEpic();

        final Map<Integer, Epic> epics = manager.getAllEpic();
        final Map<Integer, Subtask> subs = manager.getAllSub();

        assertEquals(0, epics.size(), "Задачи не удалены.");
        assertEquals(0, subs.size(), "Подзадачи из Epic не удалены.");
    }

    @Test
    void shouldDeleteAllEpicWithoutThrow() {
        assertDoesNotThrow(() -> manager.deleteAllEpic());
    }

    @Test
    void shouldDeleteAllSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());
        manager.createEpic(epic2);
        manager.createSub(subtask2, epic2.getId());
        manager.deleteAllSub();

        final Map<Integer, Subtask> subs = manager.getAllSub();

        assertEquals(0, subs.size(), "Задачи не удалены.");
        assertEquals(0, epic2.getSubInEpic().size(), "Подзадачи из Epic не удалены.");
    }

    @Test
    void shouldDeleteAllSubWithoutThrow() {
        assertDoesNotThrow(() -> manager.deleteAllSub());
    }

    @Test
    void shouldGetNextId() {
        manager.createTask(task);
        final int id = manager.getNextId();
        assertEquals(2, id, "ID расчитывается неправильно.");
    }

    @Test
    void shouldGetHistory() {
        manager.createTask(task);
        manager.createTask(task2);

        final List<Task> history = new ArrayList<>();
        history.add(manager.getTask(task.getId()));
        history.add(manager.getTask(task2.getId()));

        assertEquals(history, manager.getHistory(), "История сохранена неправильно.");
    }

    @Test
    void shouldGetEmptyHistoryWithoutThrow() {
        assertDoesNotThrow(() -> manager.getHistory());
    }
}