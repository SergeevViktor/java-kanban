package tests.taskManagerTests;

import manager.taskManagers.FileBackedTasksManager;
import manager.taskManagers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private final String directory = "./resources/test.csv";

    @Override
    @BeforeEach
    void beforeEach(){
        super.beforeEach();
        try {
            FileChannel.open(Path.of(directory), StandardOpenOption.WRITE)
                    .truncate(0).close();
            manager = new FileBackedTasksManager(directory);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    void shouldLoadFromFileEmptyTasks() {
        InMemoryTaskManager managerAfterEsc = FileBackedTasksManager.loadFromFile(new File(directory));

        assertEquals(manager.getClass(), managerAfterEsc.getClass(), "Менеджер не загружен.");
    }

    @Test
    void shouldLoadFromFileEpicWithoutSub() {
        manager.createEpic(epic);
        manager.getEpic(epic.getId());
        InMemoryTaskManager managerAfterEsc = FileBackedTasksManager.loadFromFile(new File(directory));

        assertEquals(manager.getClass(), managerAfterEsc.getClass(), "Менеджер не загружен.");
        assertEquals(manager.getAllEpic(), managerAfterEsc.getAllEpic());
    }

    @Test
    void shouldLoadFromFileWithoutHistory() {
        manager.createTask(task);
        manager.createEpic(epic);
        manager.createSub(subtask, epic.getId());

        InMemoryTaskManager managerAfterEsc = FileBackedTasksManager.loadFromFile(new File(directory));

        assertEquals(manager.getClass(), managerAfterEsc.getClass(), "Менеджер не загружен.");
        assertEquals(manager.getAllTasks(), managerAfterEsc.getAllTasks());
        assertEquals(manager.getAllEpic(), managerAfterEsc.getAllEpic());
        assertEquals(manager.getAllSub(), managerAfterEsc.getAllSub());
    }
}