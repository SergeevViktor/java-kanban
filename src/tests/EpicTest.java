package tests;

import manager.Manager;
import manager.taskManagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import util.Status;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private final TaskManager manager = Manager.getDefault();

    private Epic epic;
    private Subtask subtask;
    private Subtask subtask2;


    @BeforeEach
    void beforeEach() {
        epic = new Epic("Epic 1", "Descr");
        subtask = new Subtask("Sub 1", "Sub in Epic 1");
        subtask2 = new Subtask("Sub 2", "Sub in Epic 1");
    }

    @Test
    void shouldReturnEpicStatusNewWithNewSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, 1);
        manager.createSub(subtask2, 1);

        assertEquals(String.valueOf(Status.NEW), epic.getStatus(), "Статус присвоен некорректно.");
    }

    @Test
    void shouldReturnEpicStatusNewWithoutSub() {
        manager.createEpic(epic);

        assertEquals(String.valueOf(Status.NEW), epic.getStatus(), "Статус присвоен некорректно.");
    }

    @Test
    void shouldUpdateStatusEpicOnInProgressWithOneOfTwoDoneSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, 1);
        manager.createSub(subtask2, 1);
        Subtask updateSub = new Subtask("Sub 1", "Sub in Epic 1", 2,
                String.valueOf(Status.DONE), 1);
        manager.updateSub(updateSub);

        assertEquals(String.valueOf(Status.IN_PROGRESS), epic.getStatus(), "Статус обновился некорректно.");
    }

    @Test
    void shouldUpdateStatusEpicInProgressWithInProgressSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, 1);
        Subtask updateSub = new Subtask("Sub 1", "Sub in Epic 1", 2,
                String.valueOf(Status.IN_PROGRESS), 1);
        manager.updateSub(updateSub);

        assertEquals(String.valueOf(Status.IN_PROGRESS), epic.getStatus(), "Статус обновился некорректно.");
    }

    @Test
    void shouldUpdateStatusEpicOnDoneWithOneDoneSub() {
        manager.createEpic(epic);
        manager.createSub(subtask, 1);
        Subtask updateSub = new Subtask("Sub 1", "Sub in Epic 1", 2,
                String.valueOf(Status.DONE), 1);
        manager.updateSub(updateSub);

        assertEquals(String.valueOf(Status.DONE), epic.getStatus(), "Статус обновился некорректно.");
    }
}