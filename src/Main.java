import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import util.Status;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Task 1", "Desc 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Desc 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic 1 with 2 sub");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 with 1 sub");
        taskManager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Sub 1", "Sub 1 in Epic 1");
        taskManager.createSub(subtask1, epic1.getId());
        Subtask subtask2 = new Subtask("Sub 2", "Sub 2 in Epic 1");
        taskManager.createSub(subtask2, epic1.getId());
        Subtask subtask3 = new Subtask("Sub 3", "Sub 3 in Epic 2");
        taskManager.createSub(subtask3, epic2.getId());

        System.out.println("Печать всего:");
        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getTask(task2.getId()));
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getEpic(epic2.getId()));
        System.out.println(taskManager.getSub(subtask1.getId()));
        System.out.println(taskManager.getSub(subtask2.getId()));
        System.out.println(taskManager.getSub(subtask3.getId()));
        System.out.println("Печать садтасков в эпиках:");
        System.out.println(taskManager.getSubInEpic(epic1.getId()));
        System.out.println(taskManager.getSubInEpic(epic2.getId()));

        Task task3 = new Task("Task 1", "Desc 1", 1, Status.DONE);
        taskManager.updateTask(task3);
        Epic epic3 = new Epic("Epic 1", "Epic 1 with 2 sub", 3, Status.DONE);
        taskManager.updateEpic(epic3);
        Subtask subtask4 = new Subtask("Sub 1", "Sub 1 in Epic 1", 5, Status.DONE,
                subtask1.getEpicId());
        taskManager.updateSub(subtask4);
        Subtask subtask5 = new Subtask("Sub 3", "Sub 3 in Epic 2", 7, Status.DONE,
                subtask3.getEpicId());
        taskManager.updateSub(subtask5);

        System.out.println("Проверка изменений:");
        System.out.println(task3);
        System.out.println(subtask4);
        System.out.println(subtask5);
        System.out.println(epic3);
        System.out.println(epic2);

        taskManager.deleteTask(1);
        taskManager.deleteEpic(4);
        taskManager.deleteAllSub();

        System.out.println("Проверка удаления:");
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpic());
        System.out.println(taskManager.getAllSub());
    }
}
