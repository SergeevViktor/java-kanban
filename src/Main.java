import manager.Manager;
import manager.taskManagers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;

import static java.time.Month.FEBRUARY;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Manager.getDefault();
        Task task1 = new Task("Task 1", "Desc 1",
                LocalDateTime.of(2023, FEBRUARY, 19, 19, 9), 60);
        manager.createTask(task1);
        Task task2 = new Task("Task 2", "Desc 2");
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 with 1 sub");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 with 2 sub");
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 in Epic 1");
        manager.createSub(subtask1, epic1.getId());
        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 19, 21, 10), 540);
        manager.createSub(subtask2, epic2.getId());
        Subtask subtask3 = new Subtask("Sub 3", "Sub 3 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 21, 7, 50), 200);
        manager.createSub(subtask3, epic2.getId());

        System.out.println(manager.getPrioritySet());
    }
}
