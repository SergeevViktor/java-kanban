import manager.Manager;
import manager.server.KVServer;
import manager.taskManagers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.enums.Status;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.Month.FEBRUARY;
import static manager.taskManagers.HttpTaskManager.loadFromServer;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        TaskManager taskManager = Manager.getDefault();


        Task task1 = new Task("Task 1", "Desc 1",
                LocalDateTime.of(2023, FEBRUARY, 19, 19, 9), 60);
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Desc 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 with 1 sub");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 with 2 sub");
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 in Epic 1");
        taskManager.createSub(subtask1, epic1.getId());
        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 19, 21, 10), 540);
        taskManager.createSub(subtask2, epic2.getId());
        Subtask subtask3 = new Subtask("Sub 3", "Sub 3 in Epic 2",
                LocalDateTime.of(2023, FEBRUARY, 21, 7, 50), 200);
        taskManager.createSub(subtask3, epic2.getId());

        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getTask(2));

        System.out.println(taskManager.getEpic(3));
        System.out.println(taskManager.getEpic(4));

        System.out.println(taskManager.getSub(5));
        System.out.println(taskManager.getSub(7));
        Subtask subtask4 = new Subtask("Sub 1", "Sub 1 in Epic 1", 5,
                String.valueOf(Status.DONE), 3);
        taskManager.updateSub(subtask4);
        System.out.println(taskManager.getSub(5));

        System.out.println(taskManager.getHistory());

        System.out.println(taskManager.getPrioritySet());

        System.out.println(loadFromServer("http://localhost:8078").getPrioritySet());


    }
}
