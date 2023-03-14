package tests.taskManagerTests;

import main.server.KVServer;
import main.taskManagers.HttpTaskManager;
import main.taskManagers.InMemoryTaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final String url = "http://localhost:8078";
    private static KVServer server;

    @BeforeAll
    static void start() throws IOException {
        server = new KVServer();
        server.start();
    }

    @Override
    @BeforeEach
    void beforeEach() {
        super.beforeEach();
        manager = new HttpTaskManager(url);
    }

    @Test
    void shouldSave() {
        manager.createTask(task);
        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        manager.createEpic(epic);
        httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        int epicId = epic.getId();
        subtask.setStartTime(LocalDateTime.of(2023,3,14,15,10));
        subtask.setDuration(60);
        manager.createSub(subtask, epicId);
        httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        manager.getTask(task.getId());
        httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        manager.getEpic(epic.getId());
        httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        manager.deleteTask(task.getId());
        httpTaskManager = HttpTaskManager.loadFromServer(url);
        assertNotNull(httpTaskManager, "Состояние не сохранено.");
        assertEquals(manager.getClass(), httpTaskManager.getClass(), "Менеджеры не совпадают.");

        assertEquals(manager.getHistory(), httpTaskManager.getHistory(), "История не совпадает.");
        assertEquals(manager.getPrioritySet(), httpTaskManager.getPrioritySet(), "Список не совпадает.");
    }

    @Test
    void shouldLoadFromServerEmpty() {
        //тесты пересекаются с save()
        HttpTaskManager httpTaskManager = HttpTaskManager.loadFromServer(url);

        assertNotNull(httpTaskManager, "Менеджер не загружается.");
    }

    @AfterAll
    static void stop() {
        server.stop();
    }
}