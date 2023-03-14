package tests;

import main.Manager;
import main.historyManager.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.util.enums.Status;
import main.util.enums.TaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager manager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void beforeEach() {
        manager = Manager.getDefaultHistory();
        task = new Task("Task 1", "Descr", 1, String.valueOf(Status.NEW), String.valueOf(TaskType.TASK));
        epic = new Epic("Epic 1", "Descr", 2, String.valueOf(Status.NEW), String.valueOf(TaskType.EPIC));
        subtask = new Subtask("Sub 1", "Sub in Epic 1", 3, String.valueOf(Status.NEW),
                String.valueOf(TaskType.SUBTASK), 2);
    }

    @Test
    void shouldGetEmptyHistoryWithoutThrow() {
        assertDoesNotThrow(() -> manager.getHistory());
    }

    @Test
    void shouldGetHistory() {
        manager.add(task);
        manager.add(epic);
        manager.add(subtask);

        final List<Task> historyList = List.of(task, epic, subtask);

        assertNotEquals(0, manager.getHistory().size(), "История не заполнена.");
        assertEquals(historyList, manager.getHistory(), "Списки не совпадают.");
    }

    @Test
    void shouldAddTasks() {
        manager.add(task);

        assertEquals(1, manager.getHistory().size(), "Задача не добавлена.");
        assertEquals(task, manager.getHistory().get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldAddTasksWithoutDuplication() {
        manager.add(task);
        manager.add(task);
        manager.add(task);

        assertNotEquals(3, manager.getHistory().size(), "История заполнена с дубликатами.");
        IndexOutOfBoundsException exception = assertThrows(IndexOutOfBoundsException.class, () -> manager.getHistory().get(1),
                "История заполнена с дубликатами.");
        assertEquals("Index 1 out of bounds for length 1",exception.getMessage());
    }

    @Test
    void shouldRemoveEmptyTaskWithoutThrow() {
        assertDoesNotThrow(() -> manager.remove(task.getId()));
    }

    @Test
    void shouldRemoveFirstElement() {
        manager.add(task);
        manager.add(epic);
        manager.add(subtask);
        manager.remove(task.getId());

        assertNotEquals(3, manager.getHistory().size(), "Задача не удалена.");
        assertEquals(2, manager.getHistory().size(), "Неверное количество задач.");
        assertNotEquals(task, manager.getHistory().get(0), "Задача не удалена.");
        assertEquals(epic, manager.getHistory().get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldRemoveMiddleElement() {
        manager.add(task);
        manager.add(epic);
        manager.add(subtask);
        manager.remove(epic.getId());

        assertNotEquals(3, manager.getHistory().size(), "Задача не удалена.");
        assertEquals(2, manager.getHistory().size(), "Неверное количество задач.");
        assertNotEquals(epic, manager.getHistory().get(1), "Задача не удалена.");
        assertEquals(subtask, manager.getHistory().get(1), "Задачи не совпадают.");
    }

    @Test
    void shouldRemoveLastElement() {
        manager.add(task);
        manager.add(epic);
        manager.add(subtask);
        manager.remove(subtask.getId());

        assertNotEquals(3, manager.getHistory().size(), "Задача не удалена.");
        assertEquals(2, manager.getHistory().size(), "Неверное количество задач.");
        assertNotEquals(subtask, manager.getHistory().get(0), "Задача не совпадают.");
        assertNotEquals(subtask, manager.getHistory().get(1), "Задачи не совпадают.");
    }
}