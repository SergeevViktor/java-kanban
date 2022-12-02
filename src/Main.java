import manager.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Desc 1");
        inMemoryTaskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Desc 2");
        inMemoryTaskManager.createTask(task2);
        Task task3 = new Task("Task 3", "Desc 3");
        inMemoryTaskManager.createTask(task3);
        Task task4 = new Task("Task 4", "Desc 4");
        inMemoryTaskManager.createTask(task4);

        Epic epic1 = new Epic("Epic 1", "Epic 1 with 2 sub");
        inMemoryTaskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 with 1 sub");
        inMemoryTaskManager.createEpic(epic2);
        Epic epic3 = new Epic("Epic 3", "Epic 3 without sub");
        inMemoryTaskManager.createEpic(epic3);

        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 in Epic 1");
        inMemoryTaskManager.createSub(subtask1, epic1.getId());
        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 in Epic 1");
        inMemoryTaskManager.createSub(subtask2, epic1.getId());
        Subtask subtask3 = new Subtask("Sub 3", "Sub 3 in Epic 2");
        inMemoryTaskManager.createSub(subtask3, epic2.getId());
        Subtask subtask4 = new Subtask("Sub 4", "Sub 4 in Epic 2");
        inMemoryTaskManager.createSub(subtask4, epic2.getId());

        System.out.println(inMemoryTaskManager.getTask(1));
        System.out.println(inMemoryTaskManager.getTask(2));
        System.out.println(inMemoryTaskManager.getTask(3));
        System.out.println(inMemoryTaskManager.getTask(4));
        System.out.println(inMemoryTaskManager.getEpic(5));
        System.out.println(inMemoryTaskManager.getEpic(6));
        System.out.println(inMemoryTaskManager.getEpic(7));
        System.out.println(inMemoryTaskManager.getSub(8));
        System.out.println(inMemoryTaskManager.getSub(9));
        System.out.println(inMemoryTaskManager.getSub(10));
        System.out.println(inMemoryTaskManager.getSub(11));

        System.out.println(inMemoryTaskManager.getHistory());
    }
}
