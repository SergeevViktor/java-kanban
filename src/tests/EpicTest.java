package tests;

import main.Manager;
import main.taskManagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.util.enums.Status;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private final TaskManager manager = Manager.getInMemoryTaskManager();

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

    @Test
    void shouldGetEndTime() {
        manager.createEpic(epic);
        manager.createSub(subtask, 1);
        Subtask updateSub = new Subtask("Sub 1", "Sub in Epic 1", 2, String.valueOf(Status.DONE),
                1, LocalDateTime.of(2023, Month.FEBRUARY, 22, 2, 15), 240);
        manager.updateSub(updateSub);

        assertEquals("22.02.2023/02:15", epic.getStartTime(), "Время начала обновилось некорректно.");
        assertEquals("22.02.2023/06:15", epic.getEndTime(), "Итоговое время обновилось некорректно.");
    }
}